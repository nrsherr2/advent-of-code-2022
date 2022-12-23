class Day22 {
    val part1TestExpected = 6032
    val part2TestExpected = 5031

    fun part1(input: List<String>) = solve(input, 1)

    fun part2(input: List<String>) = solve(input, 2)

    fun solve(input: List<String>, part: Int): Int {
        val isTest = input.size == 14
        val availableDirections = mutableListOf(Point(0, 1), Point(1, 0), Point(0, -1), Point(-1, 0))
        val (pointVis, directions) = input.indexOfFirst { it.isBlank() }.let { input.slice(0 until it) to input.last() }
        val points = buildMap<Point, Boolean> {
            pointVis.forEachIndexed { rowNum, s ->
                s.forEachIndexed { colNum, c ->
                    if (c == '.') put(Point(rowNum, colNum), true)
                    if (c == '#') put(Point(rowNum, colNum), false)
                }
            }
        }
        val groups = points.entries.groupBy {
            Point(it.key.rowNum / (points.maxOf { it.key.rowNum } / (if (isTest) 3 else 4) + 1),
                it.key.colNum / (points.maxOf { it.key.colNum } / (if (isTest) 4 else 3) + 1))
        }.values.toList()
        assertEquals(6, groups.size)
        var currentDirection = Point(0, 1)
        var currentLocation = points.minBy { if (it.key.rowNum != 0) Int.MAX_VALUE else it.key.colNum }.key
        var str = ""
        val prevPoints = mutableMapOf<Point, Point>()
        directions.let {
            if (it.last().isLetter()) it
            else "$it "
        }.forEach {
            if (it.isDigit()) str += it
            else {
                var i = 0
                val sit = str.toInt()
                while (i < sit) {
                    val nextPoint = currentLocation + currentDirection
                    if (points.containsKey(nextPoint)) {
                        if (points[nextPoint]!!) {
                            currentLocation = nextPoint
                            i++
                            prevPoints[currentLocation] = currentDirection
                        } else i = sit
                    } else {
                        val (newCurrent, newI, newDir) = if (part == 1) walkOffTheEdgePart1(
                            currentDirection,
                            currentLocation,
                            points,
                            i,
                            prevPoints,
                            sit
                        ) else walkOffTheEdgePart2(
                            points,
                            currentDirection,
                            currentLocation,
                            i,
                            prevPoints,
                            sit,
                            isTest,
                            availableDirections,
                            groups
                        )
                        currentLocation = newCurrent
                        i = newI
                        currentDirection = newDir
                    }
                }
                currentDirection = when (it) {
                    'R' -> availableDirections.rotateRight()
                    'L' -> availableDirections.rotateLeft()
                    ' ' -> currentDirection
                    else -> TODO()
                }
                prevPoints[currentLocation] = currentDirection
                str = ""
            }
        }
        val finalLocation = currentLocation + Point(1, 1)
        val finalDirection = directionValue(currentDirection)
        println(finalLocation)
        println(finalDirection)
        return finalLocation.rowNum * 1000 + finalLocation.colNum * 4 + finalDirection
    }

    private fun directionValue(currentDirection: Point) = when (currentDirection) {
        Point(0, 1) -> 0 //right
        Point(1, 0) -> 1 //down
        Point(0, -1) -> 2 //left
        Point(-1, 0) -> 3 //up
        else -> TODO()
    }

    private fun walkOffTheEdgePart1(
        currentDirection: Point,
        currentLocation: Point,
        points: Map<Point, Boolean>,
        i: Int,
        prevPoints: MutableMap<Point, Point>,
        sit: Int
    ): Triple<Point, Int, Point> {
        var currentLocation1 = currentLocation
        var i1 = i
        val backItUp = currentDirection * -1
        var potPoint = currentLocation1 + backItUp
        while (points.containsKey(potPoint)) {
            potPoint += backItUp
        }
        potPoint += currentDirection
        if (points[potPoint]!!) {
            currentLocation1 = potPoint
            i1++
            prevPoints[currentLocation1] = currentDirection
        } else {
            i1 = sit
        }
        return Triple(currentLocation1, i1, currentDirection)
    }


    fun walkOffTheEdgePart2(
        points: Map<Point, Boolean>,
        currentDirection: Point,
        currentLocation: Point,
        i: Int,
        prevPoints: MutableMap<Point, Point>,
        sit: Int,
        isTest: Boolean,
        availableDirections: MutableList<Point>,
        groups: List<List<Map.Entry<Point, Boolean>>>
    ): Triple<Point, Int, Point> {
        val newDir: Point
        val source = groups.indexOfFirst { it.map { it.key }.contains(currentLocation) }
        val srcGroup = groups[source]
        val stdColNum = currentLocation.colNum - srcGroup.minCol()
        val stdRowNum = currentLocation.rowNum - srcGroup.minRow()
        val targetPoint: Point = if (isTest) {
//            println("$source ${directionValue(currentDirection)} $currentLocation $stdRowNum $stdColNum")
            when (source to directionValue(currentDirection)) {


                0 to 0 -> {
                    availableDirections.rotateLeft()
                    newDir = availableDirections.rotateRight()
                    Point(groups[5].maxRow() - stdRowNum, groups[5].maxCol())
                }

                0 to 2 -> {
                    newDir = availableDirections.rotateLeft()
                    Point(groups[2].minRow(), groups[2].minCol() + stdRowNum)
                }

                0 to 3 -> {
                    availableDirections.rotateRight()
                    newDir = availableDirections.rotateRight()
                    Point(groups[1].minRow(), groups[1].maxCol() - stdRowNum)
                }

                2 to 3 -> {
                    newDir = availableDirections.rotateRight()
                    Point(groups[0].minRow() + stdColNum, groups[0].minCol())
                }

                2 to 1 -> {
                    newDir = availableDirections.rotateLeft()
                    Point(groups[4].maxRow() - stdColNum, groups[4].minCol())
                }

                1 to 1 -> {
                    availableDirections.rotateRight()
                    newDir = availableDirections.rotateRight()
                    Point(groups[4].maxRow(), groups[4].maxCol() - stdColNum)
                }

                1 to 2 -> {
                    newDir = availableDirections.rotateRight()
                    Point(groups[5].maxRow(), groups[5].maxCol() - stdRowNum)
                }

                1 to 3 -> {
                    availableDirections.rotateRight()
                    newDir = availableDirections.rotateRight()
                    Point(groups[0].minRow(), groups[0].maxCol() - stdColNum)
                }

                3 to 0 -> {
                    newDir = availableDirections.rotateRight()
                    val newCol = groups[5].maxCol() - stdRowNum
                    val newRow = groups[5].minRow()
                    Point(newRow, newCol)
                }

                4 to 1 -> {
                    availableDirections.rotateRight()
                    newDir = availableDirections.rotateRight()
                    Point(groups[1].maxRow(), groups[1].maxCol() - stdColNum)
                }

                4 to 2 -> {
                    newDir = availableDirections.rotateRight()
                    Point(groups[2].maxRow(), groups[2].maxCol() - stdRowNum)
                }

                5 to 0 -> {
                    availableDirections.rotateRight()
                    newDir = availableDirections.rotateRight()
                    Point(groups[0].maxRow() - stdRowNum, groups[0].maxCol())
                }

                5 to 1 -> {
                    newDir = availableDirections.rotateLeft()
                    Point(groups[1].maxRow() - stdColNum, groups[1].minCol())
                }

                5 to 3 -> {
                    newDir = availableDirections.rotateLeft()
                    Point(groups[3].maxRow() - stdColNum, groups[3].maxCol())
                }

                else -> TODO()
            }
        } else {
            when (source to directionValue(currentDirection)) {
                0 to 2 -> {
                    availableDirections.rotateRight()
                    newDir = availableDirections.rotateRight()
                    Point(groups[3].maxRow() - stdRowNum, groups[3].minCol())
                }

                0 to 3 -> {
                    newDir = availableDirections.rotateRight()
                    Point(groups[5].minRow() + stdColNum, groups[5].minCol())
                }

                1 to 0 -> {
                    availableDirections.rotateRight()
                    newDir = availableDirections.rotateRight()
                    Point(groups[4].maxRow() - stdRowNum, groups[4].maxCol())
                }

                1 to 1 -> {
                    newDir = availableDirections.rotateRight()
                    Point(groups[2].minRow() + stdColNum, groups[2].maxCol())
                }

                1 to 3 -> {
                    newDir = currentDirection
                    Point(groups[5].maxRow(), groups[5].minCol() + stdColNum)
                }

                2 to 0 -> {
                    newDir = availableDirections.rotateLeft()
                    Point(groups[1].maxRow(), groups[1].minCol() + stdRowNum)
                }

                2 to 2 -> {
                    newDir = availableDirections.rotateLeft()
                    Point(groups[3].minRow(), groups[3].minCol() + stdRowNum)
                }

                3 to 2 -> {
                    availableDirections.rotateRight()
                    newDir = availableDirections.rotateRight()
                    Point(groups[0].maxRow() - stdRowNum, groups[0].minCol())
                }

                3 to 3 -> {
                    newDir = availableDirections.rotateRight()
                    Point(groups[2].minRow() + stdColNum, groups[2].minCol())
                }

                4 to 0 -> {
                    availableDirections.rotateRight()
                    newDir = availableDirections.rotateRight()
                    Point(groups[1].maxRow() - stdRowNum, groups[1].maxCol())
                }

                4 to 1 -> {
                    newDir = availableDirections.rotateLeft()
                    Point(groups[5].minRow() + stdColNum, groups[5].maxCol())
                }

                5 to 0 -> {
                    newDir = availableDirections.rotateLeft()
                    Point(groups[4].maxRow(), groups[4].minCol() + stdRowNum)
                }

                5 to 1 -> {
                    newDir = currentDirection
                    Point(groups[1].minRow(), groups[1].minCol() + stdColNum)
                }

                5 to 2 -> {
                    newDir = availableDirections.rotateLeft()
                    Point(groups[0].minRow(), groups[0].minCol() + stdRowNum)
                }

                else -> TODO((source to directionValue(currentDirection)).toString())
            }
        }
        if (!points.containsKey(targetPoint)) {
            visualize(points, prevPoints, targetPoint)
            throw IllegalArgumentException(targetPoint.toString())
        }
        if (points[targetPoint]!!) {
            prevPoints.put(currentLocation, currentDirection)
            return Triple(targetPoint, i + 1, newDir)
        } else {
            return Triple(currentLocation, sit, currentDirection)
        }
    }

    fun List<Map.Entry<Point, Boolean>>.minRow() = minOf { it.key.rowNum }
    fun List<Map.Entry<Point, Boolean>>.maxRow() = maxOf { it.key.rowNum }
    fun List<Map.Entry<Point, Boolean>>.maxCol() = maxOf { it.key.colNum }
    fun List<Map.Entry<Point, Boolean>>.minCol() = minOf { it.key.colNum }


    fun visualize(points: Map<Point, Boolean>, movements: Map<Point, Point>, currentLocation: Point) {
        println("***")
        for (rowNum in points.minOf { it.key.rowNum }..points.maxOf { it.key.rowNum }) {
            for (colNum in points.minOf { it.key.colNum }..points.maxOf { it.key.colNum }) {
                val p = Point(rowNum, colNum)
                if (p == currentLocation) print('0')
                else if (movements.containsKey(p)) {
                    when (movements[p]) {
                        Point(0, 1) -> print('>')
                        Point(1, 0) -> print('v')
                        Point(0, -1) -> print('<')
                        Point(-1, 0) -> print('^')
                    }
                } else if (points.containsKey(p)) {
                    when (points[p]) {
                        true -> print('.')
                        false -> print('#')
                        else -> TODO()
                    }
                } else print(' ')
            }
            println()
        }
    }

    fun <T> MutableList<T>.rotateRight(): T {
        val top = this.removeFirst()
        this.add(top)
        return this.first()
    }

    fun <T> MutableList<T>.rotateLeft(): T {
        val end = this.removeLast()
        this.add(0, end)
        return this.first()
    }
}