import java.io.File
import kotlin.math.abs

/**
 * Reads lines from the given input txt file.
 */
fun readInput(name: String) = File("src/main/resources", "$name.txt").readLines()
fun assertEquals(expected: Any?, condition: Any?, part: Int? = null) {
    require(condition == expected) { "Test Failed! Expected $expected, Received $condition ${if (part == null) "" else "on part $part"}" }
}

fun String.toSortedString() = toCharArray().sorted().joinToString("")

data class Point(var rowNum: Int, var colNum: Int) {
    val y = rowNum
    val x = colNum
}

data class Point3D(var rowNum: Int, var colNum: Int, var spaceNum: Int) {
    val x = colNum
    val y = rowNum
    val z = spaceNum
}

fun <T> List<List<T>>.access(point: Point) = when {
    point.rowNum < 0 || point.rowNum >= size -> null
    point.colNum < 0 || point.colNum >= get(point.rowNum).size -> null
    else -> this[point.rowNum][point.colNum]
}

fun <T> List<List<List<T>>>.access(point: Point3D) = when {
    point.rowNum < 0 || point.rowNum >= size -> null
    point.colNum < 0 || point.colNum >= get(point.rowNum).size -> null
    point.spaceNum < 0 || point.spaceNum >= get(point.rowNum)[point.colNum].size -> null
    else -> this[point.rowNum][point.colNum][point.spaceNum]
}


fun manhattanDistance(p1: Point, p2: Point) = abs(p1.x - p2.x) + abs(p1.y - p2.y)
fun manhattanDistance(p1: Point3D, p2: Point3D) = abs(p1.x - p2.x) + abs(p1.y - p2.y) + abs(p1.z - p2.z)


data class Neighbors<T>(
    val topLeft: T?,
    val topMid: T?,
    val topRight: T?,
    val midLeft: T?,
    val theObj: T,
    val midRight: T?,
    val botLeft: T?,
    val botMid: T?,
    val botRight: T?
)

fun <T> List<List<T>>.neighborsOf(point: Point) = Neighbors(
    access(point.copy(rowNum = point.rowNum - 1, colNum = point.colNum - 1)),
    access(point.copy(rowNum = point.rowNum - 1)),
    access(point.copy(rowNum = point.rowNum - 1, colNum = point.colNum + 1)),
    access(point.copy(colNum = point.colNum - 1)),
    access(point),
    access(point.copy(colNum = point.colNum - 1)),
    access(point.copy(rowNum = point.rowNum + 1, colNum = point.colNum - 1)),
    access(point.copy(rowNum = point.rowNum + 1, colNum = point.colNum)),
    access(point.copy(rowNum = point.rowNum + 1, colNum = point.colNum + 1)),
)

data class Node(val costToEnter: Int, val identifier: String? = null, var neighbors: Set<Node> = emptySet())

data class DijkstraResults(
    val grid: Set<Node>,
    val source: Node,
    val end: Node,
    val distanceFromSource: Map<Node, Int>,
    val branchMap: Map<Node, Node?>
)

fun dijkstra(grid: Set<Node>, source: Node, end: Node): DijkstraResults {
    val unusedNodes = mutableSetOf<Node>()
    val dist = mutableMapOf<Node, Int>()
    val hops = mutableMapOf<Node, Node?>()
    grid.forEach { n ->
        dist[n] = Int.MAX_VALUE
        hops[n] = null
        unusedNodes.add(n)
    }
    dist[source] = 0

    while (unusedNodes.isNotEmpty()) {
        if (unusedNodes.size % 10 == 0) println(unusedNodes.size)
        val minDist = dist.filter { it.key in unusedNodes }.minByOrNull { it.value }!!.key
        unusedNodes.remove(minDist)
        val unusedNeighbors = minDist.neighbors.filter { it in unusedNodes }
        unusedNeighbors.forEach {
            val alt = dist[minDist]!! + it.costToEnter
            if (alt < dist[it]!!) {
                dist[it] = alt
                hops[it] = minDist
            }
        }
    }
    return DijkstraResults(grid, source, end, dist, hops)
}