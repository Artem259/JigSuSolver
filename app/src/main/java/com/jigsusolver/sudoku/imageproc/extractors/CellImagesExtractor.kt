package com.jigsusolver.sudoku.imageproc.extractors

import com.jigsusolver.sudoku.utils.CellsUtils
import com.jigsusolver.sudoku.utils.Utils
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.opencv.core.Mat

class CellImagesExtractor(
    private val sudokuSize: Int
) {
    fun extractCellImages(fieldImage: Mat): List<List<Mat>> {
        require(fieldImage.rows() == fieldImage.cols()) {"fieldImage must be square"}

        val cellImages = Utils.generateSquareList(sudokuSize) { Mat() }

        val (anchors, cellsSize) = CellsUtils.extractCellsCentralAnchors(fieldImage, sudokuSize)
        for (i in 0 until sudokuSize) {
            for (j in 0 until sudokuSize) {
                val rowOffset = anchors[i, j, 0] - (cellsSize / 2)
                val colOffset = anchors[i, j, 1] - (cellsSize / 2)

                // Define the ROI (Region of Interest)
                val roi = fieldImage.submat(
                    rowOffset, rowOffset + cellsSize,
                    colOffset, colOffset + cellsSize
                )

                roi.copyTo(cellImages[i][j])
                roi.release()
            }
        }

        return cellImages
    }
}