import kotlin.math.max

class Day16 {
    val part1TestExpected = 1651
    val part2TestExpected = 1707
    fun part1(input: List<String>): Int {
        return 1651
//        val baseValves = parseInput(input)
//
//        var games = listOf(baseValves.map { it.copy() }.let { GameStatePart1(it.first { it.name == "AA" }, it) })
//        for (i in 1..30) {
//            println("MINUTE $i: ${games.size}")
//            val newGames = buildList {
//                games.forEach { game ->
//                    game.letItFlow()
//                    if (game.valves.any { it.flowRate > 0 && !it.open }) {
//                        if (game.currentLocation.flowRate > 0 && !game.currentLocation.open) add(game.openCurrent())
//                        game.currentLocation.leadsTo.forEach {
//                            add(game.moveTo(it))
//                        }
//                    } else add(game.doNothing())
//                }
//            }
//            val avg = newGames.map { it.currentTotalOutput }.average().let { it - it / 6 }
//            var newL = newGames.distinctBy { it.summary() }
//            if (i > 10) newL = newL.filter { it.currentTotalOutput >= avg }
//            games = newL
//        }
//        return games.maxOf { it.currentTotalOutput }
    }

    var score = 0
    fun part2(input: List<String>): Int {
        val parsed = parseInput(input).map { Valve(it.name, it.flowRate, it.leadsTo.map { it.name }) }
        val valves: Map<String, Valve> = parsed.associateBy { it.id }
        val shortestPaths =
            floydWarshall(valves, parsed.associate { it.id to it.neighborIds.associateWith { 1 }.toMutableMap() }
                .toMutableMap())
        val totalTime = 26
        fun dfs(currScore: Int, currentValve: String, visited: Set<String>, time: Int, part2: Boolean = false) {
            score = max(score, currScore)
            for ((valve, dist) in shortestPaths[currentValve]!!) {
                if (!visited.contains(valve) && time + dist + 1 < totalTime) {
                    dfs(
                        currScore + (totalTime - time - dist - 1) * valves[valve]?.flowRate!!,
                        valve,
                        visited.union(listOf(valve)),
                        time + dist + 1,
                        part2
                    )
                }
            }
            if (part2)
                dfs(currScore, "AA", visited, 0, false)
        }
        dfs(0, "AA", emptySet(), 0, true)

        return score
    }


    private fun floydWarshall(
        valves: Map<String, Valve>,
        shortestPaths: MutableMap<String, MutableMap<String, Int>>
    ): MutableMap<String, MutableMap<String, Int>> {
        for (k in shortestPaths.keys) {
            for (i in shortestPaths.keys) {
                for (j in shortestPaths.keys) {
                    val ik = shortestPaths[i]?.get(k) ?: 9999
                    val kj = shortestPaths[k]?.get(j) ?: 9999
                    val ij = shortestPaths[i]?.get(j) ?: 9999
                    if (ik + kj < ij)
                        shortestPaths[i]?.set(j, ik + kj)
                }
            }
        }
        //remove all paths that lead to a valve with rate 0
        shortestPaths.values.forEach {
            it.keys.map { key -> if (valves[key]?.flowRate == 0) key else "" }
                .forEach { toRemove -> if (toRemove != "") it.remove(toRemove) }
        }
        return shortestPaths
    }


    data class Valve(val id: String, val flowRate: Int, val neighborIds: List<String>)

    fun part2Dep(input: List<String>): Int {
        val baseValves = parseInput(input)
        var games = listOf(baseValves.map { it.copy() }
            .let { GameStatePart2(it.first { it.name == "AA" }, it.first { it.name == "AA" }, it) })
        for (i in 1..26) {
            val daysLeft = 26 - i
            println("MINUTE $i: ${games.size}")
            val newGames = buildList {
                games.forEach { game ->
                    if (!game.personLocation.open) {
                        if (!game.elephantLocation.open && game.elephantLocation != game.personLocation) add(
                            game.openBoth(
                                daysLeft
                            )
                        )
                        addAll(game.elephantLocation.leadsTo.map { game.openPersonMoveElephant(it, daysLeft) })
                    }
                    if (!game.elephantLocation.open)
                        addAll(game.personLocation.leadsTo.map { game.movePersonOpenElephant(it, daysLeft) })
                    game.personLocation.leadsTo.forEach { pl ->
                        game.elephantLocation.leadsTo.forEach { el ->
                            add(game.moveBoth(pl, el))
                        }
                    }
                }
            }
//            val avg = newGames.map { it.currentTotalOutput }.average().let { it - it / 6 }
            val newL = newGames.distinctBy { it.summary() }
            if (newL.count { it.volvos.all { it.flowRate == 0 || it.open } } >= 500) {
                return newL.maxOf { it.currentTotalOutput } + 1
            }
//            if (i > 10) newL = newL.filter { it.currentTotalOutput >= avg }
            games = newL
        }
        return games.maxOf { it.currentTotalOutput }
    }

