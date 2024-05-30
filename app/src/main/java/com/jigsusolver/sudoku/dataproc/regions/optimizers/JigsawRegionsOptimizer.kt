package com.jigsusolver.sudoku.dataproc.regions.optimizers

import com.jigsusolver.sudoku.models.regions.MutableSudokuRegions
import com.jigsusolver.sudoku.models.regions.SudokuRegions

class JigsawRegionsOptimizer : RegionsOptimizer {
    private lateinit var regions: MutableSudokuRegions
    private lateinit var toOptimize: MutableSet<Int>

    override fun optimizeRegions(sudokuRegions: SudokuRegions): SudokuRegions {
        setup(sudokuRegions)

        if (regions.regionCount > regions.sudokuSize * regions.sudokuSize / 2) {
            for (region in regions.regionSet) {
                regions[region] = 1
            }
        } else {
            while (toOptimize.isNotEmpty()) {
                optimizeRegion(toOptimize.first())
            }
        }

        return regions
    }

    private fun setup(sudokuRegions: SudokuRegions) {
        regions = MutableSudokuRegions(sudokuRegions)
        toOptimize = regions.regionSet.toMutableSet()
    }

    private fun optimizeRegion(region: Int) {
        val toMergeRegions = findSharedStartingSubpath(region, regions.sudokuSize)
        for (toMergeRegion in toMergeRegions) {
            toOptimize.remove(toMergeRegion)
            regions[toMergeRegion] = region
        }
        toOptimize.remove(region)
    }

    private fun findSharedStartingSubpath(startVertex: Int, targetWeight: Int): Set<Int> {
        val currentPath = mutableListOf<Int>()
        var longestCommonPrefix = setOf<Int>()
        val visited = mutableSetOf<Int>()
        var pathsFound = 0

        fun dfs(currentVertex: Int, currentSum: Int) {
            // Add the current vertex to the path
            currentPath.add(currentVertex)
            visited.add(currentVertex)

            // Check if the current sum equals the target weight
            if (currentSum == targetWeight) {
                longestCommonPrefix = if (pathsFound == 0) {
                    currentPath.toMutableSet()
                } else {
                    findLongestCommonPrefix(longestCommonPrefix, currentPath.toSet())
                }
                pathsFound++
            } else if (currentSum < targetWeight) {
                // Continue exploring neighbors
                for (neighbor in regions.regionAdjacency(currentVertex)) {
                    if (!visited.contains(neighbor)) {
                        dfs(neighbor, currentSum + regions.regionSize(neighbor))
                    }
                }
            }

            // Backtrack
            currentPath.removeAt(currentPath.size - 1)
            visited.remove(currentVertex)
        }

        // Start DFS from the start vertex
        dfs(startVertex, regions.regionSize(startVertex))

        return longestCommonPrefix
    }

    private fun findLongestCommonPrefix(path1: Set<Int>, path2: Set<Int>): Set<Int> {
        var longestPrefix = setOf<Int>()

        // Iterate through subsets of increasing lengths
        for (i in 1..minOf(path1.size, path2.size)) {
            val subset1 = path1.take(i).toSet()
            val subset2 = path2.take(i).toSet()

            // Check if the subsets are equal
            if (subset1 == subset2) {
                longestPrefix = subset1
            }
        }

        return longestPrefix
    }
}