class Day23 {
    val part1TestExpected = 110
    val part2TestExpected = 20
    fun part1(input: List<String>): Int {
        val directionList = mutableListOf('N', 'S', 'W', 'E')
        var elves = buildList {
            input.forEachIndexed { rowNum, s ->
                s.forEachIndexed { colNum, c ->
                    if (c == '#') add(Point(rowNum, colNum))
                }
            }
        }
        for (i in 1..10) {
            val proposedPoints = elves.associateWith { e ->
                if (directionList.flatMap { it.scanPoints() }.distinct().none { elves.contains(it + e) }) null
                else directionList.firstOrNull {
                    it.scanPoints().asSequence().map { s -> s + e }.none { scp -> elves.contains(scp) }
                }?.toDirectionPoint()?.plus(e)
            }
            val newPoints = proposedPoints.map { (oldPoint, newPoint) ->
                when {
                    newPoint == null -> oldPoint
                    proposedPoints.values.count { it == newPoint } != 1 -> oldPoint
                    else -> newPoint
                }
            }
            elves = newPoints
            directionList.rotate()
        }
        return (elves.minOf { it.rowNum }..elves.maxOf { it.rowNum }).sumOf { rowNum ->
            val a = (elves.minOf { it.colNum }..elves.maxOf { it.colNum }).count { colNum ->
                val p = Point(rowNum, colNum)
                val c = !elves.contains(p)
//                print(if (!c) '#' else '.')
                c
            }
//            println()
            a
        }
    }

    fun part2(input: List<String>): Int {
        val directionList = mutableListOf('N', 'S', 'W', 'E')
        var elves = buildList {
            input.forEachIndexed { rowNum, s ->
                s.forEachIndexed { colNum, c ->
                    if (c == '#') add(Point(rowNum, colNum))
                }
            }
        }
        var i = 0
        while (true) {
            i++
            var numMoved = 0
            val proposedPoints = elves.associateWith { e ->
                if (directionList.flatMap { it.scanPoints() }.distinct().none { elves.contains(it + e) }) null
                else directionList.firstOrNull {
                    it.scanPoints().asSequence().map { s -> s + e }.none { scp -> elves.contains(scp) }
                }?.toDirectionPoint()?.plus(e)
            }
            val newPoints = proposedPoints.map { (oldPoint, newPoint) ->
                when {
                    newPoint == null -> oldPoint
                    proposedPoints.values.count { it == newPoint } != 1 -> oldPoint
                    else -> {
                        numMoved++
                        newPoint
                    }
                }
            }
            if (numMoved == 0) return i
            if (i % 100 == 0) println("Round $i, $numMoved moved")
            elves = newPoints
            directionList.rotate()
        }
    }


    fun Char.scanPoints(): List<Point> = when (this) {
        'N' -> listOf(Point(-1, -1), Point(-1, 0), Point(-1, 1))
        'S' -> listOf(Point(1, -1), Point(1, 0), Point(1, 1))
        'E' -> listOf(Point(-1, 1), Point(0, 1), Point(1, 1))
        'W' -> listOf(Point(-1, -1), Point(0, -1), Point(1, -1))
        else -> TODO()
    }

    fun Char.toDirectionPoint(): Point = when (this) {
        'N' -> Point(-1, 0)
        'S' -> Point(1, 0)
        'E' -> Point(0, 1)
        'W' -> Point(0, -1)
        else -> TODO()
    }
}