class Day08 {
    val part1TestExpected = 21
    val part2TestExpected = 8
    fun part1(input: List<String>): Int {
        val parsed = input.map { line -> line.map { it.digitToInt() } }
        val b = parsed.flatMapIndexed { rowNum, line ->
            line.mapIndexed { colNum, height ->
                val noneToTheLeft = colNum == 0 || line.subList(0, colNum).none { it >= height }
                val noneToTheRight =
                    colNum == line.lastIndex || line.subList(colNum + 1, line.size).none { it >= height }
                val noneToTheUp = rowNum == 0 || parsed.subList(0, rowNum).none { it[colNum] >= height }
                val noneToTheDown =
                    rowNum == parsed.lastIndex || parsed.subList(rowNum + 1, parsed.size).none { it[colNum] >= height }
                noneToTheLeft || noneToTheRight || noneToTheUp || noneToTheDown
            }
        }
        return b.count { it }
    }

    fun part2(input: List<String>): Int {
        val parsed = input.map { line -> line.map { it.digitToInt() } }
        val b = parsed.flatMapIndexed { rowNum, line ->
            line.mapIndexed { colNum, height ->
                val col = parsed.map { it[colNum] }
                //I got 120. Passed test, did not pass solution. Check it out.
                scoreLess(line, colNum, height) +
                        scoreMore(line, colNum, height) +
                        scoreLess(col, rowNum, height) +
                        scoreMore(col, rowNum, height)
            }
        }
        return b.maxOf { it }
    }

    private fun scoreLess(line: List<Int>, colNum: Int, height: Int): Int {
        if (colNum == 0) return 0
        val myList = line.subList(0, colNum)
        val idx = myList.indexOfLast { it >= height }
        return if (idx == -1) colNum else colNum - idx
    }

    private fun scoreMore(line: List<Int>, colNum: Int, height: Int): Int {
        if (colNum == line.lastIndex) return 0
        val myList = line.subList(colNum + 1, line.size)
        val idx = myList.indexOfFirst { it >= height }
        return if (idx == -1) line.lastIndex - colNum else idx - colNum
    }
}