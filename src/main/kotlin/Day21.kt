import com.fathzer.soft.javaluator.DoubleEvaluator
import com.fathzer.soft.javaluator.StaticVariableSet

class Day21 {
    val part1TestExpected = 152L
    val part2TestExpected = 301L
    fun part1(input: List<String>): Long {
        var splitList = input.splitNumbersAndExpressions()
        while (splitList.second.isNotEmpty()) {
            val numMap = splitList.first.associate { line -> line.split(": ").let { it[0] to it[1] } }
            val splitNewSecond = splitList.second.map { line -> line.split(" ").joinToString(" ") { numMap[it] ?: it } }
                .map { ln ->
                    val spl = ln.split(": ")
                    val sr = spl[1].let { expr ->
                        if (expr.any { it.isLetter() }) expr
                        else evalExpression(expr)
                    }
                    "${spl[0]}: $sr"
                }
            splitList = splitNewSecond.splitNumbersAndExpressions()
        }
        return splitList.first.first().split(": ")[1].toLong()
    }

    fun part2(input: List<String>): Long {


        val mut = input.toMutableList()
        var expr = mut.first { it.startsWith("root:") }
        mut.remove(expr)
        expr = expr.replace("+", "=")
        while (mut.size > 1) {
            expr = expr.split(" ").joinToString(" ") { word ->
                if (word.any { !it.isLetter() } || word == "X") return@joinToString word
                val ln = try {
                    mut.first { it.startsWith(word) }
                } catch (e: NoSuchElementException) {
                    throw e
                }
                mut.remove(ln)
                if (ln.startsWith("humn:"))
                    "X"
                else
                    "( ${ln.split(": ")[1]} )"
            }
        }
        val (left, right) = expr.split(": ")[1].split("=").map { it.replace(" ", "") }.let {
            it.first { it.contains("X") } to it.first { !it.contains("X") }
        }
        val guessr = DoubleEvaluator()
        val expected = guessr.evaluate(right)
        var stepper = 1_000_000_000_000_000
        var lb = Long.MIN_VALUE
        var ub = Long.MAX_VALUE
        while (stepper > 0) {
            run {
                val checkGreater = guessr.evaluate(
                    left,
                    StaticVariableSet<Double>().apply { set("X", lb.toDouble()) }) < guessr.evaluate(
                    left,
                    StaticVariableSet<Double>().apply { set("X", (lb + stepper).toDouble()) })
                for (i in lb..ub step stepper) {
                    val r = guessr.evaluate(left, StaticVariableSet<Double>().apply { set("X", i.toDouble()) })
                    if ((checkGreater && r > expected) || (!checkGreater && r < expected)) {
                        ub = i
                        return@run
                    } else if (r == expected) {
                        return i
                    } else lb = i
                }
            }
            stepper /= 10
        }
        TODO("WE CAN'T FIND SHIT")
    }

    private fun evalExpression(expr: String): String {
        val (left, operator, right) = expr.split(" ")
        return when (operator) {
            "+" -> left.toLong() + right.toLong()
            "-" -> left.toLong() - right.toLong()
            "*" -> left.toLong() * right.toLong()
            "/" -> left.toLong() / right.toLong()
            else -> TODO()
        }.toString()
    }

    private fun List<String>.splitNumbersAndExpressions() =
        partition { it.split(": ")[1].let { it.filter { it.isDigit() }.length == it.length } }
}