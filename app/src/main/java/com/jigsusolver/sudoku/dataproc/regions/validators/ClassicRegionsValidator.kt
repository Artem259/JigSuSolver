package com.jigsusolver.sudoku.dataproc.regions.validators

import com.jigsusolver.sudoku.models.regions.SudokuRegions
import com.jigsusolver.sudoku.utils.Utils

class ClassicRegionsValidator : RegionsValidatorBase() {
    override fun extractErrorRegions(sudokuRegions: SudokuRegions): Set<Int> {
        val sudokuSize = sudokuRegions.sudokuSize
        if (!Utils.isSquare(sudokuSize)) {
            throw IllegalArgumentException("The side size of a Classic Sudoku must be square")
        }
        val errorRegions = mutableSetOf<Int>()

        val regionSize = Utils.intSqrt(sudokuSize)
        for (row in 0 until sudokuSize step regionSize) {
            for (col in 0 until sudokuSize step regionSize) {
                val regionValue = sudokuRegions[row, col]
                for (i in row until row + regionSize) {
                    for (j in col until col + regionSize) {
                        if (sudokuRegions[i, j] != regionValue) {
                            errorRegions.addAll(listOf(regionValue, sudokuRegions[i, j]))
                        }
                    }
                }
            }
        }

        for (region in sudokuRegions.regionSet) {
            if (sudokuRegions.regionSize(region) != sudokuSize) {
                errorRegions.add(region)
            }
        }

        return errorRegions
    }
}