import gui.GUI
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

class Simulation(private val sideLength: Int) {
    private val boardState = Array(sideLength) { Array(sideLength) { emptyMark } }
    private val delay: Long = 1L
    private lateinit var gui: GUI

    private val appleLocations: ArrayList<Pair<Int, Int>> = arrayListOf()

    private val lairLocationX: Int = 47
    private val lairLocationY: Int = 47

    private val emptyMark = 0
    private val wallMark = 1
    private val antSearchingMark = 2
    private val appleMark = 3
    private val lairMark = 4
    private val antReturningMark = 5

    private val ants = arrayListOf<Ant>()
    private val pheromoneMapSearching = Array(sideLength) { Array(sideLength) { 1 } }
    private val pheromoneMapReturning = Array(sideLength) { Array(sideLength) { 1 } }


    init {
        drawApples()
        drawWalls()
        addAnts()
        updateBoard()
        initGUI()
    }

    /**
     * Play the ant colony simulation.
     */
    fun play() {
        while (true) {
            spawnApples()

            ants.forEach { ant ->
                resolveMovement(ant)
                ant.decreasePheromoneProduction()
                updateBoard()
                resolvePheromones(ant)
            }

            increasePheromoneLevelForAllAnts()
            evaporate()

            updateGUI()
        }
    }

    private fun initGUI() {
        gui = GUI(boardState)
        gui.isVisible = true
    }

    private fun updateGUI() {
        gui.update(boardState)
        Thread.sleep(delay)
    }

    private fun increasePheromoneLevelForAllAnts() {
        ants.forEach { ant -> increasePheromoneLevel(ant) }
    }

    private fun resolveMovement(ant: Ant) {
        if (abs(Random.nextInt() % 10) > Constants.FREEDOM_DEGREE) {
            ant.moveRandomly(calculateAvailableSquares(ant))
        } else {
            ant.move(
                calculateAvailableSquares(ant),
                if (ant.isSearching()) pheromoneMapReturning else pheromoneMapSearching
            )
        }
    }

    private fun resolvePheromones(ant: Ant) {
        if (ant.isSearching() && (isAppleEaten(ant))
            || (isAntHome(ant) && ant.isReturning())
        ) {
            ant.fillPheromoneLevel()
            ant.swapPheromone()
        }
    }

    private fun spawnApples() {
        if (appleLocations.isEmpty()) {
            val x = (2..60).random()
            val y = (2..60).random()

            appleLocations.add(Pair(x, y))
            appleLocations.add(Pair(x + 1, y))
            appleLocations.add(Pair(x + 1, y - 1))
            appleLocations.add(Pair(x, y - 1))
            appleLocations.add(Pair(x, y + 1))
        }
    }

    private fun isAppleEaten(ant: Ant): Boolean {
        val neighbors = arrayListOf<Pair<Int, Int>>()
        neighbors.add(Pair(ant.positionX - 1, ant.positionY))
        neighbors.add(Pair(ant.positionX - 1, ant.positionY - 1))
        neighbors.add(Pair(ant.positionX - 1, ant.positionY + 1))
        neighbors.add(Pair(ant.positionX, ant.positionY - 1))
        neighbors.add(Pair(ant.positionX, ant.positionY + 1))
        neighbors.add(Pair(ant.positionX + 1, ant.positionY - 1))
        neighbors.add(Pair(ant.positionX + 1, ant.positionY))
        neighbors.add(Pair(ant.positionX + 1, ant.positionY + 1))

        for (neighbor in neighbors) {
            if (boardState[neighbor.first][neighbor.second] == appleMark) {
                if ((Random.nextInt() % Constants.APPLE_BITES) == 0) {
                    appleLocations.remove(Pair(neighbor.first, neighbor.second))
                }
                return true
            }
        }
        return false
    }

    private fun isAntHome(ant: Ant): Boolean {
        return (ant.positionX == lairLocationX) && (ant.positionY == lairLocationY)
    }

    private fun calculateAvailableSquares(ant: Ant): MutableMap<Array<Int>, Int> {
        val availableSquares = mutableMapOf(
            arrayOf(ant.positionX - 1, ant.positionY) to 1,
            arrayOf(ant.positionX, ant.positionY - 1) to 1,
            arrayOf(ant.positionX, ant.positionY + 1) to 1,
            arrayOf(ant.positionX + 1, ant.positionY) to 1,
        )

        if (Constants.EIGHT_DIMENSIONAL_MOVEMENT) {
            availableSquares[arrayOf(ant.positionX - 1, ant.positionY - 1)] = 1
            availableSquares[arrayOf(ant.positionX - 1, ant.positionY + 1)] = 1
            availableSquares[arrayOf(ant.positionX + 1, ant.positionY - 1)] = 1
            availableSquares[arrayOf(ant.positionX + 1, ant.positionY + 1)] = 1
        }

        // remove walls from the equation
        for ((position, _) in availableSquares) {
            if (boardState[position[0]][position[1]] == wallMark) {
                availableSquares[position] = 0
            }
        }
        return availableSquares.filterValues { it != 0 } as MutableMap<Array<Int>, Int>
    }

    private fun evaporate() {
        pheromoneMapSearching.forEachIndexed { x, yArray ->
            yArray.forEachIndexed { y, pheromone ->
                if (pheromone > Constants.EVAPORATION_RATE) pheromoneMapSearching[x][y] -= Constants.EVAPORATION_RATE
            }
        }
        pheromoneMapReturning.forEachIndexed { x, yArray ->
            yArray.forEachIndexed { y, pheromone ->
                if (pheromone > Constants.EVAPORATION_RATE) pheromoneMapReturning[x][y] -= Constants.EVAPORATION_RATE
            }
        }
    }

    private fun increasePheromoneLevel(ant: Ant) {
        if (ant.pheromone == Ant.Pheromone.SEARCHING) {
            pheromoneMapSearching[ant.positionX][ant.positionY] =
                max(ant.pheromoneLevel, pheromoneMapSearching[ant.positionX][ant.positionY])
        } else {
            pheromoneMapReturning[ant.positionX][ant.positionY] =
                max(ant.pheromoneLevel, pheromoneMapReturning[ant.positionX][ant.positionY])
        }
    }

    private fun drawWalls() {
        for (index in 0 until sideLength) {
            boardState[0][index] = wallMark
            boardState[sideLength - 1][index] = wallMark
            boardState[index][0] = wallMark
            boardState[index][sideLength - 1] = wallMark
        }
    }

    private fun drawApples() {
        appleLocations.forEach {
            boardState[it.first][it.second] = appleMark
        }
    }

    /**
     * Update position of ants and apple on the board.
     */
    private fun updateBoard() {
        drawWalls()

        for (x in 1 until sideLength - 1) {
            for (y in 1 until sideLength - 1) {
                if (boardState[x][y] != wallMark) {
                    boardState[x][y] = 0
                }
            }
        }

        drawApples()

        for (ant in ants) {
            if (ant.pheromone == Ant.Pheromone.SEARCHING) {
                boardState[ant.positionX][ant.positionY] = antSearchingMark
            } else {
                boardState[ant.positionX][ant.positionY] = antReturningMark
            }
        }
        boardState[lairLocationX][lairLocationY] = lairMark
    }

    private fun addAnts() {
        for (i in 1..Constants.MAX_ANTS) {
            ants.add(Ant(Pair(lairLocationX, lairLocationY)))
        }
    }
}