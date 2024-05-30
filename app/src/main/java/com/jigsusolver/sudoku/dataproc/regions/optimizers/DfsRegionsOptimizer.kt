package com.jigsusolver.sudoku.dataproc.regions.optimizers

import com.jigsusolver.sudoku.models.regions.SudokuRegions
import com.jigsusolver.sudoku.utils.RegionsUtils

class DfsRegionsOptimizer : RegionsOptimizer {
    override fun optimizeRegions(sudokuRegions: SudokuRegions): SudokuRegions {
        return RegionsUtils.dfs(sudokuRegions)
    }
}