package com.jigsusolver.sudoku.imageproc.extractors

import com.jigsusolver.sudoku.models.regions.MutableSudokuRegions
import com.jigsusolver.sudoku.models.regions.SudokuRegions
import com.jigsusolver.sudoku.utils.CellsUtils
import com.jigsusolver.sudoku.utils.Utils
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.opencv.core.Mat

class SudokuRegionsExtractor(
    private val sudokuSize: Int
) {
    fun extractSudokuRegions(regionsMaskImage: Mat): SudokuRegions {
        val sudokuRegions = MutableSudokuRegions(sudokuSize)
        val (anchors, _) = CellsUtils.extractCellsCentralAnchors(regionsMaskImage, sudokuSize)

        for (i in 0 until sudokuSize) {
            for (j in 0 until sudokuSize) {
                val pixelColor = regionsMaskImage.get(anchors[i, j, 0], anchors[i, j, 1])
                val pixelInt = Utils.color2int(pixelColor)
                sudokuRegions[i, j] = pixelInt
            }
        }

        return sudokuRegions
    }
}