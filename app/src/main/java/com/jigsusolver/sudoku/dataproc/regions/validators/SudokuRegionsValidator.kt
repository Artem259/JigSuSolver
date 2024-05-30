package com.jigsusolver.sudoku.dataproc.regions.validators

import com.jigsusolver.sudoku.models.SudokuType
import com.jigsusolver.sudoku.models.regions.SudokuRegions

class SudokuRegionsValidator(
    private val sudokuType: SudokuType
) : RegionsValidatorBase() {
    override fun extractErrorRegions(sudokuRegions: SudokuRegions): Set<Int> {
        val regionsValidator = when (sudokuType) {
            SudokuType.CLASSIC -> ClassicRegionsValidator()
            SudokuType.JIGSAW -> JigsawRegionsValidator()
        }

        return regionsValidator.extractErrorRegions(sudokuRegions)
    }
}