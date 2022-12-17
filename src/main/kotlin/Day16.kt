class Day16 {
    val part1TestExpected = 1651
    val part2TestExpected = 1707
    fun part1(input: List<String>): Int {
        val baseValves = parseInput(input)

        var games = listOf(baseValves.map { it.copy() }.let { GameStatePart1(it.first { it.name == "AA" }, it) })
        for (i in 1..30) {
            println("MINUTE $i: ${games.size}")
            val newGames = buildList {
                games.forEach { game ->
                    game.letItFlow()
                    if (game.valves.any { it.flowRate > 0 && !it.open }) {
                        if (game.currentLocation.flowRate > 0 && !game.currentLocation.open) add(game.openCurrent())
                        game.currentLocation.leadsTo.forEach {
                            add(game.moveTo(it))
                        }
                    } else add(game.doNothing())
                }
            }
            val avg = newGames.map { it.currentTotalOutput }.average().let { it - it / 6 }
            var newL = newGames.distinctBy { it.summary() }
            if (i > 10) newL = newL.filter { it.currentTotalOutput >= avg }
            games = newL
        }
        return games.maxOf { it.currentTotalOutput }
    }

    fun part2(input: List<String>): Int {
        val baseValves = parseInput(input)
        var games = listOf(baseValves.map { it.copy() }
            .let { GameStatePart2(it.first { it.name == "AA" }, it.first { it.name == "AA" }, it) })
        for (i in 1..26) {
            val daysLeft = 26 - i
            println("MINUTE $i: ${games.size}")
            val newGames = buildList {
                games.forEach { game ->
                    if (!game.personLocation.open) {
                        if (!game.elephantLocation.open && game.elephantLocation != game.personLocation) add(game.openBoth(daysLeft))
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
            if(newL.count { it.valves.all { it.flowRate == 0 || it.open } } >= 500) {
                return newL.maxOf { it.currentTotalOutput }+1
            }
//            if (i > 10) newL = newL.filter { it.currentTotalOutput >= avg }
            games = newL
        }
        return games.maxOf { it.currentTotalOutput }
    }

    fun parseInput(input: List<String>): List<Valve> {
        val baseValves = input.map { line ->
            Valve(
                name = line.split("Valve ")[1].split(" has")[0], flowRate = line.split(";")[0].split("=")[1].toInt()
            ) to line.split("to ")[1]
        }
        baseValves.forEach { (valve, line) ->
            val splits = line.split(" ").let { it.subList(1, it.size) }.map { it.filter { it in 'A'..'Z' } }
            valve.leadsTo = baseValves.filter { it.first.name in splits }.map { it.first }
        }
        return baseValves.map { it.first }
    }

    data class Valve(
        val name: String, val flowRate: Int, var open: Boolean = false, var leadsTo: List<Valve> = emptyList()
    ) {
        override fun toString(): String {
            return "Valve(name='$name', flowRate=$flowRate, open=$open, leadsTo=${leadsTo.map { it.name }})"
        }
    }

    data class GameStatePart2(
        var personLocation: Valve, var elephantLocation: Valve, var valves: List<Valve>, var currentTotalOutput: Int = 0
    ) {
        fun openPersonMoveElephant(newElephant: Valve, daysLeft: Int): GameStatePart2 {
            val copyList = valves.map { it.copy() }
            val newPer = copyList.first { it.name == personLocation.name }
            val newScore = newPer.flowRate * daysLeft
            newPer.open = true
            val newEle = copyList.first { it.name == newElephant.name }
            return this.copy(
                personLocation = newPer,
                elephantLocation = newEle,
                valves = copyList,
                currentTotalOutput = currentTotalOutput + newScore
            )
        }

        fun movePersonOpenElephant(newPerson: Valve, daysLeft: Int): GameStatePart2 {
            val copyList = valves.map { it.copy() }
            val newPer = copyList.first { it.name == newPerson.name }
            val newEle = copyList.first { it.name == elephantLocation.name }
            val newScore = newEle.flowRate * daysLeft
            newEle.open = true
            return this.copy(
                personLocation = newPer,
                elephantLocation = newEle,
                valves = copyList,
                currentTotalOutput = newScore + currentTotalOutput
            )
        }

        fun openBoth(daysLeft: Int): GameStatePart2 {
            val copyList = valves.map { it.copy() }
            val newPer = copyList.first { it.name == personLocation.name }
            val perScore = newPer.flowRate * daysLeft
            newPer.open = true
            val newEle = copyList.first { it.name == elephantLocation.name }
            val eleScore = newEle.flowRate * daysLeft
            newEle.open = true
            return this.copy(
                personLocation = newPer,
                elephantLocation = newEle,
                valves = copyList,
                currentTotalOutput = currentTotalOutput + perScore + eleScore
            )
        }

        fun moveBoth(newPerson: Valve, newElephant: Valve): GameStatePart2 {
            val copyList = valves.map { it.copy() }
            val newPer = copyList.first { it.name == newPerson.name }
            val newEle = copyList.first { it.name == newElephant.name }
            return this.copy(personLocation = newPer, elephantLocation = newEle, valves = copyList)
        }

        fun summary(): GameSummary2 {
            return GameSummary2(
                personLocation.name,
                elephantLocation.name,
                currentTotalOutput,
                valves.filter { it.open }.map { it.name })
        }
    }

    data class GameStatePart1(
        var currentLocation: Valve, var valves: List<Valve>, var currentTotalOutput: Int = 0
    ) {
        fun openCurrent(): GameStatePart1 {
            val copyList = valves.map { it.copy() }
            val newCur = copyList.first { it.name == currentLocation.name }
            newCur.open = true
            return this.copy(currentLocation = newCur, valves = copyList)
        }

        fun moveTo(newLocation: Valve): GameStatePart1 {
            val copyList = valves.map { it.copy() }
            val newCur = copyList.first { it.name == newLocation.name }
            return this.copy(currentLocation = newCur, valves = copyList)
        }

        fun doNothing(): GameStatePart1 {
            val copyList = valves.map { it.copy() }
            val newCur = copyList.first { it.name == currentLocation.name }
            return this.copy(currentLocation = newCur, valves = copyList)
        }

        fun letItFlow() {
            val s = valves.sumOf { if (it.open) it.flowRate else 0 }
//            println("valves ${valves.filter { it.open }.map { it.name }} are open releasing $s pressure")
            currentTotalOutput += s
        }

        fun summary(): GameSummary1 {
            return GameSummary1(currentLocation.name, currentTotalOutput, valves.filter { it.open }.map { it.name })
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