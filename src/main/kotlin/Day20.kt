class Day20 {
    val part1TestExpected = 3
    val part2TestExpected = 1623178306L
    fun part1(input: List<String>): Int {
        val numbers = input.map { it.toInt() }
        val list = MutableList(numbers.size) { it }
//        println(list.map { numbers[it] })
        numbers.forEachIndexed { sInd, i ->
            val idx = list.indexOf(sInd)
            list.shift(idx, i)
//            println(list.map { numbers[it] })
        }
        val endList = list.map { numbers[it] }
        val idx0 = endList.indexOf(0)
        return (1000 until 4000 step 1000).sumOf { endList[(idx0 + it) % endList.size] }
    }

    fun MutableList<Int>.shift(indexFrom: Int, amt: Int) = this.shift(indexFrom, amt.toLong())
    fun MutableList<Int>.shift(indexFrom: Int, amt: Long) {
        val sz = this.size
        val item = this.removeAt(indexFrom)
        val newIndex = when (indexFrom + amt) {
            in Long.MIN_VALUE..-1 -> ((indexFrom + amt) % size) + size
            in size..Long.MAX_VALUE -> (indexFrom + amt) % size
            else -> indexFrom + amt
        }
        this.add(newIndex.toInt(), item)
    }

    fun part2(input: List<String>): Long {
        val numbers = input.map { it.toLong() * 811589153 }
        val list = MutableList(numbers.size) { it }
        for (i in 1..10) {
            numbers.forEachIndexed { sInd, i ->
                val idx = list.indexOf(sInd)
                list.shift(idx, i)
            }
        }
        val endList = list.map { numbers[it] }
        val idx0 = endList.indexOf(0)
        return (1000 until 4000 step 1000).sumOf { endList[(idx0 + it) % endList.size] }
    }
}