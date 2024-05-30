package com.jigsusolver.sudoku.dataproc.regions.optimizers

import com.jigsusolver.sudoku.models.regions.MutableSudokuRegions
import com.jigsusolver.sudoku.models.regions.SudokuRegions
import com.jigsusolver.sudoku.utils.Utils

class ClassicRegionsOptimizer : RegionsOptimizer {
    override fun optimizeRegions(sudokuRegions: SudokuRegions): SudokuRegions {
        val sudokuSize = sudokuRegions.sudokuSize
        if (!Utils.isSquare(sudokuSize)) {
            throw IllegalArgumentException("The side size of a Classic Sudoku must be square")
        }
        val optimisedRegions = MutableSudokuRegions(sudokuSize)

        val regionSize = Utils.intSqrt(sudokuSize)
        var value = 1
        for (row in 0 until sudokuSize step regionSize) {
            for (col in 0 until sudokuSize step regionSize) {
                for (i in row until row + regionSize) {
                    for (j in col until col + regionSize) {
                        optimisedRegions[i, j] = value
                    }
                }
                value++
            }
        }

        return optimisedRegions
    }
}