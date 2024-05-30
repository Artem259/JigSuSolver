package com.jigsusolver.sudoku.dataproc.regions.validators

import com.jigsusolver.sudoku.models.regions.SudokuRegions

abstract class RegionsValidatorBase: RegionsValidator {
    override fun isValidRegions(sudokuRegions: SudokuRegions): Boolean {
        return extractErrorRegions(sudokuRegions).isEmpty()
    }
}