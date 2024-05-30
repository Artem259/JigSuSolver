package com.jigsusolver.sudoku.models.regions

import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set

class MutableSudokuRegions(sudokuSize: Int) : SudokuRegions(sudokuSize) {
    override val regionSet: Set<Int>
        get() = super.regionSet.toSet()

    constructor(sudokuRegions: SudokuRegions) : this(sudokuRegions.sudokuSize) {
        for (row in 0 until sudokuSize) {
            for (col in 0 until sudokuSize) {
                this[row, col] = sudokuRegions[row, col]
            }
        }
    }

    override fun regionCells(region: Int): List<Pair<Int, Int>> {
        return super.regionCells(region).toList()
    }

    override fun regionCells(row: Int, col: Int): List<Pair<Int, Int>> {
        return super.regionCells(row, col).toList()
    }

    operator fun set(row: Int, col: Int, value: Int) {
        val oldValue = matrix[row, col]

        regionsCells[oldValue]?.remove(Pair(row, col))
        if (regionsCells[oldValue]?.isEmpty() == true) {
            regionsCells.remove(oldValue)
        }

        if (!regionsCells.containsKey(value)) {
            regionsCells[value] = mutableListOf()
        }
        regionsCells[value]?.add(Pair(row, col))

        matrix[row, col] = value
    }

    operator fun set(oldValue: Int, newValue: Int) {
        val regionCells = regionCells(oldValue)
        for ((row, col) in regionCells) {
            this[row, col] = newValue
        }
    }

    operator fun set(cells: List<Pair<Int, Int>>, value: Int) {
        for ((row, col) in cells) {
            this[row, col] = value
        }
    }
}