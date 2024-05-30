package com.jigsusolver.sudoku.models.sudoku

import com.jigsusolver.sudoku.models.SudokuLabels
import com.jigsusolver.sudoku.models.SudokuType
import com.jigsusolver.sudoku.models.regions.MutableSudokuRegions
import com.jigsusolver.sudoku.models.regions.SudokuRegions

class MutableSudoku(
    values: List<List<String>>,
    regions: SudokuRegions,
    type: SudokuType,
    labels: SudokuLabels
) : Sudoku(values, regions, type, labels) {
    override val values = values.map { it.toMutableList() }
    override val regions = MutableSudokuRegions(regions)

    constructor(sudoku: Sudoku) : this(sudoku.values, sudoku.regions, sudoku.type, sudoku.labels)
}