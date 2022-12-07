class Day07 {
    val part1TestExpected = 95437
    val part2TestExpected = 24933642
    fun part1(input: List<String>): Int {
        val currentTree = parseInput(input)
        return recursiveSumSubFolders(currentTree.head)
    }

    fun part2(input: List<String>): Int {
        val currentTree = parseInput(input)
        val desiredSize = 70000000 - 30000000
        val initialSize = currentTree.head.size
        val canDelete = buildList {
            var layer = listOf(currentTree.head)
            while (layer.isNotEmpty()) {
                addAll(layer.filter { initialSize - it.size <= desiredSize })
                layer = layer.flatMap { it.children.filterIsInstance<FileTreeFolder>() }
            }
        }
        return canDelete.minOf { it.size }
    }

    private fun recursiveSumSubFolders(folder: FileTreeFolder): Int {
        val directoryChildren = folder.children.filterIsInstance<FileTreeFolder>()
        val under = directoryChildren.filter { it.size < 100000 }
        return under.sumOf { it.size } + directoryChildren.sumOf { recursiveSumSubFolders(it) }
    }

    private fun parseInput(input: List<String>): FileTree {
        val tree = FileTree()
        var currentNode: FileTreeFolder = tree.head

        input.forEach { line ->
            val spl = line.split(" ")
            when (spl.first()) {
                "dir" -> {
                    val nnn = FileTreeFolder(spl.last(), currentNode)
                    currentNode.children.add(nnn)
                }

                "$" -> {
                    when (line) {
                        "$ ls" -> {}
                        "$ cd /" -> currentNode = tree.head
                        "$ cd .." -> currentNode = currentNode.parent!!
                        else -> {
                            val name = spl.last()
                            currentNode = currentNode.children.first { it.name == name } as FileTreeFolder
                        }
                    }
                }

                else -> currentNode.children.add(FileTreeFile(spl.last(), spl.first().toInt()))
            }
        }
        return tree

    }


    enum class TerminalState { INPUT, OUTPUT }

    data class FileTree(var head: FileTreeFolder = FileTreeFolder("/"))

    sealed interface Resource {
        val name: String
        val size: Int
    }

    data class FileTreeFolder(
        override val name: String,
        val parent: FileTreeFolder? = null,
        val children: MutableList<Resource> = mutableListOf()
    ) : Resource {
        override val size: Int
            get() = children.sumOf { it.size }

        override fun toString(): String {
            return "FileTreeFolder(name=$name, parent=${parent?.name}, children = $children)"
        }
    }

    data class FileTreeFile(override val name: String, override val size: Int) : Resource
}