package com.jigsusolver.sudoku.dataproc.sudoku.solvers

import com.jigsusolver.sudoku.models.sudoku.Sudoku

interface Solver {
    fun solve(sudoku: Sudoku): Sudoku?
}