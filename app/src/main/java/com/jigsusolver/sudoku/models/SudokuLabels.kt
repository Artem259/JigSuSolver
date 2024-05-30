package com.jigsusolver.sudoku.models

class SudokuLabels(
    val labels: List<String>
) {
    val maxLength = labels.maxOfOrNull { it.length } ?: 0

    companion object {
        fun from1to9(): SudokuLabels {
            return SudokuLabels((1..9).map { it.toString() })
        }

        fun from1to16(): SudokuLabels {
            return SudokuLabels(((1..16)).map { it.toString() })
        }

        fun from1to25(): SudokuLabels {
            return SudokuLabels(((1..25)).map { it.toString() })
        }

        fun from0toF(): SudokuLabels {
            return SudokuLabels(((1..9) + ('A'..'F')).map { it.toString() })
        }

        fun <T : Comparable<T>> fromCustomList(iter: Iterable<T>): SudokuLabels {
            return SudokuLabels(iter.map { it.toString() })
        }
    }
}