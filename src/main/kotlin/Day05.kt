class Day05 {
    val part1TestExpected = "CMZ"
    val part2TestExpected = "MCD"
    fun part1(input: List<String>): String {
        val (crates, instructions) = parse(input)
        instructions.forEach { instruction ->
            (1..instruction.numToMove).forEach {
                crates[instruction.dest - 1].addFirst(crates[instruction.src - 1].removeFirst())
            }
        }
        return crates.map { it.first() }.joinToString("")
    }

    fun part2(input: List<String>): String {
        val (crates, instructions) = parse(input)
        val crane = ArrayDeque<Char>()
        instructions.forEach { instruction ->
            (1..instruction.numToMove).onEach {
                crane.addFirst(crates[instruction.src - 1].removeFirst())
            }.forEach {
                crates[instruction.dest - 1].addFirst(crane.removeFirst())
            }
        }
        return crates.map { it.first() }.joinToString("")
    }

    private fun parse(input: List<String>): ParsedInput {
        val splitTime = input.indexOfFirst { it.isBlank() }
        val numStacks = input[splitTime - 1].trim().split("   ").size
        val stacks = List(numStacks) { ArrayDeque<Char>() }
        for (i in splitTime - 2 downTo 0) {
            for (j in 0..numStacks) {
                val charAt = input[i].getOrNull(j * 4 + 1)
                if (charAt != null && charAt != ' ') stacks[j].addFirst(charAt)
            }
        }
        val instructions =
            (splitTime + 1..input.lastIndex).map { input[it] }.filter { it.isNotBlank() }.map { line ->
                Instruction(
                    line.split(" from")[0].substring(5).toInt(),
                    line.split("from ")[1].split(" to")[0].toInt(),
                    line.split("to ")[1].toInt()
                )
            }
        return ParsedInput(stacks, instructions)
    }

    private data class ParsedInput(
        var crates: List<ArrayDeque<Char>>,
        var instructions: List<Instruction>
    )

    private data class Instruction(
        var numToMove: Int,
        var src: Int,
        var dest: Int
    )
}
