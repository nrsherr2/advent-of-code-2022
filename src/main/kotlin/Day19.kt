class Day19 {
    val part1TestExpected = 33
    val part2TestExpected = -1

    fun part1(input: List<String>): Int {
        val blueprintStates = parseInput(input)
        return blueprintStates.mapIndexed { index, gameState ->
            val noth = dfs(gameState)
            println("FINAL SCORE: $noth")
            noth * (index + 1)
        }.sum()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    private fun dfs(gs: GameState, day: Int = 1, layer: Int = 0): Int {
        if (day >= 24) {
            return gs.final
        }
        if (day == 23 || layer >= 30) return waitUntilEnd(gs.deepCopy(), day, layer + 1)
        val justWait = if (gs.robots[Material.GEODE] != null) waitUntilEnd(gs.deepCopy(), day, layer + 1) else null
        val newOreBot = buildOre(gs.deepCopy(), day, layer + 1)
        val newClayBot = buildClay(gs.deepCopy(), day, layer + 1)
        val newObsBot =
            if (gs.robots.getOrDefault(Material.CLAY, 0) != 0) buildObs(gs.deepCopy(), day, layer + 1) else null
        val newGeoBot =
            if (gs.robots.getOrDefault(Material.OBSIDIAN, 0) != 0) buildGeo(gs.deepCopy(), day, layer + 1) else null
        return listOfNotNull(justWait, newOreBot, newClayBot, newObsBot, newGeoBot).max()
    }

    private fun buildOre(gs: GameState, day: Int = 1, layer: Int): Int {
        if (day >= 24) return gs.final
//        println("Layer $layer Day $day Ore")
        var curDay = day
        do {
            if (curDay >= 24) return gs.final
            curDay++
            extractResources(gs)
        } while (gs.materials.getOrDefault(Material.ORE, 0) < gs.costs[Material.ORE]!!.cost[Material.ORE]!!)
        gs.materials[Material.ORE] = gs.materials[Material.ORE]!! - gs.costs[Material.ORE]!!.cost[Material.ORE]!!
        gs.robots[Material.ORE] = gs.robots[Material.ORE]!! + 1
        return dfs(gs.deepCopy(), curDay, layer)
    }

    private fun buildClay(gs: GameState, day: Int = 1, layer: Int): Int {
        if (day >= 24) return gs.final
//        println("Layer $layer Day $day Clay")
        var curDay = day
        do {
            if (curDay >= 24) return gs.final
            curDay++
            extractResources(gs)
        } while (gs.materials.getOrDefault(Material.ORE, 0) < gs.costs[Material.CLAY]!!.cost[Material.ORE]!!)
        gs.materials[Material.ORE] = gs.materials[Material.ORE]!! - gs.costs[Material.ORE]!!.cost[Material.ORE]!!
        gs.robots[Material.CLAY] = gs.robots.getOrDefault(Material.CLAY, 0) + 1
        return dfs(gs.deepCopy(), curDay, layer)
    }

    private fun buildObs(gs: GameState, day: Int = 1, layer: Int): Int {
        if (day >= 24) return gs.final
//       println("Layer $layer Day $day Obsidian")
        var curDay = day
        val cost = gs.costs[Material.OBSIDIAN]!!
        do {
            if (curDay >= 24) return gs.final
            curDay++
            extractResources(gs)
        } while (
            gs.materials.getOrDefault(Material.ORE, 0) < cost.cost[Material.ORE]!! &&
            gs.materials.getOrDefault(Material.CLAY, 0) < cost.cost[Material.CLAY]!!
        )
        gs.materials[Material.ORE] = gs.materials[Material.ORE]!! - cost.cost[Material.ORE]!!
        gs.materials[Material.CLAY] = gs.materials[Material.CLAY]!! - cost.cost[Material.CLAY]!!
        gs.robots[Material.OBSIDIAN] = gs.robots.getOrDefault(Material.OBSIDIAN, 0) + 1
        return dfs(gs.deepCopy(), curDay, layer)
    }

    private fun buildGeo(gs: GameState, day: Int = 1, layer: Int): Int {
        if (day >= 24) return gs.final
//        println("Layer $layer Day $day Geode")
        var curDay = day
        val cost = gs.costs[Material.GEODE]!!
        do {
            if (curDay >= 24) return gs.final
            curDay++
            extractResources(gs)
        } while (
            gs.materials.getOrDefault(Material.OBSIDIAN, 0) < cost.cost[Material.OBSIDIAN]!! &&
            gs.materials.getOrDefault(Material.CLAY, 0) < cost.cost[Material.OBSIDIAN]!!
        )
        gs.materials[Material.ORE] = gs.materials[Material.ORE]!! - cost.cost[Material.ORE]!!
        gs.materials[Material.OBSIDIAN] = gs.materials[Material.OBSIDIAN]!! - cost.cost[Material.OBSIDIAN]!!
        gs.robots[Material.GEODE] = gs.robots.getOrDefault(Material.GEODE, 0) + 1
        return dfs(gs.deepCopy(), curDay, layer)
    }

    private fun waitUntilEnd(gs: GameState, day: Int, layer: Int): Int {
        if (day >= 24) return gs.final
        if (day < 17) println("Layer $layer Day $day wait")
        for (i in day..24) {
            for (mat in Material.values()) {
                gs.materials[mat] = gs.materials.getOrDefault(mat, 0) + gs.robots.getOrDefault(mat, 0)
            }
        }
        return gs.final
    }

    private fun extractResources(gameState: GameState) {
        Material.values().forEach { mat ->
            val robotCount = gameState.robots.getOrDefault(mat, 0)
            if (robotCount > 0) {
                val old = gameState.materials.getOrDefault(mat, 0)
                gameState.materials[mat] = old + robotCount
//                println("$robotCount $mat robots: $old -> ${gameState.currentResources[mat]}")
            }
        }
    }

    private fun parseInput(input: List<String>) = input.map { line ->
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

    data class GameState(
        val robots: MutableMap<Material, Int>,
        val materials: MutableMap<Material, Int>,
        val costs: Map<Material, RobotDef>
    ) {
        fun deepCopy() = this.copy(robots.toMutableMap(), materials.toMutableMap(), costs.mapValues { it.value.copy() })

        val final: Int get() = materials.getOrDefault(Material.GEODE, 0)
    }

    enum class Material { ORE, CLAY, OBSIDIAN, GEODE }
    data class RobotDef(val cost: Map<Material, Int>)

}