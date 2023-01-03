import kotlin.math.max

class Day19 {
    val part1TestExpected = 33
    val part2TestExpected = 56 * 62

    fun part1(input: List<String>): Int {
        val blueprintStates = parseInput(input)
        return blueprintStates.mapIndexed { index, gameState ->
            val noth = dfs2(gameState.costs, 24)
            println("FINAL SCORE: $noth")
            noth * (index + 1)
        }.sum()
    }

    fun part2(input: List<String>): Int {
        val blueprintStates = parseInput(input.take(3))
        return blueprintStates.mapIndexed { index, gameState ->
            val noth = dfs2(gameState.costs, 32)
            println("FINAL SCORE: $noth")
            noth
        }.foldRight(1) { i, acc -> i * acc }
    }

    private fun dfs2(costs: Map<Material, RobotDef>, timeLimit: Int): Int {
        var maxRes = 0
        fun build(gameState: GameState) {

            if (gameState.materials.access(Material.ORE) >= costs.book(Material.GEODE, Material.ORE) &&
                gameState.materials.access(Material.OBSIDIAN) >= costs.book(Material.GEODE, Material.OBSIDIAN)
            ) {
                val g = gameState.deepCopy()
                g.materials[Material.ORE] =
                    g.materials.access(Material.ORE) - costs.book(Material.GEODE, Material.ORE)
                g.materials[Material.OBSIDIAN] =
                    g.materials.access(Material.OBSIDIAN) - costs.book(Material.GEODE, Material.OBSIDIAN)
                extractResources(g)
                g.day++
                g.robots[Material.GEODE] = g.robots.access(Material.GEODE) + 1
                if (g.day >= timeLimit) {
                    maxRes = max(maxRes, g.final)
                    if (g.final == maxRes && maxRes > 0) println("${g.final} GEM: ${g.buildOrder} ${g.materials}")
                    return
                }
                g.buildOrder += "GEO${g.day} "
                build(g)
            } else {
                if (gameState.robots.access(Material.OBSIDIAN) == 0) run buildOre@{
                    val g = gameState.deepCopy()
                    while (g.materials.access(Material.ORE) < costs.book(Material.ORE, Material.ORE)) {
                        extractResources(g)
                        g.day++
                        if (g.day >= timeLimit) {
                            maxRes = max(maxRes, g.final)
                            if (g.final == maxRes && maxRes > 0) println("${g.final} GEM: ${g.buildOrder} ${g.materials}")
                            return
                        }
                    }
                    g.materials[Material.ORE] =
                        g.materials.access(Material.ORE) - costs.book(Material.ORE, Material.ORE)
                    extractResources(g)
                    g.day++
                    g.robots[Material.ORE] = g.robots.access(Material.ORE) + 1
                    if (g.day >= timeLimit) {
                        maxRes = max(maxRes, g.final)
                        if (g.final == maxRes && maxRes > 0) println("${g.final} GEM: ${g.buildOrder} ${g.materials}")
                        return
                    }
                    g.buildOrder += "ORE${g.day} "
                    build(g)
                }
                if (gameState.robots.access(Material.GEODE) == 0) run buildClay@{
                    val g = gameState.deepCopy()
                    while (g.materials.access(Material.ORE) < costs.book(Material.CLAY, Material.ORE)) {
                        extractResources(g)
                        g.day++
                        if (g.day >= timeLimit) {
                            maxRes = max(maxRes, g.final)
                            if (g.final == maxRes && maxRes > 0) println("${g.final} GEM: ${g.buildOrder} ${g.materials}")
                            return
                        }
                    }
                    g.materials[Material.ORE] =
                        g.materials.access(Material.ORE) - costs.book(Material.CLAY, Material.ORE)
                    extractResources(g)
                    g.day++
                    g.robots[Material.CLAY] = g.robots.access(Material.CLAY) + 1
                    if (g.day >= timeLimit) {
                        maxRes = max(maxRes, g.final)
                        if (g.final == maxRes && maxRes > 0) println("${g.final} GEM: ${g.buildOrder} ${g.materials}")
                        return
                    }
                    g.buildOrder += "CLA${g.day} "
                    build(g)
                }
                if (gameState.robots.access(Material.CLAY) > 0) run buildObs@{
                    val g = gameState.deepCopy()
                    while (g.materials.access(Material.ORE) < costs.book(Material.OBSIDIAN, Material.ORE) ||
                        g.materials.access(Material.CLAY) < costs.book(Material.OBSIDIAN, Material.CLAY)
                    ) {
                        extractResources(g)
                        g.day++
                        if (g.day >= timeLimit) {
                            maxRes = max(maxRes, g.final)
                            if (g.final == maxRes && maxRes > 0) println("${g.final} GEM: ${g.buildOrder} ${g.materials}")
                            return
                        }
                    }
                    g.materials[Material.ORE] =
                        g.materials.access(Material.ORE) - costs.book(Material.OBSIDIAN, Material.ORE)
                    g.materials[Material.CLAY] =
                        g.materials.access(Material.CLAY) - costs.book(Material.OBSIDIAN, Material.CLAY)
                    extractResources(g)
                    g.day++
                    g.robots[Material.OBSIDIAN] = g.robots.access(Material.OBSIDIAN) + 1
                    if (g.day >= timeLimit) {
                        maxRes = max(maxRes, g.final)
                        if (g.final == maxRes && maxRes > 0) println("${g.final} GEM: ${g.buildOrder} ${g.materials}")
                        return
                    }
                    g.buildOrder += "OBS${g.day} "
                    build(g)
                }
                if (gameState.robots.access(Material.OBSIDIAN) > 0) run buildGeo@{
                    val g = gameState.deepCopy()
                    while (g.materials.access(Material.ORE) < costs.book(Material.GEODE, Material.ORE) ||
                        g.materials.access(Material.OBSIDIAN) < costs.book(Material.GEODE, Material.OBSIDIAN)
                    ) {
                        extractResources(g)
                        g.day++
                        if (g.day >= timeLimit) {
                            maxRes = max(maxRes, g.final)
                            if (g.final == maxRes && maxRes > 0) println("${g.final} GEM: ${g.buildOrder} ${g.materials}")
                            return
                        }
                    }
                    g.materials[Material.ORE] =
                        g.materials.access(Material.ORE) - costs.book(Material.GEODE, Material.ORE)
                    g.materials[Material.OBSIDIAN] =
                        g.materials.access(Material.OBSIDIAN) - costs.book(Material.GEODE, Material.OBSIDIAN)
                    extractResources(g)
                    g.day++
                    g.robots[Material.GEODE] = g.robots.access(Material.GEODE) + 1
                    if (g.day >= timeLimit) {
                        maxRes = max(maxRes, g.final)
                        if (g.final == maxRes && maxRes > 0) println("${g.final} GEM: ${g.buildOrder} ${g.materials}")
                        return
                    }
                    g.buildOrder += "GEO${g.day} "
                    build(g)
                }
            }
        }
        build(GameState(mutableMapOf(Material.ORE to 1), mutableMapOf(), costs))
        return maxRes
    }


    fun Map<Material, RobotDef>.book(def: Material, mat: Material) = this[def]!!.cost.access(mat)

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
        var buildOrder: String = "",
        var day: Int = 0
    ) {
        fun deepCopy() = this.copy(robots.toMutableMap(), materials.toMutableMap(), costs.mapValues { it.value.copy() })
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as GameState

            if (buildOrder != other.buildOrder) return false
            if (day != other.day) return false

            return true
        }

        override fun hashCode(): Int {
            var result = buildOrder.hashCode()
            result = 31 * result + day
            return result
        }


        val final: Int get() = materials.getOrDefault(Material.GEODE, 0)


    }

    private fun Map<Material, Int>.access(material: Material) = this.getOrDefault(material, 0)

    enum class Material { ORE, CLAY, OBSIDIAN, GEODE }
    data class RobotDef(val cost: Map<Material, Int>)

}