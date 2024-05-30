package com.jigsusolver.sudoku.dataproc.sudoku.solvers

import com.jigsusolver.sudoku.models.sudoku.MutableSudoku
import com.jigsusolver.sudoku.models.sudoku.Sudoku

class ClassicJigsawSudokuSolver : Solver {
    override fun solve(sudoku: Sudoku): Sudoku? {
        val solvedSudoku = MutableSudoku(sudoku)
        return if (solveRecursive(solvedSudoku)) {
            solvedSudoku
        } else {
            null // Sudoku is unsolvable
        }
    }

    private fun solveRecursive(sudoku: MutableSudoku): Boolean {
        val emptyCell = findEmptyCell(sudoku) ?: return true

        val (row, col) = emptyCell
        for (label in sudoku.labels.labels) {
            if (isValidMove(sudoku, row, col, label)) {
                sudoku.values[row][col] = label
                if (solveRecursive(sudoku)) {
                    return true
                }
                sudoku.values[row][col] = ""
            }
        }
        return false
    }

    private fun findEmptyCell(sudoku: Sudoku): Pair<Int, Int>? {
        for (row in 0 until sudoku.size) {
            for (col in 0 until sudoku.size) {
                if (sudoku.values[row][col] == "") {
                    return Pair(row, col)
                }
            }
        }
        return null
    }

    private fun isValidMove(sudoku: Sudoku, row: Int, col: Int, label: String): Boolean {
        val region = sudoku.regions[row, col]
        return !(label in sudoku.rowValues(row)
                || label in sudoku.colValues(col)
                || label in sudoku.regionValues(region))
    }
}