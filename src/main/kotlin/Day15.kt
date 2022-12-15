import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.system.measureTimeMillis

class Day15 {
    val part1TestExpected = 26
    val part2TestExpected = 56000011
    fun part1(input: List<String>): Int {
        val map = parseInput(input)
        val numToLookFor = if (map.size < 20) 10 else 2000000
        val filtered = map.mapNotNull {
            val mhd = manhattanDistance(it.key, it.value)
            val range = (it.key.rowNum - mhd)..(it.key.rowNum + mhd)
            if (numToLookFor in range) PointTriple(it.key, it.value, mhd)
            else null
        }
        println("mapSize: ${map.size}\t filteredSize: ${filtered.size}")
        return (filtered.minOf { it.sensor.colNum - it.manhattan }..filtered.maxOf { it.sensor.colNum + it.manhattan }).count { colNum ->
            val point = Point(numToLookFor, colNum)
            if (point in map.values) false
            else filtered.any { manhattanDistance(point, it.sensor) <= it.manhattan }
        }
    }

    fun part2(input: List<String>): Int {
        val map = parseInput(input).map { PointTriple(it.key, it.value, manhattanDistance(it.key, it.value)) }
        val numToLookFor = if (map.size < 20) 20 else 4000000
        val list = mutableSetOf<Point>()
        map.forEachIndexed { index, t ->
            println("Processing aura $index")
            val points = t.menacingAura()
            println("Matching against $t: ${points.size}")
            points.forEach { p ->
                if (p.rowNum < 0 || p.colNum < 0 || p.rowNum > numToLookFor || p.colNum > numToLookFor) return@forEach
                if (map.all { manhattanDistance(it.sensor, p) > it.manhattan }) list.add(p)
            }
        }
        println(list)
        return list.first().let { it.colNum * 4000000 + it.rowNum }
    }

    fun part2Dep(input: List<String>): Int {
        val map = parseInput(input).map { PointTriple(it.key, it.value, manhattanDistance(it.key, it.value)) }
        val numToLookFor = if (map.size < 20) 20 else 4000000
        for (rowNum in 0..numToLookFor) measureTimeMillis {
            val res = map.fold(emptyList<Int>()) { acc, pointTriple ->
                acc + pointTriple.colRange(rowNum, numToLookFor)
            }.distinct()
            if (res.size != numToLookFor + 1) {
                val x = (0..numToLookFor).first { it !in res }
                return x * 4000000 + rowNum
            }
        }.let { println("It took $rowNum $it seconds to finish") }
        return -1
    }

    data class PointTriple(val sensor: Point, val beacon: Point, val manhattan: Int) {
        fun colRange(rowNum: Int, numToLookFor: Int): IntRange {
            if (rowNum !in sensor.rowNum - manhattan..sensor.rowNum + manhattan) return IntRange.EMPTY
            val numOff = abs(rowNum - sensor.rowNum)
            val leftPoint = max(0, sensor.colNum - manhattan + numOff)
            val rightPoint = min(numToLookFor, sensor.colNum + manhattan - numOff)
            return leftPoint..rightPoint
        }

        fun menacingAura() = buildList {
            println(-(manhattan + 1)..(manhattan + 1))
            for (i in -(manhattan + 1)..(manhattan + 1)) {
                val rowNum = sensor.rowNum + i
                val colOffset = (manhattan + 1) - abs(i)
                val colNumRight = sensor.colNum + colOffset
                val colNumLeft = sensor.colNum - colOffset
                add(Point(rowNum, colNumRight))
                add(Point(rowNum, colNumLeft))
            }
        }.distinct()

    }

    fun visualize(map: Map<Point, Point>, radio: Set<Point>) {
        for (i in radio.minOf { it.rowNum }..radio.maxOf { it.rowNum }) {
            for (j in radio.minOf { it.colNum }..radio.maxOf { it.colNum }) {
                val p = Point(i, j)
                if (p in map.keys) print('S')
                else if (p in map.values) print('B')
                else if (p in radio) print('#')
                else if (i == 10) print(".")
                else print(' ')
            }
            println()
        }
    }

    fun parseInput(input: List<String>): Map<Point, Point> {
        return input.associate { line ->
            val (before, after) = line.split(":")
            val x1 = before.split("x=")[1].split(",")[0].toInt()
            val y1 = before.split("y=")[1].toInt()
            val x2 = after.split("x=")[1].split(",")[0].toInt()
            val y2 = after.split("y=")[1].toInt()
            Point(y1, x1) to Point(y2, x2)
        }
    }
}