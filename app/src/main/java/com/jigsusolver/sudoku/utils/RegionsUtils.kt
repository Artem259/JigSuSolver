package com.jigsusolver.sudoku.utils

import com.jigsusolver.sudoku.models.regions.MutableSudokuRegions
import com.jigsusolver.sudoku.models.regions.SudokuRegions

class RegionsUtils {
    companion object {
        fun dfs(sudokuRegions: SudokuRegions): SudokuRegions {
            val sudokuSize = sudokuRegions.sudokuSize
            val dfsResult = MutableSudokuRegions(sudokuSize)
            val visited = Utils.generateSquareList(sudokuSize) { false }

            fun isValidMove(row: Int, col: Int, value: Int): Boolean {
                return row in 0 until sudokuSize
                        && col in 0 until sudokuSize
                        && sudokuRegions[row, col] == value
                        && !visited[row][col]
            }

            // Create a stack for DFS traversal
            val stack = ArrayDeque<Pair<Int, Int>>()

            // Define possible moves: Up, Down, Left, Right
            val directions = arrayOf(-1, 0, 1, 0, -1)

            var regionCount = 1 // Initialize region count
            for (i in 0 until sudokuSize) {
                for (j in 0 until sudokuSize) {
                    if (!visited[i][j]) {
                        stack.addLast(Pair(i, j))
                        val regionValue = sudokuRegions[i, j]
                        while (stack.isNotEmpty()) {
                            val (row, col) = stack.removeLast()
                            if (!visited[row][col]) {
                                visited[row][col] = true
                                dfsResult[row, col] = regionCount // Assign DFS region value
                                for (k in 0 until 4) {
                                    val newRow = row + directions[k]
                                    val newCol = col + directions[k + 1]
                                    if (isValidMove(newRow, newCol, regionValue)) {
                                        stack.addLast(Pair(newRow, newCol))
                                    }
                                }
                            }
                        }
                        regionCount++ // Increment region count for the next DFS region
                    }
                }
            }

            return dfsResult
        }
    }
}