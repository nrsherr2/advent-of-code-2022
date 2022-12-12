import kotlin.math.min

class Day12 {
    val part1TestExpected = 31
    val part2TestExpected = 29
    fun part1(input: List<String>): Int {
        val mountains = parseInput(input)
        val start = mountains.first { it.identifier!!.height == '`' }
        val end = mountains.first { it.identifier!!.height == '{' }
        val results = dijkstra(mountains, start, end)
        return results.distanceFromSource[end]!!
    }

    fun part2(input: List<String>): Int {
        val mountains = parseInput(input, true)
        val starts =
            mountains.filter { it.identifier!!.height == 'a' && !it.neighbors.all { it.identifier!!.height == 'a' } }
        println("There are ${starts.size} potential starting points")
        val end = mountains.first { it.identifier!!.height == '{' }
        var minDist = Int.MAX_VALUE
        starts.forEachIndexed { index, node ->
            println("starting point $index")
            val result = dijkstra(mountains, node, end)
            val distanceFromSource = result.distanceFromSource[end]!!
            minDist = min(minDist, distanceFromSource)
        }
        return minDist
    }

    private fun parseInput(input: List<String>, part2: Boolean = false): Set<Node<Mountain>> {
        val chars = input.flatMapIndexed { rowNum, line ->
            line.mapIndexed { colNum, it ->
                Mountain(
                    when (it) {
                        'S' -> 'a' - if (part2) 0 else 1
                        'E' -> 'z' + 1
                        else -> it
                    },
                    Point(rowNum, colNum)
                )
            }
        }
        val nodes = chars.map { Node(1, it) }
        nodes.forEach { m ->
            val potPoints =
                listOf(Point(-1, 0), Point(1, 0), Point(0, -1), Point(0, 1)).map { m.identifier!!.location + it }
            m.neighbors =
                nodes
                    .filter {
                        it.identifier!!.location in potPoints &&
                                when {
                                    m.identifier!!.height == 'S' -> it.identifier.height in listOf('a', 'b')
                                    it.identifier.height == 'E' -> m.identifier.height in listOf('y', 'z')
                                    else -> it.identifier.height - m.identifier.height <= 1
                                }
                    }
                    .toSet()
        }
        return nodes.toSet()
    }

    data class Mountain(var height: Char, val location: Point)
}