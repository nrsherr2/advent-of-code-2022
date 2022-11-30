import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val dayNum = 1.toString().padStart(2, '0')
    val day = Day01()
    val part1Only = false
    val includeTesting = true
    val part1TestExpected = -1
    val part2TestExpected = -1

    if (includeTesting) {
        val testInput = readInput("Day${dayNum}_Test")
        val part1TestOutput = day.part1(testInput)
        assertEquals(part1TestExpected, part1TestOutput, 1)
        if (!part1Only) {
            val part2TestOutput = day.part2(testInput)
            assertEquals(part2TestExpected, part2TestOutput, 2)
        }
    }

    val actualInput = readInput("Day${dayNum}_Test")
    val timeToExecute = measureTimeMillis {
        val part1Output = day.part1(actualInput)
        val part2Output = if (part1Only) null else day.part2(actualInput)
        println(
            """
                *** PART 1 ***
                $part1Output
                *** PART 2 ***
                $part2Output
                ***  END  ***
            """.trimIndent()
        )
    }
    println("Processing time: ${timeToExecute}ms")
}