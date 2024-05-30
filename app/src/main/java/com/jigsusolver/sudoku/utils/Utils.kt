package com.jigsusolver.sudoku.utils

import android.graphics.Bitmap
import org.opencv.core.Mat
import kotlin.math.sqrt
import kotlin.random.Random
import org.opencv.android.Utils

class Utils {
    companion object {
        fun <T> generateSquareList(size: Int, e: () -> T): List<MutableList<T>> {
            return List(size) { MutableList(size) { e() } }
        }

        fun <T> isSquare(matrix: List<List<T>>): Boolean {
            val size = matrix.firstOrNull()?.size ?: return true
            return isRectangular(matrix) && matrix.size == size
        }

        fun <T> isRectangular(matrix: List<List<T>>): Boolean {
            val size = matrix.firstOrNull()?.size ?: return true
            return matrix.all { it.size == size }
        }

        fun adjacentCells(row: Int, col: Int, size: Int): List<Pair<Int, Int>?> {
            val adjacentCells = mutableListOf<Pair<Int, Int>?>()

            val directions = arrayOf(-1, 0, 1, 0, -1)
            for (i in 0 until 4) {
                val newRow = row + directions[i]
                val newCol = col + directions[i + 1]

                val adjacentCell = if (newRow !in 0 until size || newCol !in 0 until size) {
                    null
                } else {
                    Pair(newRow, newCol)
                }

                adjacentCells.add(adjacentCell)
            }

            return adjacentCells
        }

        fun int2color(int: Int): DoubleArray {
            val c1 = ((int shr 16) and 0xFF).toDouble()
            val c2 = ((int shr 8) and 0xFF).toDouble()
            val c3 = (int and 0xFF).toDouble()
            return doubleArrayOf(c1, c2, c3)
        }

        fun color2int(color: DoubleArray): Int {
            val (c1, c2, c3) = color
            return (c1.toInt() and 0xFF shl 16) or (c2.toInt() and 0xFF shl 8) or (c3.toInt() and 0xFF)
        }

        fun mat2Bitmap(mat: Mat, config: Bitmap.Config): Bitmap {
            val bitmap = Bitmap.createBitmap(mat.cols(), mat.rows(), config)
            Utils.matToBitmap(mat, bitmap)
            return bitmap
        }

        fun randColor(from: IntArray = intArrayOf(0, 0, 0), until: IntArray = intArrayOf(256, 256, 256)): DoubleArray {
            val c1 = Random.nextInt(from[0], until[0]).toDouble()
            val c2 = Random.nextInt(from[1], until[1]).toDouble()
            val c3 = Random.nextInt(from[2], until[2]).toDouble()
            return doubleArrayOf(c1, c2, c3)
        }

        fun intSqrt(n: Int): Int {
             return sqrt(n.toDouble()).toInt()
        }

        fun isSquare(n: Int): Boolean {
            if (n < 0) return false
            val sqrt = intSqrt(n)
            return (sqrt * sqrt == n)
        }

        fun maxOfWidth(imagesMatrix: List<Mat>): Int {
            return imagesMatrix.maxOfOrNull { it.width() } ?: 0
        }

        fun maxOfHeight(imagesMatrix: List<Mat>): Int {
            return imagesMatrix.maxOfOrNull { it.height() } ?: 0
        }

        fun maxOfWidthAndHeight(imagesMatrix: List<Mat>): Int {
            return maxOf(maxOfWidth(imagesMatrix), maxOfHeight(imagesMatrix))
        }

        fun sumOfWidth(imagesMatrix: List<Mat>): Int {
            return imagesMatrix.sumOf { it.width() }
        }

        fun sumOfHeight(imagesMatrix: List<Mat>): Int {
            return imagesMatrix.sumOf { it.height() }
        }
    }
}