package com.jigsusolver.sudoku.dataproc.sudoku.validators

import com.jigsusolver.sudoku.models.sudoku.Sudoku

interface Validator {
    fun extractErrorRegions(sudoku: Sudoku): Set<Int>
    fun isValidRegions(sudoku: Sudoku): Boolean
    fun extractErrorValues(sudoku: Sudoku): List<Pair<Int, Int>>
    fun isValidValues(sudoku: Sudoku): Boolean
    fun isValidSudoku(sudoku: Sudoku): Boolean
}