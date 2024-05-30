package com.jigsusolver.sudoku.imageproc.extractors

import android.content.Context
import android.graphics.Bitmap
import com.jigsusolver.sudoku.imageproc.classification.ImageClassifier
import com.jigsusolver.sudoku.models.SudokuLabels
import com.jigsusolver.sudoku.utils.Utils
import org.opencv.core.Mat

class CharValuesExtractor(
    private val sudokuLabels: SudokuLabels,
    private val context: Context
) {
    private val classifiers: List<List<ImageClassifier>>

    init {
        classifiers = setupClassifiers()
    }

    fun extractCharValues(charImages: List<List<List<Mat>>>): List<List<String>> {
        val sudokuSize = charImages.size
        val charValues = Utils.generateSquareList(sudokuSize) { String() }

        for (i in 0 until sudokuSize) {
            for (j in 0 until sudokuSize) {
                val cellCharImages = charImages[i][j]
                val length = cellCharImages.size
                for ((pos, charImage) in cellCharImages.withIndex()) {
                    val label = processCharImage(charImage, length, pos)
                    if (label != ' ') {
                        charValues[i][j] = charValues[i][j] + label
                    }
                }
            }
        }

        return charValues
    }

    private fun processCharImage(charImage: Mat, length: Int, pos: Int): Char {
        val bitmap = Utils.mat2Bitmap(charImage, Bitmap.Config.ARGB_8888)
        val classifications = classifiers[length - 1][pos].classify(bitmap)
        return classifications[0].categories.firstOrNull()?.label?.get(0) ?: ' '
    }

    private fun setupClassifiers(): List<List<ImageClassifier>> {
        val labelAllowList = List(sudokuLabels.maxLength) { lengthIndex ->
            List(lengthIndex + 1) { mutableListOf<String>() }
        }
        sudokuLabels.labels.forEach { label ->
            label.withIndex().forEach { (i, char) ->
                labelAllowList[label.length - 1][i].add(char.toString())
            }
        }

        return labelAllowList.map { row ->
            row.map {
                ImageClassifier(context, it, threshold = 0.5f)
            }
        }
    }
}