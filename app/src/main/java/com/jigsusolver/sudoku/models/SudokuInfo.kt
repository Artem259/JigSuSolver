package com.jigsusolver.sudoku.models

data class SudokuInfo(
    val type: SudokuType,
    val size: Int,
    val labels: SudokuLabels
)