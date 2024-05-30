package com.jigsusolver.sudoku.imageproc.classification

import com.jigsusolver.sudoku.utils.Utils
import org.opencv.core.Mat
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc

class CharImagesPreprocessor {
    fun preprocessCharImages(charImages: List<List<List<Mat>>>): List<List<List<Mat>>> {
        val sudokuSize = charImages.size
        val preprocessedCharImages = Utils.generateSquareList(sudokuSize) { mutableListOf<Mat>() }

        for (i in 0 until sudokuSize) {
            for (j in 0 until sudokuSize) {
                for (charImage in charImages[i][j]) {
                    val image = processCharImage(charImage)
                    preprocessedCharImages[i][j].add(image)
                }
            }
        }

        return preprocessedCharImages
    }

    private fun processCharImage(charImage: Mat): Mat {
        var image = Mat()
        Imgproc.cvtColor(charImage, image, Imgproc.COLOR_BGR2GRAY)

        Imgproc.GaussianBlur(image, image, Size(3.0, 3.0), 0.0)

        Imgproc.threshold(image, image, 0.0, 255.0,
            Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU
        )

        image = makeSquare(image)
        image = addPadding(image, 0.2)

        Imgproc.resize(image, image, Size(28.0, 28.0)) // TODO const

        Imgproc.cvtColor(image, image, Imgproc.COLOR_GRAY2RGB)
        return image
    }

    private fun makeSquare(image: Mat): Mat {
        val maxSize = maxOf(image.rows(), image.cols())

        // Create a black canvas
        val squareImage = Mat.zeros(maxSize, maxSize, image.type())

        // Calculate the offset for the image
        val xOffset = (maxSize - image.cols()) / 2
        val yOffset = (maxSize - image.rows()) / 2

        // Copy the image to the center of the canvas
        val roi = squareImage.submat(yOffset, yOffset + image.rows(), xOffset, xOffset + image.cols())
        image.copyTo(roi)

        return squareImage
    }

    private fun addPadding(image: Mat, paddingRatio: Double): Mat {
        val rows = image.rows()
        val cols = image.cols()

        // Calculate the padding size
        val paddingRows = (rows * paddingRatio).toInt()
        val paddingCols = (cols * paddingRatio).toInt()

        // Create a black canvas with padding
        val paddedImage = Mat.zeros(rows + 2 * paddingRows, cols + 2 * paddingCols, image.type())

        // Calculate the offset for the image
        val xOffset = paddingCols
        val yOffset = paddingRows

        // Copy the image to the center of the canvas
        val roi = paddedImage.submat(yOffset, yOffset + rows, xOffset, xOffset + cols)
        image.copyTo(roi)

        return paddedImage
    }
}