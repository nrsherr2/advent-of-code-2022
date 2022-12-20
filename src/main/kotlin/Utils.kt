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
    operator fun plus(other: Point): Point = Point(this.rowNum + other.rowNum, this.colNum + other.colNum)

    operator fun times(projector: Int) = Point(rowNum * projector, colNum * projector)
}

data class PointL(var rowNum: Long, var colNum: Long) {
    val y = rowNum
    val x = colNum
    operator fun plus(other: PointL): PointL = PointL(this.rowNum + other.rowNum, this.colNum + other.colNum)

    operator fun times(projector: Int) = PointL(rowNum * projector, colNum * projector)
}

data class Point3D(var rowNum: Int, var colNum: Int, var spaceNum: Int) {
    val x = colNum
    val y = rowNum
    val z = spaceNum

    operator fun plus(other: Point3D) =
        Point3D(this.rowNum + other.rowNum, this.colNum + other.colNum, this.spaceNum + other.spaceNum)
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
) {
    fun toList(includeObj: Boolean = false) = listOfNotNull(
        topLeft,
        topMid,
        topRight,
        midLeft,
        midRight,
        botLeft,
        botMid,
        botRight,
        theObj?.takeIf { includeObj })

    fun orthogonalList() = listOfNotNull(topMid, midLeft, midRight, botMid)
}

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

class Node<T>(val costToEnter: Int, val identifier: T? = null, var neighbors: Set<Node<T>> = emptySet()) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Node<*>

        if (costToEnter != other.costToEnter) return false
        if (identifier != other.identifier) return false

        return true
    }

    override fun hashCode(): Int {
        var result = costToEnter
        result = 31 * result + (identifier?.hashCode() ?: 0)
        return result
    }
}

data class DijkstraResults<T>(
    val grid: Set<Node<T>>,
    val source: Node<T>,
    val end: Node<T>,
    val distanceFromSource: Map<Node<T>, Int>,
    val branchMap: Map<Node<T>, Node<T>?>
)

fun <T> dijkstra(grid: Set<Node<T>>, source: Node<T>, end: Node<T>): DijkstraResults<T> {
    val unusedNodes = mutableSetOf<Node<T>>()
    val dist = mutableMapOf<Node<T>, Int>()
    val hops = mutableMapOf<Node<T>, Node<T>?>()
    grid.forEach { n ->
        dist[n] = Int.MAX_VALUE
        hops[n] = null
        unusedNodes.add(n)
    }
    dist[source] = 0

    while (unusedNodes.isNotEmpty()) {
//        if (unusedNodes.size % 10 == 0) println(unusedNodes.size)
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