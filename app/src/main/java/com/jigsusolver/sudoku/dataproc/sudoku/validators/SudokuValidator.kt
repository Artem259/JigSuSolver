package com.jigsusolver.sudoku.dataproc.sudoku.validators

import com.jigsusolver.sudoku.models.SudokuType
import com.jigsusolver.sudoku.models.sudoku.Sudoku

class SudokuValidator : ValidatorBase() {
    override fun extractErrorValues(sudoku: Sudoku): List<Pair<Int, Int>> {
        val sudokuValidator = when (sudoku.type) {
            SudokuType.CLASSIC -> ClassicJigsawSudokuValidator()
            SudokuType.JIGSAW -> ClassicJigsawSudokuValidator()
        }

        return sudokuValidator.extractErrorValues(sudoku)
    }
}