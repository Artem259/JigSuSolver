package com.jigsusolver.sudoku.imageproc.extractors

import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import org.opencv.imgproc.Imgproc.drawContours

class FieldRectExtractor {
    fun extractFieldRect(image: Mat): Mat {
        val copyImage = Mat()
        Imgproc.cvtColor(image, copyImage, Imgproc.COLOR_BGR2GRAY)

        Imgproc.GaussianBlur(copyImage, copyImage, Size(5.0, 5.0), 0.0)

        Imgproc.adaptiveThreshold(
            copyImage,
            copyImage,
            255.0,
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C,
            Imgproc.THRESH_BINARY_INV,
            11,
            2.0
        )

        val kernel = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, Size(5.0, 5.0))
        Imgproc.morphologyEx(copyImage, copyImage, Imgproc.MORPH_CLOSE, kernel)

        val cntss = ArrayList<MatOfPoint>()
        Imgproc.findContours(
            copyImage,
            cntss,
            Mat(),
            Imgproc.RETR_EXTERNAL,
            Imgproc.CHAIN_APPROX_SIMPLE
        )
        val rcnts = cntss.sortedByDescending { Imgproc.contourArea(it) }

        val puzzleCnt = MatOfPoint2f()
        rcnts[0].convertTo(puzzleCnt, CvType.CV_32FC2)

        val peri = Imgproc.arcLength(puzzleCnt, true)
        Imgproc.approxPolyDP(puzzleCnt, puzzleCnt, 0.015 * peri, true)

        val mat = MatOfPoint()
        puzzleCnt.convertTo(mat, CvType.CV_32SC2)

        // drawContours(image, listOf(mat), -1, Scalar(255.0, 255.0, 255.0, 255.0), 3)
        return mat
    }
}