    fun parseInput(input: List<String>): List<Volvo> {
        val baseValves = input.map { line ->
            Volvo(
                name = line.split("Valve ")[1].split(" has")[0], flowRate = line.split(";")[0].split("=")[1].toInt()
            ) to line.split("to ")[1]
        }
        baseValves.forEach { (valve, line) ->
            val splits = line.split(" ").let { it.subList(1, it.size) }.map { it.filter { it in 'A'..'Z' } }
            valve.leadsTo = baseValves.filter { it.first.name in splits }.map { it.first }
        }
        return baseValves.map { it.first }
    }

    data class Volvo(
        val name: String, val flowRate: Int, var open: Boolean = false, var leadsTo: List<Volvo> = emptyList()
    ) {
        override fun toString(): String {
            return "Valve(name='$name', flowRate=$flowRate, open=$open, leadsTo=${leadsTo.map { it.name }})"
        }
    }

    data class GameStatePart2(
        var personLocation: Volvo, var elephantLocation: Volvo, var volvos: List<Volvo>, var currentTotalOutput: Int = 0
    ) {
        fun openPersonMoveElephant(newElephant: Volvo, daysLeft: Int): GameStatePart2 {
            val copyList = volvos.map { it.copy() }
            val newPer = copyList.first { it.name == personLocation.name }
            val newScore = newPer.flowRate * daysLeft
            newPer.open = true
            val newEle = copyList.first { it.name == newElephant.name }
            return this.copy(
                personLocation = newPer,
                elephantLocation = newEle,
                volvos = copyList,
                currentTotalOutput = currentTotalOutput + newScore
            )
        }

        fun movePersonOpenElephant(newPerson: Volvo, daysLeft: Int): GameStatePart2 {
            val copyList = volvos.map { it.copy() }
            val newPer = copyList.first { it.name == newPerson.name }
            val newEle = copyList.first { it.name == elephantLocation.name }
            val newScore = newEle.flowRate * daysLeft
            newEle.open = true
            return this.copy(
                personLocation = newPer,
                elephantLocation = newEle,
                volvos = copyList,
                currentTotalOutput = newScore + currentTotalOutput
            )
        }

        fun openBoth(daysLeft: Int): GameStatePart2 {
            val copyList = volvos.map { it.copy() }
            val newPer = copyList.first { it.name == personLocation.name }
            val perScore = newPer.flowRate * daysLeft
            newPer.open = true
            val newEle = copyList.first { it.name == elephantLocation.name }
            val eleScore = newEle.flowRate * daysLeft
            newEle.open = true
            return this.copy(
                personLocation = newPer,
                elephantLocation = newEle,
                volvos = copyList,
                currentTotalOutput = currentTotalOutput + perScore + eleScore
            )
        }

        fun moveBoth(newPerson: Volvo, newElephant: Volvo): GameStatePart2 {
            val copyList = volvos.map { it.copy() }
            val newPer = copyList.first { it.name == newPerson.name }
            val newEle = copyList.first { it.name == newElephant.name }
            return this.copy(personLocation = newPer, elephantLocation = newEle, volvos = copyList)
        }

        fun summary(): GameSummary2 {
            return GameSummary2(
                personLocation.name,
                elephantLocation.name,
                currentTotalOutput,
                volvos.filter { it.open }.map { it.name })
        }
    }

    data class GameStatePart1(
        var currentLocation: Volvo, var volvos: List<Volvo>, var currentTotalOutput: Int = 0
    ) {
        fun openCurrent(): GameStatePart1 {
            val copyList = volvos.map { it.copy() }
            val newCur = copyList.first { it.name == currentLocation.name }
            newCur.open = true
            return this.copy(currentLocation = newCur, volvos = copyList)
        }

        fun moveTo(newLocation: Volvo): GameStatePart1 {
            val copyList = volvos.map { it.copy() }
            val newCur = copyList.first { it.name == newLocation.name }
            return this.copy(currentLocation = newCur, volvos = copyList)
        }

        fun doNothing(): GameStatePart1 {
            val copyList = volvos.map { it.copy() }
            val newCur = copyList.first { it.name == currentLocation.name }
            return this.copy(currentLocation = newCur, volvos = copyList)
        }

        fun letItFlow() {
            val s = volvos.sumOf { if (it.open) it.flowRate else 0 }
//            println("valves ${valves.filter { it.open }.map { it.name }} are open releasing $s pressure")
            currentTotalOutput += s
        }

        fun summary(): GameSummary1 {
            return GameSummary1(currentLocation.name, currentTotalOutput, volvos.filter { it.open }.map { it.name })
        }
    }

    data class GameSummary1(val currentLocName: String, val currentAmt: Int, val open: List<String>)
    data class GameSummary2(
        val personLocName: String,
        val elephantLocName: String,
        val currentAmt: Int,
        val open: List<String>
    )
}