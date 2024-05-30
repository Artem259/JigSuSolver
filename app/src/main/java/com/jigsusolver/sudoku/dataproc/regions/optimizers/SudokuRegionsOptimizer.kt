package com.jigsusolver.sudoku.dataproc.regions.optimizers

import com.jigsusolver.sudoku.models.SudokuType
import com.jigsusolver.sudoku.models.regions.SudokuRegions

class SudokuRegionsOptimizer(
    private val sudokuType: SudokuType
) : RegionsOptimizer {
    override fun optimizeRegions(sudokuRegions: SudokuRegions): SudokuRegions {
        val regionsOptimizer = when (sudokuType) {
            SudokuType.CLASSIC -> ClassicRegionsOptimizer()
            SudokuType.JIGSAW -> JigsawRegionsOptimizer()
        }

        return regionsOptimizer.optimizeRegions(sudokuRegions)
    }
}