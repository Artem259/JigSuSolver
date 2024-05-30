package com.jigsusolver.ui.components

import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.jigsusolver.R
import com.jigsusolver.sudoku.SudokuProcessor
import com.jigsusolver.sudoku.dataproc.regions.optimizers.SudokuRegionsOptimizer
import com.jigsusolver.sudoku.dataproc.sudoku.solvers.SudokuSolver
import com.jigsusolver.sudoku.models.SudokuInfo
import com.jigsusolver.sudoku.models.SudokuLabels
import com.jigsusolver.sudoku.models.SudokuType
import com.jigsusolver.sudoku.models.regions.SudokuRegions
import com.jigsusolver.sudoku.models.sudoku.Sudoku
import com.jigsusolver.sudoku.utils.Utils
import com.jigsusolver.ui.data.GridAction
import org.opencv.android.OpenCVLoader
import org.opencv.core.Size
import org.opencv.imgcodecs.Imgcodecs
import org.opencv.imgproc.Imgproc
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var sudokuGridView: SudokuView
    private lateinit var sudokuProcessor: SudokuProcessor
    private lateinit var resetButton: Button
    private lateinit var validateButton: Button
    private lateinit var solveButton: Button
    private lateinit var gridMoveButton: Button
    private lateinit var gridRegionsButton: Button
    private lateinit var gridCellsButton: Button
    private lateinit var scanImageButton: Button
    private lateinit var sudokuTypeSpinner: Spinner
    private lateinit var sudokuSizeSpinner: Spinner
    private lateinit var sudokuLabelsSpinner: Spinner
    private var gridActionState = GridAction.MOVE
    private lateinit var sudokuSpinnersInfo: SudokuInfo
    private lateinit var sudokuLabelsCandidates: MutableList<SudokuLabels>

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = contentResolver.openInputStream(uri)
            inputStream?.use { stream ->
                val tempFile = File.createTempFile("tempImage", ".png")
                tempFile.outputStream().use { output ->
                    stream.copyTo(output)
                }
                val image = Imgcodecs.imread(tempFile.absolutePath)
                Imgproc.resize(image, image, Size(0.0, 0.0),
                    2.75, 2.75, Imgproc.INTER_LINEAR)
                sudokuProcessor = SudokuProcessor(image, sudokuSpinnersInfo, this)
                sudokuGridView.setSudoku(sudokuProcessor.unsolvedSudoku)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        OpenCVLoader.initLocal()

        sudokuGridView = findViewById(R.id.sudokuGridView)
        resetButton = findViewById(R.id.resetButton)
        validateButton = findViewById(R.id.validateButton)
        solveButton = findViewById(R.id.solveButton)
        gridMoveButton = findViewById(R.id.gridMoveButton)
        gridRegionsButton = findViewById(R.id.gridRegionsButton)
        gridCellsButton = findViewById(R.id.gridCellsButton)
        scanImageButton = findViewById(R.id.scanImageButton)
        sudokuTypeSpinner = findViewById(R.id.sudokuTypeSpinner)
        sudokuSizeSpinner = findViewById(R.id.sudokuSizeSpinner)
        sudokuLabelsSpinner = findViewById(R.id.sudokuLabelsSpinner)

        resetButton.setOnClickListener { resetButtonClicked() }
        validateButton.setOnClickListener { validateButtonClicked() }
        solveButton.setOnClickListener { solveButtonClicked() }

        gridMoveButton.setOnClickListener { gridMoveButtonClicked() }
        gridRegionsButton.setOnClickListener { gridRegionsButtonClicked() }
        gridCellsButton.setOnClickListener { gridCellsButtonClicked() }

        scanImageButton.setOnClickListener { scanImage() }

        val buttonIds = arrayOf(
            R.id.button0, R.id.button1, R.id.button2, R.id.button3,
            R.id.button4, R.id.button5, R.id.button6, R.id.button7,
            R.id.button8, R.id.button9, R.id.buttonA, R.id.buttonB,
            R.id.buttonC, R.id.buttonD, R.id.buttonE, R.id.buttonF
        )
        val downResource = R.drawable.button_down
        val upResource = R.drawable.button_up
        for (buttonId in buttonIds) {
            findViewById<Button>(buttonId).setOnTouchListener { v, event ->
                val char = (v as Button).text.toString()[0]
                if (event.action == MotionEvent.ACTION_DOWN) {
                    v.setBackgroundResource(downResource)
                    sudokuGridView.sendChar(char)
                    true
                } else if (event.action == MotionEvent.ACTION_UP) {
                    v.setBackgroundResource(upResource)
                    true
                }
                true
            }
        }

        sudokuTypeSpinner.onItemSelectedListener = SudokuTypeSpinnerListener()
        sudokuSizeSpinner.onItemSelectedListener = SudokuSizeSpinnerListener()
        sudokuLabelsSpinner.onItemSelectedListener = SudokuLabelsSpinnerListener()

        sudokuTypeSpinnerSetup()
        gridActionStateUpdate()
        sudokuGridViewReset()
    }

    private fun resetButtonClicked() {
        sudokuGridView.setSudoku(Sudoku(
            Utils.generateSquareList(sudokuSpinnersInfo.size) { "" },
            SudokuRegionsOptimizer(sudokuSpinnersInfo.type).optimizeRegions(SudokuRegions(sudokuSpinnersInfo.size)),
            sudokuSpinnersInfo.type,
            sudokuSpinnersInfo.labels
        ))
    }

    private fun validateButtonClicked() {
        sudokuGridView.validate()
    }

    private fun solveButtonClicked() {
        try {
            val solvedSudoku = SudokuSolver().solve(sudokuGridView.getSudoku()) ?: throw Exception()
            sudokuGridView.setSudoku(solvedSudoku)
        } catch (e: Exception) {
            validateButtonClicked()
        }
    }

    private fun gridMoveButtonClicked() {
        gridActionState = GridAction.MOVE
        gridActionStateUpdate()
    }

    private fun gridRegionsButtonClicked() {
        gridActionState = GridAction.REGIONS
        gridActionStateUpdate()
    }

    private fun gridCellsButtonClicked() {
        gridActionState = GridAction.CELLS
        gridActionStateUpdate()
    }

    private fun gridActionStateUpdate() {
        val downResource = R.drawable.button_down
        val upResource = R.drawable.button_up
        gridMoveButton.setBackgroundResource(upResource)
        gridRegionsButton.setBackgroundResource(upResource)
        gridCellsButton.setBackgroundResource(upResource)
        sudokuGridView.gridActionState = gridActionState

        when (gridActionState) {
            GridAction.MOVE -> {
                gridMoveButton.setBackgroundResource(downResource)
            }
            GridAction.REGIONS -> {
                gridRegionsButton.setBackgroundResource(downResource)
            }
            GridAction.CELLS -> {
                gridCellsButton.setBackgroundResource(downResource)
            }
        }
    }

    private fun sudokuTypeSpinnerSetup() {
        val items = resources.getStringArray(R.array.sudoku_types)
        val adapter = ArrayAdapter(this, R.layout.spinner_layout, items)
        sudokuTypeSpinner.adapter = adapter
    }

    private fun sudokuSizeSpinnerUpdate(sudokuType: SudokuType) {
        val items = when (sudokuType) {
            SudokuType.CLASSIC -> {
                resources.getStringArray(R.array.classic_sudoku_sizes)
            }
            SudokuType.JIGSAW -> {
                resources.getStringArray(R.array.jigsaw_sudoku_sizes)
            }
        }
        val adapter = ArrayAdapter(this, R.layout.spinner_layout, items)
        sudokuSizeSpinner.adapter = adapter
    }

    private fun sudokuLabelsSpinnerUpdate(sudokuSize: Int) {
        sudokuLabelsCandidates = mutableListOf()

        val lastChar = 'A' + sudokuSize - 10
        val items = if (sudokuSize <= 9) {
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(1..sudokuSize))
            arrayListOf("1–$sudokuSize")
        } else if (sudokuSize == 10) {
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(1..sudokuSize))
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(0..<sudokuSize))
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(('1'..'9') + ('A'..lastChar)))
            arrayListOf("1–$sudokuSize", "0–${sudokuSize - 1}", "1–$lastChar")
        } else if (sudokuSize in 11..15) {
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(1..sudokuSize))
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(0..<sudokuSize))
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(('1'..'9') + ('A'..lastChar)))
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(('0'..'9') + ('A'..<lastChar)))
            arrayListOf("1–$sudokuSize", "0–${sudokuSize - 1}", "1–$lastChar", "0–${lastChar - 1}")
        } else {
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(1..sudokuSize))
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(0..<sudokuSize))
            sudokuLabelsCandidates.add(SudokuLabels.fromCustomList(('0'..'9') + ('A'..<lastChar)))
            arrayListOf("1–$sudokuSize", "0–${sudokuSize - 1}", "0–${lastChar - 1}")
        }

        val adapter = ArrayAdapter(this, R.layout.spinner_layout, items)
        sudokuLabelsSpinner.adapter = adapter
    }

    private fun sudokuGridViewReset() {
        sudokuGridView.setSudoku(Sudoku(
            Utils.generateSquareList(9) { "" },
            SudokuRegionsOptimizer(SudokuType.CLASSIC).optimizeRegions(SudokuRegions(9)),
            SudokuType.CLASSIC,
            SudokuLabels.from1to9()
        ))
    }

    private fun scanImage() {
        getContent.launch("image/*")
    }

    private inner class SudokuTypeSpinnerListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            val selectedItem = parent.getItemAtPosition(position).toString()
            when (selectedItem) {
                "Classic" -> {
                    sudokuSpinnersInfo = SudokuInfo(SudokuType.CLASSIC, -1, SudokuLabels.from1to9())
                    sudokuSizeSpinnerUpdate(SudokuType.CLASSIC)
                }
                "Jigsaw" -> {
                    sudokuSpinnersInfo = SudokuInfo(SudokuType.JIGSAW, -1, SudokuLabels.from1to9())
                    sudokuSizeSpinnerUpdate(SudokuType.JIGSAW)
                }
            }
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            sudokuTypeSpinnerSetup()
        }
    }

    private inner class SudokuSizeSpinnerListener : AdapterView.OnItemSelectedListener {
        private val regex = Regex("""(\d+)x\d+""")

        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            val selectedItem = parent.getItemAtPosition(position).toString()
            val regexResult = regex.find(selectedItem)
            val sudokuSize = regexResult?.groups?.get(1)?.value?.toInt()!!
            sudokuSpinnersInfo = sudokuSpinnersInfo.copy(size = sudokuSize)
            sudokuLabelsSpinnerUpdate(sudokuSize)
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            sudokuTypeSpinnerSetup()
        }
    }

    private inner class SudokuLabelsSpinnerListener : AdapterView.OnItemSelectedListener {
        override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
            sudokuSpinnersInfo = sudokuSpinnersInfo.copy(labels = sudokuLabelsCandidates[position])
        }

        override fun onNothingSelected(parent: AdapterView<*>) {
            sudokuTypeSpinnerSetup()
        }
    }
}