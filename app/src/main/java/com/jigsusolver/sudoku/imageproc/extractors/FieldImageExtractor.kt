package com.jigsusolver.sudoku.imageproc.extractors

import org.opencv.core.Mat
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Point
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.INTER_CUBIC
import org.opencv.imgproc.Imgproc.resize

class FieldImageExtractor {
    fun extractFieldImage(image: Mat, rect: Mat): Mat {
        val img = perspectiveTransform(image, rect)
        val size = (if (img.cols() > img.rows()) img.cols() else img.rows()).toDouble()
        resize(img, img, Size(size, size),0.0,0.0, INTER_CUBIC)
        return img
    }

    private fun perspectiveTransform(image: Mat, rect: Mat): Mat {
        fun orderCornerPoints(rect: Mat): List<Point> {
            // Convert rectangle to list of points
            val points = mutableListOf<Point>()
            for (i in 0 until rect.rows()) {
                val point = Point(rect.get(i, 0)[0], rect.get(i, 0)[1])
                points.add(point)
            }
            // Order points in clockwise order
            val topLeft = points.minByOrNull { it.x + it.y }!!
            val bottomRight = points.maxByOrNull { it.x + it.y }!!
            val topRight = points.maxByOrNull { it.x - it.y }!!
            val bottomLeft = points.minByOrNull { it.x - it.y }!!
            return listOf(topLeft, topRight, bottomRight, bottomLeft)
        }
        fun Double.pow(exp: Double): Double = Math.pow(this, exp)
        fun Double.sqrt(): Double = kotlin.math.sqrt(this)

        // Order points in clockwise order
        val orderedCorners = orderCornerPoints(rect)
        val (topLeft, topRight, bottomRight, bottomLeft) = orderedCorners

        // Determine width of new image which is the max distance between
        // (bottom right and bottom left) or (top right and top left) x-coordinates
        val widthA = (bottomRight.x - bottomLeft.x).pow(2.0) + (bottomRight.y - bottomLeft.y).pow(2.0)
        val widthB = (topRight.x - topLeft.x).pow(2.0) + (topRight.y - topLeft.y).pow(2.0)
        val width = maxOf(widthA.sqrt(), widthB.sqrt()).toInt()

        // Determine height of new image which is the max distance between
        // (top right and bottom right) or (top left and bottom left) y-coordinates
        val heightA = (topRight.x - bottomRight.x).pow(2.0) + (topRight.y - bottomRight.y).pow(2.0)
        val heightB = (topLeft.x - bottomLeft.x).pow(2.0) + (topLeft.y - bottomLeft.y).pow(2.0)
        val height = maxOf(heightA.sqrt(), heightB.sqrt()).toInt()

        // Construct new points to obtain top-down view of image in
        // top_r, top_l, bottom_l, bottom_r order
        val dimensions = MatOfPoint2f(
            Point(0.0, 0.0),
            Point((width - 1).toDouble(), 0.0),
            Point((width - 1).toDouble(), (height - 1).toDouble()),
            Point(0.0, (height - 1).toDouble())
        )

        // Convert to MatOfPoint2f format
        val orderedCornersMat = MatOfPoint2f()
        orderedCornersMat.fromList(orderedCorners)

        // Find perspective transform matrix
        val matrix = Imgproc.getPerspectiveTransform(orderedCornersMat, dimensions)

        // Return the transformed image
        val transformedImage = Mat()
        Imgproc.warpPerspective(image, transformedImage, matrix, Size(width.toDouble(), height.toDouble()))
        return transformedImage
    }
}