package com.jigsusolver.sudoku.dataproc.regions.validators

import com.jigsusolver.sudoku.models.regions.SudokuRegions

interface RegionsValidator {
    fun extractErrorRegions(sudokuRegions: SudokuRegions): Set<Int>
    fun isValidRegions(sudokuRegions: SudokuRegions): Boolean
}