package com.jigsusolver.sudoku.utils

import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Scalar

class CharsUtils {
    companion object {
        fun mergeToCells(
            charImages: List<List<List<Mat>>>,
            borderSize: Int = 3,
            borderColor: Scalar = Scalar(0.0, 0.0, 0.0)
        ): List<List<Mat>> {
            val sudokuSize = charImages.size
            val cellImages = Utils.generateSquareList(sudokuSize) { Mat() }

            for (row in 0 until sudokuSize) {
                for (col in 0 until sudokuSize) {
                    val cellChars = charImages[row][col]
                    val size = cellChars.size

                    if (size == 0) {
                        cellImages[row][col] = Mat(0, 0, CvType.CV_8UC3)
                        continue
                    }

                    val totalHeight = Utils.maxOfHeight(cellChars)
                    val totalWidth = Utils.sumOfWidth(cellChars) + borderSize * (size - 1)
                    val mergedImage = Mat(totalHeight, totalWidth, CvType.CV_8UC3, borderColor)

                    var colOffset = 0
                    for (i in 0 until size) {
                        val width = cellChars[i].width()
                        val height = cellChars[i].height()

                        // Define the ROI (Region of Interest)
                        val roi = mergedImage.submat(
                            0, height,
                            colOffset, colOffset + width
                        )

                        cellChars[i].copyTo(roi)
                        colOffset += width + borderSize
                    }

                    cellImages[row][col] = mergedImage
                }
            }

            return cellImages
        }
    }
}