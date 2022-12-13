class Day13 {
    val part1TestExpected = 13
    val part2TestExpected = 140
    fun part1(input: List<String>): Int {
        return input.chunked(3).asSequence().map {
            parseLine(it[0]) to parseLine(it[1])
        }.mapIndexed { index, (left, right) ->
            val res = compareSides(left, right) ?: throw IllegalArgumentException()
            if (res) index + 1 else 0
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val baseLines = input.filter { it.isNotBlank() }.map { parseLine(it) }
        val two = parseLine("[[2]]")
        val six = parseLine("[[6]]")
        val indexTwo = baseLines.count { compareSides(it, two) } + 1
        val indexSix = baseLines.count { compareSides(it, six) } + 2
        return indexTwo * indexSix
    }

    private fun compareSides(left: List<Any>, right: List<Any>): Boolean {
        return compareInner(left, right) ?: true
    }

    private fun compareInner(left: List<Any>, right: List<Any>, depth: Int = 0): Boolean? {
        val lQ = left.toMutableList()
        val rQ = right.toMutableList()
        while (lQ.isNotEmpty()) {
            if (rQ.isEmpty()) return false
            val li = lQ.removeFirst()
            val ri = rQ.removeFirst()
            when (li) {
                is Int -> {
                    when (ri) {
                        is Int -> {
                            if (li < ri) return true
                            if (li > ri) return false
                        }

                        is List<*> -> {
                            val list = listOf(li)
                            when (compareInner(list, ri as List<Any>, depth + 1)) {
                                false -> return false
                                true -> return true
                                else -> {}
                            }
                        }
                    }
                }

                is List<*> -> {
                    when (ri) {
                        is Int -> {
                            val list = listOf(ri)
                            when (compareInner(li as List<Any>, list)) {
                                false -> return false
                                true -> return true
                                else -> {}
                            }
                        }

                        is List<*> -> {
                            when (compareInner(li as List<Any>, ri as List<Any>)) {
                                false -> return false
                                true -> return true
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
        return if (rQ.isEmpty()) null else true
    }

    fun parseLine(input: String): List<Any> {
        return readInsideBracket(input, 0).first
    }

    private fun readInsideBracket(line: String, startingIdx: Int): Pair<List<Any>, Int> {
        var iterator = startingIdx
        val elements = mutableListOf<Any>()
        try {
            while (line[iterator] != ']') {
                iterator++
                when (line[iterator]) {
                    ']' -> {}
                    '[' -> {
                        val (subList, newIdx) = readInsideBracket(line, iterator)
                        iterator = newIdx + 1
                        elements.add(subList)
                    }

                    else -> {
                        val num = buildString {
                            while (line[iterator].isDigit()) {
                                append(line[iterator])
                                iterator++
                            }
                        }
                        elements.add(num.toInt())
                    }
                }
            }
            return elements to iterator
        } catch (e: Exception) {
            println(line)
            println(iterator)
            println(line[iterator])
            throw e
        }
    }
}