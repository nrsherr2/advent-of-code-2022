class Day18 {
    val part1TestExpected = 64
    val part2TestExpected = 58
    fun part1(input: List<String>): Int {
        val points = input.map { line ->
            line.split(",").map { it.toInt() }.let {
                Frag(it[0], it[1], it[2])
            }
        }
        return points.sumOf { frag -> frag.neighbors().count { !points.contains(it) } }
    }

    fun part2(input: List<String>): Int {
        val points = input.map { line ->
            line.split(",").map { it.toInt() }.let {
                Frag(it[0], it[1], it[2])
            }
        }
        val minX = points.minOf { it.x }
        val minY = points.minOf { it.y }
        val minZ = points.minOf { it.z }
        val maxX = points.maxOf { it.x }
        val maxY = points.maxOf { it.y }
        val maxZ = points.maxOf { it.z }
        val connectedToOutside = mutableListOf<Frag>()
        return points.asSequence().flatMapIndexed { idx, it ->
            println("$idx: $it")
            it.neighbors()
        }.filter { !points.contains(it) }.count { ne ->
            println(ne)
            var bubble = listOf(ne)
            val totalBubble = mutableListOf(ne)
            while (bubble.isNotEmpty()) {
                bubble = bubble.flatMap { it.neighbors() }.distinct()
                    .filter { !points.contains(it) && !totalBubble.contains(it) }
                totalBubble += bubble
                println("bubble: ${bubble.size}\t total: ${totalBubble.size}")
                if (bubble.any {
                        it in connectedToOutside ||
                                it.x == maxX || it.x == minX ||
                                it.y == maxY || it.y == minY ||
                                it.z == maxZ || it.z == minZ
                    }) {
                    connectedToOutside.addAll(totalBubble)
                    return@count true
                }
            }
            false
        }

    }

    fun Frag.neighbors(): List<Frag> {
        val up = this + Frag(1, 0, 0)
        val down = this + Frag(-1, 0, 0)
        val left = this + Frag(0, 1, 0)
        val right = this + Frag(0, -1, 0)
        val back = this + Frag(0, 0, 1)
        val front = this + Frag(0, 0, -1)
        return listOf(up, down, left, right, back, front)
    }

}

typealias Frag = Point3D