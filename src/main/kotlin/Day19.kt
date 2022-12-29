class Day19 {
    val part1TestExpected = 33
    val part2TestExpected = -1

    fun part1(input: List<String>): Int {
        val blueprintStates = parseInput(input)
        return blueprintStates.mapIndexed { index, gameState ->
            val noth = runGame(gameState.costs)
            println("FINAL SCORE: $noth")
            noth * (index + 1)
        }.sum()
    }

    fun part2(input: List<String>): Int {
        TODO()
    }

    private fun runGame(costs: Map<Material, RobotDef>): Int {
        var gameStates = listOf(GameState(mutableMapOf(Material.ORE to 1), mutableMapOf(), costs))
        println(1)
        for (i in 1..24) {
            if (gameStates.any { it.final > 0 }) println("GEO! ${gameStates.maxOf { it.final }}")
            val nextStates = gameStates.flatMap { gs ->
                buildList {
                    val cd = gs.deepCopy().apply {
                        buildOrder += "WAI$i "
                        extractResources(this)
                    }
                    add(cd)
                    if (gs.materials.access(Material.ORE) >= gs.costs[Material.GEODE]!!.cost.access(Material.ORE) &&
                        gs.materials.access(Material.OBSIDIAN) >= gs.costs[Material.GEODE]!!.cost.access(Material.OBSIDIAN)
                    ) {
                        val c = gs.deepCopy()
                        c.materials[Material.ORE] =
                            c.materials.access(Material.ORE) - c.costs[Material.GEODE]!!.cost.access(Material.ORE)
                        c.materials[Material.OBSIDIAN] =
                            c.materials.access(Material.OBSIDIAN) - c.costs[Material.GEODE]!!.cost.access(Material.OBSIDIAN)
                        extractResources(c)
                        c.robots[Material.GEODE] = c.robots.access(Material.GEODE) + 1
                        c.buildOrder += "GEO$i "
                        add(c)
                    }
                    if (gs.materials.access(Material.ORE) >= gs.costs[Material.OBSIDIAN]!!.cost.access(Material.ORE) &&
                        gs.materials.access(Material.CLAY) >= gs.costs[Material.OBSIDIAN]!!.cost.access(Material.CLAY)
                    ) {
                        val c = gs.deepCopy()
                        c.materials[Material.ORE] =
                            c.materials.access(Material.ORE) - c.costs[Material.OBSIDIAN]!!.cost.access(Material.ORE)
                        c.materials[Material.CLAY] =
                            c.materials.access(Material.CLAY) - c.costs[Material.OBSIDIAN]!!.cost.access(Material.CLAY)
                        extractResources(c)
                        c.robots[Material.OBSIDIAN] = c.robots.access(Material.OBSIDIAN) + 1
                        c.buildOrder += "OBS$i "
                        add(c)
                    }
                    if (gs.materials.access(Material.ORE) >= gs.costs[Material.CLAY]!!.cost.access(Material.ORE)) {
                        val c = gs.deepCopy()
                        c.materials[Material.ORE] =
                            c.materials.access(Material.ORE) - c.costs[Material.CLAY]!!.cost.access(Material.ORE)
                        extractResources(c)
                        c.robots[Material.CLAY] = c.robots.access(Material.CLAY) + 1
                        c.buildOrder += "CLA$i "
                        add(c)
                    }
                    if (gs.materials.access(Material.ORE) >= gs.costs[Material.ORE]!!.cost.access(Material.ORE)) {
                        val c = gs.deepCopy()
                        c.materials[Material.ORE] =
                            c.materials.access(Material.ORE) - c.costs[Material.ORE]!!.cost.access(Material.ORE)
                        extractResources(c)
                        c.robots[Material.ORE] = c.robots.access(Material.ORE) + 1
                        c.buildOrder += "ORE$i "
                        add(c)
                    }
                }
            }
            val newStates = nextStates.distinct()
            println("${nextStates.size} ${newStates.size}")
            gameStates = (newStates.maxOf { it.final } - 2).let { mx -> newStates.filter { it.final > mx } }
//            if (gameStates.any { it.robots.access(Material.OBSIDIAN) > 0 }) gameStates = gameStates.filter { it.robots.access(Material.OBSIDIAN) > 0 }
//            if (gameStates.any { it.robots.access(Material.CLAY) > 0 }) gameStates = gameStates.filter { it.robots.access(Material.CLAY) > 0 }
            gameStates.maxBy { it.final }.let { println(it.buildOrder) }
//            if(i >= 20)gameStates = gameStates.filter { it.robots.access(Material.OBSIDIAN) == 0 }
        }
        gameStates.maxBy { it.final }.let { println(it.buildOrder) }
        return gameStates.maxOf { it.final }
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
        val costs: Map<Material, RobotDef>,
        var buildOrder: String = ""
    ) {
        fun deepCopy() = this.copy(robots.toMutableMap(), materials.toMutableMap(), costs.mapValues { it.value.copy() })
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GameState

            if (robots != other.robots) return false
            if (materials != other.materials) return false

            return true
        }

        override fun hashCode(): Int {
            var result = robots.hashCode()
            result = 31 * result + materials.hashCode()
            return result
        }

        val final: Int get() = materials.getOrDefault(Material.GEODE, 0)


    }

    private fun Map<Material, Int>.access(material: Material) = this.getOrDefault(material, 0)

    enum class Material { ORE, CLAY, OBSIDIAN, GEODE }
    data class RobotDef(val cost: Map<Material, Int>)

}