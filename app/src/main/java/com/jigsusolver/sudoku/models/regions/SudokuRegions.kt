package com.jigsusolver.sudoku.models.regions

import com.jigsusolver.sudoku.utils.Utils
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.max
import org.jetbrains.kotlinx.multik.ndarray.operations.min

open class SudokuRegions(sudokuSize: Int) {
    protected var matrix: D2Array<Int>
    protected var regionsCells: MutableMap<Int, MutableList<Pair<Int, Int>>>

    open val sudokuSize: Int
        get() = matrix.shape[0]

    open val regionSet: Set<Int>
        get() = regionsCells.keys

    open val regionCount: Int
        get() = regionsCells.size

    init {
        matrix = mk.zeros<Int>(sudokuSize, sudokuSize)

        regionsCells = hashMapOf(0 to mutableListOf())
        for (i in 0 until sudokuSize) {
            for (j in 0 until sudokuSize) {
                regionsCells[0]?.add(Pair(i, j))
            }
        }
    }

    open fun min(): Int? {
        return matrix.min()
    }

    open fun max(): Int? {
        return matrix.max()
    }

    open fun regionCells(region: Int): List<Pair<Int, Int>> {
        return regionsCells[region] ?: listOf()
    }

    open fun regionCells(row: Int, col: Int): List<Pair<Int, Int>> {
        val region = matrix[row, col]
        return regionCells(region)
    }

    open fun regionSize(region: Int): Int {
        return regionCells(region).size
    }

    open fun cellAdjacencyOther(row: Int, col: Int): Set<Int> {
        val cellAdjacency = cellAdjacencyWithThis(row, col).toMutableSet()
        cellAdjacency.remove(matrix[row, col])
        return cellAdjacency
    }

    open fun cellAdjacencyWithThis(row: Int, col: Int): Set<Int> {
        return cellAdjacencyByDirections(row, col).filterNotNull().toSet()
    }

    open fun cellAdjacencyByDirections(row: Int, col: Int): List<Int?> {
        return Utils.adjacentCells(row, col, sudokuSize).map { it?.let { (row, col) ->
            matrix[row, col]
        }}
    }

    open fun regionAdjacency(region: Int): Set<Int> {
        val regionsAdjacency = mutableSetOf<Int>()

        val regionCells = regionCells(region)
        for ((row, col) in regionCells) {
            val cellAdjacency = cellAdjacencyOther(row, col)
            regionsAdjacency.addAll(cellAdjacency)
        }

        return regionsAdjacency
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SudokuRegions) return false

        if (matrix != other.matrix) return false

        return true
    }

    override fun hashCode(): Int {
        return matrix.hashCode()
    }

    open operator fun get(row: Int, col: Int): Int {
        return matrix[row, col]
    }
}