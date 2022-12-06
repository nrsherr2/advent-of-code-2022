class Day06 {
    val part1TestExpected = 7
    val part2TestExpected = 19
    fun part1(input: List<String>): Int {
        //detect start of packet
        //start of packet is four characters in a row that are all different
        //wtf: number character where the window has 4 different letters
        val realInput = input[0]
        return realInput.windowed(4, 1).indexOfFirst { it.toCharArray().distinct().size == it.length } + 4
    }

    fun part2(input: List<String>): Int {
        val realInput = input[0]
        return realInput.windowed(14, 1).indexOfFirst { it.toCharArray().distinct().size == it.length } + 14
    }
}