package com.jigsusolver.sudoku.utils

import com.jigsusolver.sudoku.models.regions.SudokuRegions
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D3Array
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.Rect
import org.opencv.core.Scalar
import org.opencv.imgproc.Imgproc

class CellsUtils {
    companion object {
        private const val CELL_BORDER_WIDTH_RATIO = 0.04

        fun extractCellsCentralAnchors(
            image: Mat,
            size: Int,
            borderWidthRatio: Double = CELL_BORDER_WIDTH_RATIO
        ): Pair<D3Array<Int>, Int> {
            val anchors = mk.zeros<Int>(size, size,2)
            val totalSize = image.cols()
            val borderSize = ((totalSize * borderWidthRatio) / size).toInt()
            val cellSize = (totalSize - (borderSize * (size + 1))) / size

            for (i in 0 until size) {
                for (j in 0 until size) {
                    val anchorRow = borderSize + (cellSize / 2) + (i * (cellSize + borderSize))
                    val anchorCol = borderSize + (cellSize / 2) + (j * (cellSize + borderSize))
                    anchors[i, j, 0] = anchorRow
                    anchors[i, j, 1] = anchorCol
                }
            }

            return Pair(anchors, cellSize)
        }

        fun generateRandom(cellsSize: Int = 100, sudokuSize: Int): List<List<Mat>> {
            val cellImages = Utils.generateSquareList(sudokuSize) { Mat() }

            for (i in 0 until sudokuSize) {
                for (j in 0 until sudokuSize) {
                    val img = Mat(cellsSize, cellsSize, CvType.CV_8UC3)
                    val color = Utils.randColor()
                    Imgproc.rectangle(img, Rect(0, 0, cellsSize, cellsSize), Scalar(color), Imgproc.FILLED)
                    cellImages[i][j] = img
                }
            }

            return cellImages
        }

        fun generateFromSudokuRegions(sudokuRegions: SudokuRegions, cellsSize: Int = 100): List<List<Mat>> {
            val sudokuSize = sudokuRegions.sudokuSize
            val cellImages = Utils.generateSquareList(sudokuSize) { Mat() }

            val colorsMap: MutableMap<Int, Scalar> = HashMap()
            for (i in 0 until sudokuSize) {
                for (j in 0 until sudokuSize) {
                    val v = sudokuRegions[i, j]
                    val img = Mat(cellsSize, cellsSize, CvType.CV_8UC3)
                    if (!colorsMap.containsKey(v)) {
                        val color = Utils.randColor()
                        colorsMap[v] = Scalar(color)
                    }
                    val color = colorsMap[v]
                    Imgproc.rectangle(img, Rect(0, 0, cellsSize, cellsSize), color, Imgproc.FILLED)
                    cellImages[i][j] = img
                }
            }

            return cellImages
        }

        fun merge(
            cellImages: List<List<Mat>>,
            borderSize: Int = 3,
            borderColor: Scalar = Scalar(0.0, 0.0, 0.0)
        ): Mat {
            val sudokuSize = cellImages.size
            val cvType = cellImages[0][0].type()
            val cellSize = Utils.maxOfWidthAndHeight(cellImages.flatten())
            val totalSize = cellSize * sudokuSize + borderSize * (sudokuSize + 1)
            val mergedImage = Mat(totalSize, totalSize, cvType, borderColor)

            for (i in 0 until sudokuSize) {
                for (j in 0 until sudokuSize) {
                    val width = cellImages[i][j].width()
                    val height = cellImages[i][j].height()
                    val rowOffset = borderSize + i * (cellSize + borderSize)
                    val colOffset = borderSize + j * (cellSize + borderSize)

                    // Define the ROI (Region of Interest)
                    val roi = mergedImage.submat(
                        rowOffset, rowOffset + height,
                        colOffset, colOffset + width
                    )

                    cellImages[i][j].copyTo(roi)
                }
            }

            return mergedImage
        }
    }
}