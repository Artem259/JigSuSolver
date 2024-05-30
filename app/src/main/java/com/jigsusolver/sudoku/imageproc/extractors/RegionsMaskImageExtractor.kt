package com.jigsusolver.sudoku.imageproc.extractors

import com.jigsusolver.sudoku.utils.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.Point
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.min

class RegionsMaskImageExtractor(
    private val sudokuSize: Int
) {
    fun extractRegionsMaskImage(fieldImage: Mat): Mat {
        // Calculate cell size and region threshold
        val cellSize = min(fieldImage.width(), fieldImage.height()) / sudokuSize
        val regionThreshold = cellSize * cellSize * sudokuSize * 0.5

        // Convert the input image to grayscale
        val grayImage = Mat()
        Imgproc.cvtColor(fieldImage, grayImage, Imgproc.COLOR_BGR2GRAY)

        // Apply median blur to reduce noise
        var ksize = (cellSize * 0.05).toInt()
        ksize += (1 - ksize % 2)
        Imgproc.medianBlur(grayImage, grayImage, ksize)

        // Apply Canny edge detector with adaptive threshold
        val cannyAccThresh = Imgproc.threshold(
            grayImage, Mat(), 0.0, 255.0,
            Imgproc.THRESH_BINARY_INV or Imgproc.THRESH_OTSU
        )
        val cannyThresh = 0.8 * cannyAccThresh
        Imgproc.Canny(grayImage, grayImage, cannyThresh, cannyAccThresh)

        // Dilate and erode to enhance edges
        val s = cellSize * 0.05
        val kernel1 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(s, s))
        val kernel2 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(s / 2, s / 2))
        Imgproc.dilate(grayImage, grayImage, kernel1, Point(-1.0, -1.0))
        Imgproc.erode(grayImage, grayImage, kernel2, Point(-1.0, -1.0))

        // Find contours in the processed image
        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(grayImage, contours, Mat(), Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE)

        // Filter contours by area to keep only significant ones
        val regionsContours = contours.filter { contour ->
            val area = Imgproc.boundingRect(contour).width * Imgproc.boundingRect(contour).height
            area > regionThreshold
        }

        // Draw the filtered contours on a blank image
        val contoursImage = blankImage(grayImage)
        Imgproc.drawContours(contoursImage, regionsContours, -1, Scalar(255.0), 1)

        // Close gaps between contour lines
        val s2 = cellSize * 0.2
        val kernel3 = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(s2, s2))
        Imgproc.morphologyEx(contoursImage, contoursImage, Imgproc.MORPH_CLOSE, kernel3)

        // Draw the border of the contours image
        Imgproc.rectangle(
            contoursImage,
            Point(0.0, 0.0),
            Point(contoursImage.width() - 1.0, contoursImage.height() - 1.0),
            Scalar(255.0),
            1
        )

        // Find contours again on the processed contours image
        contours.clear()
        Imgproc.findContours(
            contoursImage,
            contours,
            Mat(),
            Imgproc.RETR_TREE,
            Imgproc.CHAIN_APPROX_SIMPLE
        )

        // Create a mask image and draw each contour with a unique color
        val maskImage = Mat(contoursImage.rows(), contoursImage.cols(), CvType.CV_8UC3, Scalar(0.0, 0.0, 0.0))
        for (i in contours.indices) {
            val color = Utils.int2color(i)
            Imgproc.drawContours(maskImage, contours, i, Scalar(color), -1)
        }

        return maskImage
    }

    private fun blankImage(template: Mat): Mat {
        return Mat(template.rows(), template.cols(), CvType.CV_8UC1, Scalar(0.0))
    }
}