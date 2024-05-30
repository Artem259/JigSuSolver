package com.jigsusolver.sudoku.models.sudoku

import com.jigsusolver.sudoku.models.SudokuLabels
import com.jigsusolver.sudoku.models.SudokuType
import com.jigsusolver.sudoku.models.regions.SudokuRegions

open class Sudoku(
    open val values: List<List<String>>,
    open val regions: SudokuRegions,
    open val type: SudokuType,
    open val labels: SudokuLabels
) {
    val size: Int
        get() = regions.sudokuSize

    fun rowValues(n: Int): List<String> {
        return values[n]
    }

    fun colValues(n: Int): List<String> {
        return values.map { it[n] }
    }

    fun regionValues(n: Int): List<String> {
        val regionCells = regions.regionCells(n)
        return regionCells.map { (i, j) -> values[i][j] }
    }

    override fun toString(): String {
        return values.joinToString("\n") { row -> row.joinToString() }
    }
}