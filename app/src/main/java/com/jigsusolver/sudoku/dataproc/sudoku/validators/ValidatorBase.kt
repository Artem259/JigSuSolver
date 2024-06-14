package com.jigsusolver.sudoku.dataproc.sudoku.validators

import com.jigsusolver.sudoku.dataproc.regions.validators.SudokuRegionsValidator
import com.jigsusolver.sudoku.models.sudoku.Sudoku

abstract class ValidatorBase : Validator {
    override fun extractErrorRegions(sudoku: Sudoku): Set<Int> {
        val validator = SudokuRegionsValidator(sudoku.type)
        return validator.extractErrorRegions(sudoku.regions)
    }

    override fun isValidRegions(sudoku: Sudoku): Boolean {
        val validator = SudokuRegionsValidator(sudoku.type)
        return validator.isValidRegions(sudoku.regions)
    }

    override fun isValidValues(sudoku: Sudoku): Boolean {
        return extractErrorValues(sudoku).isEmpty()
    }

    override fun isValidSudoku(sudoku: Sudoku): Boolean {
        return isValidRegions(sudoku) && isValidValues(sudoku)
    }
}