import kotlin.math.max
import kotlin.math.min

class Day14 {
    val part1TestExpected = 24
    val part2TestExpected = 93
    fun part1(input: List<String>): Int {
        val walls = parseInput(input)
        val abyssLevel = walls.maxOf { it.rowNum } + 1
        val grainsOfSand = mutableSetOf<Point>()
//        visualize(walls,grainsOfSand)
        while (true) {
            var gws = Point(grainsOfSand.filter { it.colNum == 500 }.minOfOrNull { it.rowNum }?.minus(1) ?: 0, 500)
            var first = gws.firstAvailable(walls, grainsOfSand)
            while (first != null) {
                gws = first.copy()
                if (gws.rowNum == abyssLevel) return grainsOfSand.size
                first = gws.firstAvailable(walls, grainsOfSand)
            }
            grainsOfSand.add(gws)
//            visualize(walls,grainsOfSand)
        }
    }

    fun part2(input: List<String>): Int {
        val walls = parseInput(input)
        val grainsOfSand = mutableSetOf<Point>()
        val floor = walls.maxOf { it.rowNum } + 2
        while (Point(0, 500).firstAvailable(walls, grainsOfSand) != null) {
            var gws = Point(grainsOfSand.filter { it.colNum == 500 }.minOfOrNull { it.rowNum }?.minus(1) ?: 0, 500)
            var first = gws.firstAvailableP2(walls, grainsOfSand, floor)
            while (first != null) {
                gws = first.copy()
                if (gws.rowNum == floor) return grainsOfSand.size
                first = gws.firstAvailableP2(walls, grainsOfSand, floor)
            }
            grainsOfSand.add(gws)
        }
//        visualize(walls,grainsOfSand)
        return grainsOfSand.size + 1
    }

    private fun Point.firstAvailable(walls: Set<Point>, sands: Set<Point>): Point? {
        listOf(0, -1, 1).forEach {
            val newPoint = this + Point(1, it)
            if (!walls.contains(newPoint) && !sands.contains(newPoint)) return newPoint
        }
        return null
    }

    private fun Point.firstAvailableP2(walls: Set<Point>, sands: Set<Point>, floorLevel: Int): Point? {
        return if (this.rowNum == floorLevel - 1) null
        else firstAvailable(walls, sands)
    }

    private fun parseInput(input: List<String>): Set<Point> {
        val points = buildSet {
            input.forEach { segmentedLine ->
                val seggs =
                    segmentedLine.split(" -> ")
                        .map { pair -> pair.split(",").let { Point(it[1].toInt(), it[0].toInt()) } }
                seggs.windowed(2, 1).forEach { (a, b) ->
                    for (i in min(a.rowNum, b.rowNum)..max(a.rowNum, b.rowNum)) {
                        for (j in min(a.colNum, b.colNum)..max(a.colNum, b.colNum)) {
                            add(Point(i, j))
                        }
                    }
                }
            }
        }
        return points
    }

    private fun visualize(walls: Set<Point>, sands: Set<Point>) {
        val minCol = min(walls.minOf { it.colNum }, sands.minOfOrNull { it.colNum } ?: Int.MAX_VALUE)
        val maxCol = max(walls.maxOf { it.colNum }, sands.maxOfOrNull { it.colNum } ?: Int.MIN_VALUE)
        for (i in minCol..maxCol)
            print(if (i == 500) '5' else ' ')
        println()
        val minRow = 0
        val maxRow = max(walls.maxOf { it.rowNum }, sands.maxOfOrNull { it.rowNum } ?: Int.MIN_VALUE)
        for (i in minRow..maxRow) {
            for (j in minCol..maxCol) {
                val p = Point(i, j)
                if (sands.contains(p)) print('░')
                else if (walls.contains(p)) print('▓')
                else print(' ')
            }
            println()
        }
    }
}