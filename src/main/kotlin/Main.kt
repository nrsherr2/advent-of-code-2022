import kotlin.system.measureTimeMillis

fun main(args: Array<String>) {
    val dayNum = 17.toString().padStart(2, '0')
    val day = Day17()
    val part1Only = true
    val part2HasDifferentInput = false
    val includeTesting = true

    if (includeTesting) {
        val testInput = readInput("Day${dayNum}_Test")
        val part1TestOutput = day.part1(testInput)
        assertEquals(day.part1TestExpected, part1TestOutput, 1)
        if (!part1Only) {
            val testInput2 = if (part2HasDifferentInput) readInput("Day${dayNum}_Test2") else testInput
            val part2TestOutput = day.part2(testInput2)
            assertEquals(day.part2TestExpected, part2TestOutput, 2)
        }
    }

    val actualInput = readInput("Day${dayNum}_Input")
    val timeToExecute = measureTimeMillis {
        val part1Output = day.part1(actualInput)
        val part2Output = if (part1Only) null else day.part2(actualInput)
        println(
            """
                |*** PART 1 ***
                |$part1Output
                |*** PART 2 ***
                |$part2Output
                |***  END  ***
            """.trimMargin()
        )
    }
    println("Processing time: ${timeToExecute}ms")
}