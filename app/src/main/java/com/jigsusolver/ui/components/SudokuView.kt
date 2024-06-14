package com.jigsusolver.ui.components

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.content.ContextCompat
import com.jigsusolver.R
import com.jigsusolver.sudoku.dataproc.regions.optimizers.DfsRegionsOptimizer
import com.jigsusolver.sudoku.dataproc.sudoku.validators.SudokuValidator
import com.jigsusolver.sudoku.models.sudoku.MutableSudoku
import com.jigsusolver.sudoku.models.sudoku.Sudoku
import com.jigsusolver.sudoku.utils.Utils
import com.jigsusolver.ui.data.GridAction

class SudokuView @JvmOverloads constructor(
    private val context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    var gridActionState = GridAction.MOVE
    private lateinit var sudoku: MutableSudoku
    private val cellPaint = Paint()
    private val boldPaint = Paint()
    private val textPaint = Paint()
    private val textErrorPaint = Paint()
    private val pressedCellPaint = Paint()
    private val regionErrorPaint = Paint()
    private var scaleGestureDetector: ScaleGestureDetector
    private var scaleFactor = 1.0f
    private var lastTouchX: Float = 0.toFloat()
    private var lastTouchY: Float = 0.toFloat()
    private var pressedCell: Pair<Int, Int>? = null
    private var lastRegionsCell: Pair<Int, Int>? = null
    private var currentDrawingRegionCell: Pair<Int, Int>? = null
    private var isValidation: Boolean = false

    init {
        cellPaint.style = Paint.Style.STROKE
        cellPaint.color = ContextCompat.getColor(context, R.color.colorGrid)
        cellPaint.strokeWidth = 1f

        boldPaint.style = Paint.Style.STROKE
        boldPaint.color = ContextCompat.getColor(context, R.color.colorRegions)
        boldPaint.strokeWidth = 10f

        textPaint.color = ContextCompat.getColor(context, R.color.colorText)
        textPaint.textAlign = Paint.Align.CENTER

        textErrorPaint.color = ContextCompat.getColor(context, R.color.colorTextError)
        textErrorPaint.textAlign = Paint.Align.CENTER

        pressedCellPaint.style = Paint.Style.FILL
        pressedCellPaint.color = ContextCompat.getColor(context, R.color.colorButtonPressed)

        regionErrorPaint.style = Paint.Style.FILL
        regionErrorPaint.color = ContextCompat.getColor(context, R.color.regionError)

        scaleGestureDetector = ScaleGestureDetector(context, ScaleListener())
    }

    fun setSudoku(sudoku: Sudoku) {
        this.sudoku = MutableSudoku(sudoku)
        isValidation = false
        pressedCell = null
        invalidate()
    }

    fun getSudoku(): Sudoku {
        return sudoku
    }

    fun sendChar(char: Char) {
        if (gridActionState != GridAction.CELLS || pressedCell == null) {
            return
        }
        val currentLabel = sudoku.values[pressedCell!!.first][pressedCell!!.second]
        if (sudoku.labels.labels.any { it.startsWith(currentLabel + char) }) {
            isValidation = false
            sudoku.values[pressedCell!!.first][pressedCell!!.second] = currentLabel + char
        }
        invalidate()
    }

    fun validate() {
        isValidation = true
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        canvas.drawColor(resources.getColor(R.color.colorButton, context.theme))
        sudoku.let {
            val centerX = width / 2f
            val centerY = height / 2f

            canvas.save()
            canvas.translate(centerX, centerY)
            canvas.scale(scaleFactor, scaleFactor)
            canvas.translate(-centerX, -centerY)

            drawGrid(canvas, it)
            drawRegions(canvas, it)
            drawValues(canvas, it)
            canvas.restore()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)

        if (gridActionState != GridAction.REGIONS) {
            lastRegionsCell = null
            currentDrawingRegionCell = null
        }

        when (gridActionState) {
            GridAction.REGIONS -> {
                if (event.action == MotionEvent.ACTION_UP) {
                    lastRegionsCell = null
                    currentDrawingRegionCell = null
                    pressedCell = null
                    invalidate()
                    return true
                }

                val (eventRow, eventCol) = calculateEventCell(event.x.toInt(), event.y.toInt())
                pressedCell = Pair(eventRow, eventCol)
                if (event.action != MotionEvent.ACTION_MOVE) {
                    invalidate()
                    return true
                }

                if (lastRegionsCell == null || lastRegionsCell !in Utils.adjacentCells(eventRow, eventCol, sudoku.size)) {
                    lastRegionsCell = pressedCell
                    invalidate()
                    return true
                }
                isValidation = false

                val (lastRegionsRow, lastRegionsCol) = lastRegionsCell!!
                if (currentDrawingRegionCell == null) {
                    val lastRegion = sudoku.regions[lastRegionsRow, lastRegionsCol]
                    if (sudoku.regions[eventRow, eventCol] != lastRegion) {
                        currentDrawingRegionCell = lastRegionsCell
                    } else {
                        currentDrawingRegionCell = lastRegionsCell
                        sudoku.regions[lastRegionsRow, lastRegionsCol] = (sudoku.regions.max() ?: 0) + 1
                    }
                }

                lastRegionsCell = pressedCell
                val drawRegion = sudoku.regions[currentDrawingRegionCell!!.first, currentDrawingRegionCell!!.second]
                sudoku.regions[eventRow, eventCol] = drawRegion

                val optimizedRegions = DfsRegionsOptimizer().optimizeRegions(sudoku.regions)
                for (row in 0 until sudoku.regions.sudokuSize) {
                    for (col in 0 until sudoku.regions.sudokuSize) {
                        sudoku.regions[row, col] = optimizedRegions[row, col]
                    }
                }

                invalidate()
            }
            GridAction.MOVE -> {
                val dx = event.x - lastTouchX
                val dy = event.y - lastTouchY
                lastTouchX = event.x
                lastTouchY = event.y

                // Calculate the boundaries
                val scaledWidth = width * scaleFactor
                val scaledHeight = height * scaleFactor

                val maxX = (scaledWidth - width) / 2
                val maxY = (scaledHeight - height) / 2
                val minX = -maxX
                val minY = -maxY

                // Apply boundaries
                val newScrollX = (scrollX - dx).coerceIn(minX, maxX)
                val newScrollY = (scrollY - dy).coerceIn(minY, maxY)
                scrollTo(newScrollX.toInt(), newScrollY.toInt())
            }
            GridAction.CELLS -> {
                pressedCell = calculateEventCell(event.x.toInt(), event.y.toInt())
                if (sudoku.values[pressedCell!!.first][pressedCell!!.second] != "") {
                    isValidation = false
                }
                sudoku.values[pressedCell!!.first][pressedCell!!.second] = ""
                invalidate()
            }
        }

        return true
    }

    private fun drawGrid(canvas: Canvas, sudoku: Sudoku) {
        val sudokuSize = sudoku.size
        val cellSize = width / sudokuSize.toFloat()

        val errorRegions = SudokuValidator().extractErrorRegions(sudoku)
        for (row in 0 until sudokuSize) {
            for (col in 0 until sudokuSize) {
                val startX = col * cellSize
                val startY = row * cellSize

                if (sudoku.regions[row, col] in errorRegions) {
                    canvas.drawRect(startX, startY, startX + cellSize, startY + cellSize, regionErrorPaint)
                }

                canvas.drawRect(startX, startY, startX + cellSize, startY + cellSize, cellPaint)
            }
        }

        pressedCell?.let { (row, col) ->
            val startX = col * cellSize
            val startY = row * cellSize
            canvas.drawRect(startX, startY, startX + cellSize, startY + cellSize, pressedCellPaint)
        }
    }

    private fun drawRegions(canvas: Canvas, sudoku: Sudoku) {
        val sudokuSize = sudoku.size
        val cellSize = width / sudokuSize.toFloat()
        val regions = sudoku.regions

        for (row in 0 until sudokuSize) {
            for (col in 0 until sudokuSize) {
                val startX = col * cellSize
                val startY = row * cellSize
                val endX = (col + 1) * cellSize
                val endY = (row + 1) * cellSize
                val d = boldPaint.strokeWidth / 2
                val region = regions[row, col]
                val (_, rightRegion, bottomRegion, _) = regions.cellAdjacencyByDirections(row, col)

                if (row == 0) {
                    canvas.drawLine(startX, startY, endX, startY, boldPaint)
                }
                if (col == 0) {
                    canvas.drawLine(startX, startY, startX, endY, boldPaint)
                }

                if (rightRegion != region) {
                    canvas.drawLine(endX, startY - d, endX, endY + d, boldPaint)
                }
                if (bottomRegion != region) {
                    canvas.drawLine(startX - d, endY, endX + d, endY, boldPaint)
                }
            }
        }
    }

    private fun drawValues(canvas: Canvas, sudoku: Sudoku) {
        val sudokuSize = sudoku.size
        val cellSize = width / sudokuSize.toFloat()
        val values = sudoku.values

        textPaint.textSize = (cellSize / sudoku.labels.maxLength * 1.4f).coerceAtMost(cellSize * 0.8f)
        textErrorPaint.textSize = (cellSize / sudoku.labels.maxLength * 1.4f).coerceAtMost(cellSize * 0.8f)

        val errorValues = if (isValidation) {
            SudokuValidator().extractErrorValues(sudoku)
        } else {
            null
        }
        for (row in 0 until sudokuSize) {
            for (col in 0 until sudokuSize) {
                val value = values[row][col]
                if (value.isNotEmpty()) {
                    val x = (col * cellSize) + (cellSize / 2)
                    val y = (row * cellSize) + (cellSize / 2) - ((textPaint.descent() + textPaint.ascent()) / 2)

                    if (isValidation && errorValues != null && Pair(row, col) in errorValues) {
                        canvas.drawText(value, x, y, textErrorPaint)
                    } else {
                        canvas.drawText(value, x, y, textPaint)
                    }
                }
            }
        }
    }

    private fun calculateEventCell(eventX: Int, eventY: Int): Pair<Int, Int> {
        val cellSize = width / sudoku.size.toFloat()

        val hiddenRow = ((height * (1 - 1 / scaleFactor) * scaleFactor / 2 - scrollY) / 2) / cellSize / scaleFactor * 2
        val displayedRow = (sudoku.size - sudoku.size / scaleFactor * eventY / (height / scaleFactor)) / scaleFactor
        var row = (sudoku.size - hiddenRow - displayedRow).toInt()

        val hiddenCol = ((width * (1 - 1/scaleFactor) * scaleFactor / 2 - scrollX) / 2) / cellSize / scaleFactor * 2
        val displayedCol = (sudoku.size - sudoku.size / scaleFactor * eventX / (width / scaleFactor)) / scaleFactor
        var col = (sudoku.size - hiddenCol - displayedCol).toInt()

        row = row.coerceIn(0, sudoku.size - 1)
        col = col.coerceIn(0, sudoku.size - 1)

        return Pair(row, col)
    }

    private inner class ScaleListener : ScaleGestureDetector.SimpleOnScaleGestureListener() {
        override fun onScale(detector: ScaleGestureDetector): Boolean {
            if (gridActionState != GridAction.MOVE) {
                return false
            }
            scaleFactor *= detector.scaleFactor
            scaleFactor = 1.0f.coerceAtLeast(scaleFactor.coerceAtMost(2.0f))
            invalidate()
            return true
        }
    }
}