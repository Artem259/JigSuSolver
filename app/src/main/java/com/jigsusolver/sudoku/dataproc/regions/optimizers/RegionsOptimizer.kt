package com.jigsusolver.sudoku.dataproc.regions.optimizers

import com.jigsusolver.sudoku.models.regions.SudokuRegions

interface RegionsOptimizer {
    fun optimizeRegions(sudokuRegions: SudokuRegions): SudokuRegions
}