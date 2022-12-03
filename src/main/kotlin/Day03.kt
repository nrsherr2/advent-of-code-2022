class Day03 {
    val part1TestExpected = 157
    val part2TestExpected = 70
    fun part1(input: List<String>): Int {
        return input.sumOf { line ->
            val leftHalf = line.take(line.length / 2)
            val rightHalf = line.takeLast(line.length / 2)
            val commonCharacters = leftHalf.filter { it in rightHalf }.toCharArray().distinct()
            commonCharacters.sumOf { ch ->
                if (ch.isUpperCase()) ch - 'A' + 27 else ch - 'a' + 1
            }
        }
    }

    fun part2(input: List<String>): Int {
        val groups = input.windowed(3, 3)
        return groups.sumOf { (line1, line2, line3) ->
            val commonAcrossAll3 = line1.first { it in line2 && it in line3 }
            if(commonAcrossAll3.isUpperCase()) commonAcrossAll3 - 'A' + 27 else commonAcrossAll3 - 'a' + 1
        }
    }
}