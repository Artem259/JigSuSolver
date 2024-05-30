package com.jigsusolver.sudoku.imageproc.extractors

import com.jigsusolver.sudoku.utils.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Rect
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.min

class CharImagesExtractor(
    private val maxLength: Int
) {
    fun extractCharImages(cellImages: List<List<Mat>>): List<List<List<Mat>>> {
        val sudokuSize = cellImages.size
        val charImages = Utils.generateSquareList(sudokuSize) { listOf<Mat>() }

        for (i in 0 until sudokuSize) {
            for (j in 0 until sudokuSize) {
                charImages[i][j] = processCellImage(cellImages[i][j])
            }
        }

        return charImages
    }

    private fun processCellImage(cellImage: Mat): List<Mat> {
        val cellSize = min(cellImage.width(), cellImage.height())
        val width = cellImage.width()
        val height = cellImage.height()

        val image = Mat()
        Imgproc.cvtColor(cellImage, image, Imgproc.COLOR_BGR2GRAY)

        var ksize = cellSize * 0.05
        ksize += (1 - ksize % 2)
        Imgproc.GaussianBlur(image, image, Size(ksize, ksize), 0.0)

        // Thresholding to obtain binary image
        val binaryImage = Mat()
        Imgproc.threshold(image, binaryImage, 0.0, 255.0,
            Imgproc.THRESH_BINARY_INV + Imgproc.THRESH_OTSU
        )

        // Find contours in the binary image
        var contours: List<MatOfPoint> = ArrayList()
        val hierarchy = Mat()
        Imgproc.findContours(binaryImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        contours = contours.sortedByDescending { Imgproc.contourArea(it) }

        val charImages = ArrayList<Mat>()
        val charPositions = ArrayList<Int>()
        for (i in contours.indices) {
            val contour = contours[i]
            val boundingRect = Imgproc.boundingRect(contour)
            val x1 = boundingRect.x
            val y1 = boundingRect.y
            val w = boundingRect.width
            val h = boundingRect.height
            val x2 = x1 + w - 1
            val y2 = y1 + h - 1

            val posAbsThreshold = 1.0 // TODO const x3
            if (!positionAbsThreshold(x1, y1, width, height, posAbsThreshold) || !positionAbsThreshold(x2, y2, width, height, posAbsThreshold)) {
                continue
            }

            // val bothSizeRatio = 0.05
            // if (!sizeThreshold(w, width, bothSizeRatio) || !sizeThreshold(h, height, bothSizeRatio)) {
            //    continue
            // }

             val widthSizeRatio = 0.05
             if (!sizeThreshold(w, width, widthSizeRatio)) {
                continue
             }

            val heightSizeRatio = 0.3
            if (!sizeThreshold(h, height, heightSizeRatio)) {
                continue
            }

            val charImage = Mat(cellImage, Rect(x1, y1, w, h))
            charImages.add(charImage)
            charPositions.add(x1)
            if (charImages.size == maxLength) {
                break
            }
        }
        return charImages.zip(charPositions).sortedBy { it.second }.map { it.first }
    }

    private fun positionRelThreshold(x: Int, y: Int, imageW: Int, imageH: Int, threshold: Double): Boolean {
        return (x > imageW * threshold && x < imageW * (1 - threshold))
                && (y > imageH * threshold && y < imageH * (1 - threshold))
    }

    private fun positionAbsThreshold(x: Int, y: Int, imageW: Int, imageH: Int, threshold: Double): Boolean {
        return (x > threshold && x < imageW - threshold)
                && (y > threshold && y < imageH - threshold)
    }

    private fun sizeThreshold(charSize: Int, imageSize: Int, threshold: Double): Boolean {
        return (charSize > imageSize * threshold)
    }
}