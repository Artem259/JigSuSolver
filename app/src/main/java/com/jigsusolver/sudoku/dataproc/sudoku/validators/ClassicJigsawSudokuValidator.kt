package com.jigsusolver.sudoku.dataproc.sudoku.validators

import com.jigsusolver.sudoku.models.sudoku.Sudoku

class ClassicJigsawSudokuValidator : ValidatorBase() {
    override fun extractErrorValues(sudoku: Sudoku): List<Pair<Int, Int>> {
        val errorValues = mutableListOf<Pair<Int, Int>>()

        for (row in 0 until sudoku.size) {
            for (col in 0 until sudoku.size) {
                val value = sudoku.values[row][col]
                if (value.isEmpty()) {
                    continue
                }
                if (sudoku.regionValues(sudoku.regions[row, col]).count { it == value } > 1
                    || sudoku.rowValues(row).count { it == value } > 1
                    || sudoku.colValues(col).count { it == value } > 1) {
                    errorValues.add(Pair(row, col))
                }
            }
        }

        return errorValues
    }
}