package com.jigsusolver.sudoku.dataproc.sudoku.solvers

import com.jigsusolver.sudoku.dataproc.regions.validators.SudokuRegionsValidator
import com.jigsusolver.sudoku.models.sudoku.Sudoku
import com.jigsusolver.sudoku.models.SudokuType
import com.jigsusolver.sudoku.utils.Utils

class SudokuSolver : Solver {
    override fun solve(sudoku: Sudoku): Sudoku? {
        require(Utils.isSquare(sudoku.values))
        require(sudoku.values.flatten().all { it == "" || sudoku.labels.labels.contains(it) })
        require(SudokuRegionsValidator(sudoku.type).isValidRegions(sudoku.regions))

        val sudokuSolver = when (sudoku.type) {
            SudokuType.CLASSIC -> ClassicJigsawSudokuSolver()
            SudokuType.JIGSAW -> ClassicJigsawSudokuSolver()
        }

        return sudokuSolver.solve(sudoku)
    }
}