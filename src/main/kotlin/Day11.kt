class Day11 {
    val part1TestExpected = 10605L
    val part2TestExpected = 2713310158L
    fun part1(input: List<String>): Long {
        val monkeys = parseInput(input)
        monkeys.forEach { it.test.initTargets(monkeys) }
        for (i in 1..20) {
            monkeys.onEach { it.inspect() }
        }
        return monkeys.sortedByDescending { it.numInspections }.take(2)
            .let { (a, b) -> a.numInspections * b.numInspections }
    }

    fun part2(input: List<String>): Long {
        val monkeys = parseInput(input)
        monkeys.forEach { it.test.initTargets(monkeys) }
        val lcm = monkeys.fold(1L) { acc, monke -> acc * monke.test.comparison }
        for (i in 1..10000) {
            monkeys.onEach { it.inspectPart2(lcm) }
        }
        return monkeys.sortedByDescending { it.numInspections }.take(2)
            .let { (a, b) -> a.numInspections * b.numInspections }
    }

    private fun parseInput(input: List<String>): List<Monke> {
        return input.chunked(7).map { group ->
            Monke(
                id = group[0].filter { it.isDigit() }.toInt(),
                items = group[1].split(": ")[1].split(", ").map { it.toLong() }.toMutableList(),
                operation = group[2].split(" ").takeLast(2).let { (op, num) ->
                    Operation(
                        operation = when (op) {
                            "+" -> OperationEnum.ADD
                            "*" -> OperationEnum.MUL
                            else -> TODO()
                        },
                        factor = if (num == "old") null else num.toLong()
                    )
                },
                test = Test(
                    group[3].split(" ").last().toLong(),
                    group[4].split(" ").last().toInt(),
                    group[5].split(" ").last().toInt()
                )
            )
        }
    }

    data class Monke(
        val id: Int,
        val items: MutableList<Long>,
        val operation: Operation,
        val test: Test,
        var numInspections: Long = 0
    ) {
        fun inspect() {
            while (items.isNotEmpty()) {
                val item = items.removeFirst()
                var inspectionLevel = when (operation.operation) {
                    OperationEnum.ADD -> item + (operation.factor ?: item)
                    OperationEnum.MUL -> item * (operation.factor ?: item)
                }
                inspectionLevel /= 3
                val isDivis = (inspectionLevel % test.comparison) == 0L
                val monkeyToThrowTo = if (isDivis) test.throwToOnTrue!! else test.throwToOnFalse!!
                monkeyToThrowTo.items.add(inspectionLevel)
                numInspections++
            }
        }
        fun inspectPart2(lcm:Long) {
            while (items.isNotEmpty()) {
                val item = items.removeFirst()
                var inspectionLevel = when (operation.operation) {
                    OperationEnum.ADD -> item + (operation.factor ?: item)
                    OperationEnum.MUL -> item * (operation.factor ?: item)
                }
                inspectionLevel %= lcm
                val isDivis = (inspectionLevel % test.comparison) == 0L
                val monkeyToThrowTo = if (isDivis) test.throwToOnTrue!! else test.throwToOnFalse!!
                monkeyToThrowTo.items.add(inspectionLevel)
                numInspections++
            }
        }
    }

    data class Operation(val operation: OperationEnum, val factor: Long?)

    enum class OperationEnum { ADD, MUL }

    class Test(
        val comparison: Long,
        private val throwToOnTrueNum: Int,
        private val throwToOnFalseNum: Int
    ) {
        var throwToOnTrue: Monke? = null
        var throwToOnFalse: Monke? = null

        fun initTargets(monkeys: List<Monke>) {
            this.throwToOnTrue = monkeys[this.throwToOnTrueNum]
            this.throwToOnFalse = monkeys[this.throwToOnFalseNum]
        }

        override fun toString(): String {
            return "Test(comparison=$comparison, throwToOnTrue=Monkey$throwToOnTrueNum, throwToOnFalse=Monkey$throwToOnFalseNum)"
        }

    }
}