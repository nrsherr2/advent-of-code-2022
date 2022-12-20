class Day19Dep {
    val part1TestExpected = 33
    val part2TestExpected = -1

    fun part1(input: List<String>): Int {
        val blueprintStates = parseInput(input)
        return blueprintStates.mapIndexed { index, gameState ->
            val noth = doNothing(gameState).maxValue
            println("FINAL SCORE: $noth")
            noth * (index + 1)
        }.sum()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    private fun doNothing(gameState: GameState, time: Int = 1): GameState {
//        if (time <= 8) println("=== MINUTE $time : Do Nothing ===")
        extractResources(gameState)
        return dfsLower(time, gameState)
    }

    fun buildBot(gameState: GameState, time: Int, material: Material): GameState {
//        if (time <= 8) println("=== MINUTE $time : Build $material Robot ===")
        val robotCost = gameState.definitions[material]!!
        robotCost.cost.forEach { (mat, cost) ->
            gameState.currentResources[mat] = gameState.currentResources[mat]!! - cost
        }
        extractResources(gameState)
        gameState.robots[material] = gameState.robots.getOrDefault(material, 0) + 1
//        println("You now have ${gameState.robots[material]} $material robots")
        return dfsLower(time, gameState)
    }

    private fun doNothingRest(gameState: GameState, time: Int = 1): GameState {
        val numGeodes = gameState.currentResources.getOrDefault(Material.GEODE, 0)
        val numGeodeBots = gameState.robots.getOrDefault(Material.GEODE, 0)
        val score = numGeodes + (numGeodeBots * (25 - time))
        if (score > gameState.maxValue) {
            println(score)
            gameState.maxValue = score
        }
        return gameState
    }

    private fun dfsLower(time: Int, gameState: GameState): GameState {
        if (time == 24) {
            gameState.applyMax(doNothingRest(gameState, 25))
            return gameState
        } else {
//            if(gameState.maxValue > 0 && time > gameState.firstBotOnline && gameState.robots[Material.GEODE] == null){
//                gameState.applyMax(doNothingRest(gameState,time))
//                return gameState
//            }
//            if (time >= 23 && gameState.robots[Material.GEODE] == null) {
////                println("SLOWPOKE")
//                 gameState.applyMax(doNothingRest(gameState, time))
//                return gameState
//            }
//            if (gameState.maxValue > 0 && gameState.currentResources.getOrDefault(Material.GEODE, 0) > 1) {
//
////                val iLast = gameState.records.lastIndex
////                if (gameState.records[iLast].predict(time) < gameState.maxRecords[iLast].predict(time)) {
//////                    println("OVERRIDE")
////                    return gameState
////                }
//            }
            gameState.applyMax(doNothing(gameState.deepCopy(), time + 1))
            gameState.definitions.forEach {
                if (gameState.currentResources.canBuild(it.value)) gameState.applyMax(
                    buildBot(
                        gameState.deepCopy(), time + 1, it.key
                    )
                )
            }
            return gameState
        }
    }

    private fun extractResources(gameState: GameState) {
        Material.values().forEach { mat ->
            val robotCount = gameState.robots.getOrDefault(mat, 0)
            if (robotCount > 0) {
                val old = gameState.currentResources.getOrDefault(mat, 0)
                gameState.currentResources[mat] = old + robotCount
//                println("$robotCount $mat robots: $old -> ${gameState.currentResources[mat]}")
            }
        }
    }

    private fun parseInput(input: List<String>): List<GameState> = input.map { line ->
        val (oreBase, clayBase, obsBase, geoBase) = line.split(".")
        val mp = buildMap {
            put(
                Material.ORE, RobotDef(mapOf(Material.ORE to oreBase.split("costs")[1].filter { it.isDigit() }.toInt()))
            )
            put(
                Material.CLAY,
                RobotDef(mapOf(Material.ORE to clayBase.split("costs")[1].filter { it.isDigit() }.toInt()))
            )
            obsBase.let { obsStr ->
                val (oreSide, claySide) = obsStr.split("and").map { s -> s.filter { it.isDigit() }.toInt() }
                put(
                    Material.OBSIDIAN, RobotDef(mapOf(Material.ORE to oreSide, Material.CLAY to claySide))
                )
            }
            geoBase.let { geoStr ->
                val (oreSide, obsSide) = geoStr.split("and").map { s -> s.filter { it.isDigit() }.toInt() }
                put(
                    Material.GEODE, RobotDef(mapOf(Material.ORE to oreSide, Material.OBSIDIAN to obsSide))
                )
            }
        }
        GameState(mutableMapOf(Material.ORE to 1), mutableMapOf(), mp)
    }

    enum class Material { ORE, CLAY, OBSIDIAN, GEODE }
    data class RobotDef(val cost: Map<Material, Int>)
    data class GameState(
        val robots: MutableMap<Material, Int>,
        val currentResources: MutableMap<Material, Int>,
        val definitions: Map<Material, RobotDef>,
        var maxValue: Int = 0,
        var firstBotOnline: Int = 0
    ) {
        fun deepCopy() = this.copy(
            robots = robots.toMutableMap(),
            currentResources = currentResources.toMutableMap(),
            definitions = definitions.toMutableMap(),
            maxValue,
            firstBotOnline
        )

        private fun keepTime() = TimeRecord(
            this.robots.getOrDefault(Material.GEODE, 0), this.currentResources.getOrDefault(Material.GEODE, 0)
        )

        fun applyMax(other: GameState) {
            if (other.maxValue > this.maxValue) {
                this.maxValue = other.maxValue
                this.firstBotOnline = other.firstBotOnline
            }
        }
    }

    fun Map<Material, Int>.canBuild(definition: RobotDef): Boolean {
        return Material.values().all {
            this.getOrDefault(it, 0) >= definition.cost.getOrDefault(it, 0)
        }
    }

    data class TimeRecord(var numGeodeBots: Int, var numGeodes: Int) {
        fun predict(dayNum: Int): Int {
            return numGeodes + numGeodeBots * (25 - dayNum)
        }
    }
}