package com.jigsusolver.sudoku

import android.content.Context
import com.jigsusolver.sudoku.dataproc.regions.optimizers.SudokuRegionsOptimizer
import com.jigsusolver.sudoku.dataproc.sudoku.solvers.SudokuSolver
import com.jigsusolver.sudoku.imageproc.classification.CharImagesPreprocessor
import com.jigsusolver.sudoku.imageproc.extractors.CellImagesExtractor
import com.jigsusolver.sudoku.imageproc.extractors.CharImagesExtractor
import com.jigsusolver.sudoku.imageproc.extractors.CharValuesExtractor
import com.jigsusolver.sudoku.imageproc.extractors.FieldImageExtractor
import com.jigsusolver.sudoku.imageproc.extractors.FieldRectExtractor
import com.jigsusolver.sudoku.imageproc.extractors.RegionsMaskImageExtractor
import com.jigsusolver.sudoku.imageproc.extractors.SudokuRegionsExtractor
import com.jigsusolver.sudoku.models.SudokuInfo
import com.jigsusolver.sudoku.models.regions.SudokuRegions
import com.jigsusolver.sudoku.models.sudoku.Sudoku
import org.opencv.core.Mat

class SudokuProcessor(var image: Mat, private var sudokuInfo: SudokuInfo, private val context: Context) {
    private var _fieldRect: Mat? = null
    private var _fieldImage: Mat? = null
    private var _regionsMaskImage: Mat? = null
    private var _sudokuRegions: SudokuRegions? = null
    private var _optimizedSudokuRegions: SudokuRegions? = null
    private var _cellImages: List<List<Mat>>? = null
    private var _charImages: List<List<List<Mat>>>? = null
    private var _preprocessedCharImages: List<List<List<Mat>>>? = null
    private var _charValues: List<List<String>>? = null
    private var _unsolvedSudoku: Sudoku? = null
    private var _solvedSudoku: Sudoku? = null
    private var isSudokuSolved: Boolean = false

    val fieldRect: Mat
        get() = _fieldRect ?: FieldRectExtractor().extractFieldRect(image).also { _fieldRect = it }

    val fieldImage: Mat
        get() = _fieldImage ?: FieldImageExtractor().extractFieldImage(image, fieldRect).also { _fieldImage = it }

    val regionsMaskImage: Mat
        get() = _regionsMaskImage ?: RegionsMaskImageExtractor(sudokuInfo.size).extractRegionsMaskImage(fieldImage).also { _regionsMaskImage = it }

    val sudokuRegions: SudokuRegions
        get() = _sudokuRegions ?: SudokuRegionsExtractor(sudokuInfo.size).extractSudokuRegions(regionsMaskImage).also { _sudokuRegions = it }

    val optimizedSudokuRegions: SudokuRegions
        get() = _optimizedSudokuRegions ?: SudokuRegionsOptimizer(sudokuInfo.type).optimizeRegions(sudokuRegions).also { _optimizedSudokuRegions = it }

    val cellImages: List<List<Mat>>
        get() = _cellImages ?: CellImagesExtractor(sudokuInfo.size).extractCellImages(fieldImage).also { _cellImages = it }

    val charImages: List<List<List<Mat>>>
        get() = _charImages ?: CharImagesExtractor(sudokuInfo.labels.maxLength).extractCharImages(cellImages).also { _charImages = it }

    val preprocessedCharImages: List<List<List<Mat>>>
        get() = _preprocessedCharImages ?: CharImagesPreprocessor().preprocessCharImages(charImages).also { _preprocessedCharImages = it }

    val charValues: List<List<String>>
        get() = _charValues ?: CharValuesExtractor(sudokuInfo.labels, context).extractCharValues(preprocessedCharImages).also { _charValues = it }

    val unsolvedSudoku: Sudoku
        get() = _unsolvedSudoku ?: Sudoku(charValues, optimizedSudokuRegions, sudokuInfo.type, sudokuInfo.labels).also { _unsolvedSudoku = it }

    val solvedSudoku: Sudoku?
        get() =  if (isSudokuSolved) _solvedSudoku else SudokuSolver().solve(unsolvedSudoku).also { _solvedSudoku = it }

    fun newImage(image: Mat) {
        this.image = image
        clear()
    }

    private fun clear() {
        _fieldRect = null
        _fieldImage = null
        _regionsMaskImage = null
        _sudokuRegions = null
        _optimizedSudokuRegions = null
        _cellImages = null
        _charImages = null
        _preprocessedCharImages = null
        _charValues = null
        _unsolvedSudoku = null
        _solvedSudoku = null
        isSudokuSolved = false
    }
}