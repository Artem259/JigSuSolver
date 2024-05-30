package com.jigsusolver.sudoku.dataproc.regions.validators

import com.jigsusolver.sudoku.models.regions.SudokuRegions
import com.jigsusolver.sudoku.utils.RegionsUtils

class JigsawRegionsValidator : RegionsValidatorBase() {
    override fun extractErrorRegions(sudokuRegions: SudokuRegions): Set<Int> {
        val sudokuSize = sudokuRegions.sudokuSize
        val errorRegions = mutableSetOf<Int>()

        for (region in sudokuRegions.regionSet) {
            if (sudokuRegions.regionSize(region) != sudokuSize) {
                errorRegions.add(region)
            }
        }

        val dfsRegions = RegionsUtils.dfs(sudokuRegions)
        val regionsMap = HashMap<Int, Int>()
        for (row in 0 until sudokuSize) {
            for (col in 0 until sudokuSize) {
                val regionValue = sudokuRegions[row, col]
                val regionDfsValue = dfsRegions[row, col]

                if (!regionsMap.containsKey(regionValue)) {
                    regionsMap[regionValue] = regionDfsValue
                    continue
                }

                if (regionsMap[regionValue] != regionDfsValue) {
                    errorRegions.add(regionValue)
                }
            }
        }

        return errorRegions
    }
}