class Day09 {
    val part1TestExpected = 13
    val part2TestExpected = 36
    fun part1(input: List<String>): Int {
        val head = Head()
        input.forEach { line ->
            val dir = line.first()
            (1..line.split(" ")[1].toInt()).forEach {
                head.move(dir)
            }
        }
//        head.visualize()
        return (head.follower.getLowestHistory() + head.follower.getDownPoints().first()).size
    }

    fun part2(input: List<String>): Int {
        val head = Head(true)
        input.forEach { line ->
            val dir = line.first()
            (1..line.split(" ")[1].toInt()).forEach {
                head.move(dir)
            }
        }
//        head.visualize()
        return (head.follower.getLowestHistory() + head.follower.getDownPoints().last()).size
    }

    class Head(multipleFollowers: Boolean = false) {
        private var location: Point = Point(0, 0)
        private val maxFollowerLayer = 9
        val follower = if (multipleFollowers) Tail(true, maxFollowerLayer) else Tail()

        fun move(direction: Char) {
//            println("$direction")
            val newPoint = when (direction) {
                'U' -> Point(location.rowNum + 1, location.colNum)
                'D' -> Point(location.rowNum - 1, location.colNum)
                'R' -> Point(location.rowNum, location.colNum + 1)
                'L' -> Point(location.rowNum, location.colNum - 1)
                else -> TODO()
            }
            location = newPoint
            follower.moveIf(newPoint)
        }


        fun visualize() {
            val lh = follower.getLowestHistory()
            val dp = follower.getDownPoints()
            val locations = dp + location + follower.getLowestHistory()
            println("***")
            for (i in locations.minOf { it.rowNum }..locations.maxOf { it.rowNum }) {
                for (j in locations.minOf { it.colNum }..locations.maxOf { it.colNum }) {
                    val char = when (Point(i, j)) {
                        location -> 'H'
                        in dp -> (dp.indexOf(Point(i, j)) + 1)
                        in lh -> '#'
                        else -> '.'
                    }
                    print("$char ")
                }
                println()
            }
        }

    }

    class Tail(hasFollower: Boolean = false, maxFollowerLayer: Int = -1, followerLayer: Int = 1) {
        var location: Point = Point(0, 0)
        val prevPoints = mutableSetOf<Point>()
        private var follower: Tail? = if (!hasFollower || followerLayer == maxFollowerLayer) null
        else Tail(true, maxFollowerLayer, followerLayer + 1)

        fun getLowestHistory(): Set<Point> = (follower?.getLowestHistory()) ?: prevPoints

        fun getDownPoints(): Set<Point> {
            return emptySet<Point>() + location + (follower?.getDownPoints() ?: emptyList())
        }

        fun moveIf(newPoint: Point) {
            val neighbors = (-1..1).flatMap { l -> (-1..1).map { r -> l to r } }
                .map { a -> location + Point(a.first, a.second) }
            if (newPoint in neighbors) return
            val movePoint = location + (listOf(
                Point(-1, 0),
                Point(1, 0),
                Point(0, -1),
                Point(0, 1)
            ).firstOrNull { newPoint == location + (it * 2) }
                ?: Point(
                    if (newPoint.rowNum > location.rowNum) 1 else -1,
                    if (newPoint.colNum > location.colNum) 1 else -1
                ))
            prevPoints.add(location.copy())
            location = movePoint
            follower?.moveIf(movePoint)
        }
    }
}