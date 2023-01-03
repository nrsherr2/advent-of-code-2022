class Day24 {
    val part1TestExpected = 18
    val part2TestExpected = 54
    fun part1(input: List<String>): Int {
        val initialState = parseInput(input)
        var locations = listOf(Point(0, 1))
        val destPoint = initialState.second.let { walls ->
            val rn = walls.maxOf { it.location.rowNum }
            (0..1_000).map { Point(rn, it) }.first { Wall(it) !in walls }
        }
        val steps = calculateSteps(initialState)
        var i = 0
        while (locations.none { it == destPoint }) {
            i++
            val board = steps[i]
            val neighbors = locations.flatMap { loc ->
                listOf(Point(0, 0), Point(-1, 0), Point(1, 0), Point(0, 1), Point(0, -1)).map {
                    loc + it
                }.filter { it.rowNum > -1 && it !in board }
            }
            locations = neighbors.distinct()
            if (locations.isEmpty()) throw IllegalArgumentException()
        }
        return i
    }

    fun part2(input: List<String>): Int {
        val initialState = parseInput(input)
        val startPoint = Point(0, 1)
        var locations = listOf(startPoint)
        val destPoint = initialState.second.let { walls ->
            val rn = walls.maxOf { it.location.rowNum }
            (0..1_000).map { Point(rn, it) }.first { Wall(it) !in walls }
        }
        val steps = calculateSteps(initialState, 3)
        var i = 0
        while (locations.none { it == destPoint }) {
            i++
            val board = steps[i]
            val neighbors = locations.flatMap { loc ->
                listOf(Point(0, 0), Point(-1, 0), Point(1, 0), Point(0, 1), Point(0, -1)).map {
                    loc + it
                }.filter { it.rowNum > -1 && it !in board }
            }
            locations = neighbors.distinct()
            if (locations.isEmpty()) throw IllegalArgumentException()
        }
        locations = listOf(destPoint)
        while (locations.none { it == startPoint }) {
            i++
            val board = steps[i]
            val neighbors = locations.flatMap { loc ->
                listOf(Point(0, 0), Point(-1, 0), Point(1, 0), Point(0, 1), Point(0, -1)).map {
                    loc + it
                }.filter { it.rowNum > -1 && it.rowNum <= board.maxOf { it.rowNum } && it !in board }
            }
            locations = neighbors.distinct()
            if (locations.isEmpty()) throw IllegalArgumentException()
        }
        locations = listOf(startPoint)
        while (locations.none { it == destPoint }) {
            i++
            val board = steps[i]
            val neighbors = locations.flatMap { loc ->
                listOf(Point(0, 0), Point(-1, 0), Point(1, 0), Point(0, 1), Point(0, -1)).map {
                    loc + it
                }.filter { it.rowNum > -1 && it !in board }
            }
            locations = neighbors.distinct()
            if (locations.isEmpty()) throw IllegalArgumentException()
        }
        return i
    }

    private fun parseInput(input: List<String>): Pair<List<Blizzard>, List<Wall>> {
        val blizzard = mutableListOf<Blizzard>()
        val activision = mutableListOf<Wall>()
        input.forEachIndexed { rowNum, line ->
            line.forEachIndexed { colNum, c ->
                val p = Point(rowNum, colNum)
                when (c) {
                    '#' -> activision.add(Wall(p))
                    '>' -> blizzard.add(Blizzard(p, Point(0, 1)))
                    '<' -> blizzard.add(Blizzard(p, Point(0, -1)))
                    '^' -> blizzard.add(Blizzard(p, Point(-1, 0)))
                    'v' -> blizzard.add(Blizzard(p, Point(1, 0)))
                }
            }
        }
        return blizzard to activision
    }

    private fun calculateSteps(initialState: Pair<List<Blizzard>, List<Wall>>, factor: Int = 1): List<List<Point>> {
        val walls = initialState.second
        val locations = walls.map { it.location }
        val minRow = walls.minOf { it.location.rowNum }
        val maxRow = walls.maxOf { it.location.rowNum }
        val minCol = walls.minOf { it.location.colNum }
        val maxCol = walls.maxOf { it.location.colNum }
        val len = maxRow - minRow - 1
        val width = maxCol - minCol - 1
        return (0..(len * width * factor)).map { step ->
            initialState.first.map { b ->
                val newPoint = b.startingPoint + (b.direction * step)
                while (newPoint.rowNum >= maxRow) newPoint.rowNum -= len
                while (newPoint.rowNum <= minRow) newPoint.rowNum += len
                while (newPoint.colNum <= minCol) newPoint.colNum += width
                while (newPoint.colNum >= maxCol) newPoint.colNum -= width
                newPoint
            } + locations
        }
    }

    data class Blizzard(
        val startingPoint: Point,
        val direction: Point
    )

    fun visualize(walls: List<Point>, locations: List<Point>) {
        val minRow = walls.minOf { it.rowNum }
        val maxRow = walls.maxOf { it.rowNum }
        val minCol = walls.minOf { it.colNum }
        val maxCol = walls.maxOf { it.colNum }
        (minRow..maxRow).forEach { rn ->
            (minCol..maxCol).forEach { cn ->
                val p = Point(rn, cn)
                if (locations.contains(p)) print('E')
                else if (walls.contains(p)) print('#')
                else print(' ')
            }
            println()
        }
        println()
    }

    data class Wall(val location: Point)
}