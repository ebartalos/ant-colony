package gui

import Ant

class Simulation(private val sideLength: Int) {
    private val boardState = Array(sideLength) { Array(sideLength) { emptyMark } }

    //        private val delay: Long
//        get() = 50L / ants.size
    private val delay: Long = 1L

    private var appleLocationX: Int = 40
    private var appleLocationY: Int = 20

    private var lairLocationX: Int = 15
    private var lairLocationY: Int = 15

    private val emptyMark = 0
    private val wallMark = 1
    private val antMark = 2
    private val appleMark = 3
    private val lairMark = 4

    private val ants = arrayListOf<Ant>()
    private val pheromoneMapSearching = Array(sideLength) { Array(sideLength) { 1 } }
    private val pheromoneMapReturning = Array(sideLength) { Array(sideLength) { 1 } }


    init {
        drawWalls()

        for (i in 1..100) {
            ants.add(Ant(Pair(lairLocationX, lairLocationY)))
        }

        updateBoard()
    }

    fun play() {
        val gui = GUI(boardState)
        gui.isVisible = true

        while (true) {
            ants.forEach { ant ->
                if (ant.positionX == appleLocationX && ant.positionY == appleLocationY) {
                    ant.fillPheromoneLevel()
                    if (ant.pheromone == Ant.Pheromone.SEARCHING) {
                        ant.switchPheromones()
                    }
                } else if (ant.positionX == lairLocationX && ant.positionY == lairLocationY) {
                    ant.fillPheromoneLevel()
                    if (ant.pheromone == Ant.Pheromone.RETURNING) {
                        ant.switchPheromones()
                    }
                }

                ant.move(calculateAvailableSquares(ant), pheromoneMapSearching, pheromoneMapReturning)
                updateBoard()
            }
            gui.update(boardState)
            Thread.sleep(delay)

            ants.forEach { ant -> increasePheromoneLevel(ant) }

            decreasePheromones()
        }

//        gui.quit()
    }

    private fun calculateAvailableSquares(ant: Ant): MutableMap<Array<Int>, Double> {
        val availableSquares = mutableMapOf(
            arrayOf(ant.positionX - 1, ant.positionY - 1) to 1.0,
            arrayOf(ant.positionX - 1, ant.positionY) to 1.0,
            arrayOf(ant.positionX - 1, ant.positionY + 1) to 1.0,
            arrayOf(ant.positionX, ant.positionY - 1) to 1.0,
            arrayOf(ant.positionX, ant.positionY + 1) to 1.0,
            arrayOf(ant.positionX + 1, ant.positionY - 1) to 1.0,
            arrayOf(ant.positionX + 1, ant.positionY) to 1.0,
            arrayOf(ant.positionX + 1, ant.positionY + 1) to 1.0
        )

        // remove walls from equation
        for ((position, _) in availableSquares) {
            if (boardState[position[0]][position[1]] == wallMark) {
                availableSquares[position] = 0.0
            }
        }
        return availableSquares.filterValues { it != 0.0 } as MutableMap<Array<Int>, Double>
    }

    private fun decreasePheromones() {
        pheromoneMapSearching.forEachIndexed { x, innerArray ->
            innerArray.forEachIndexed { y, pheromone ->
                if (pheromone > 1) pheromoneMapSearching[x][y] -= 1
            }
        }
        pheromoneMapReturning.forEachIndexed { x, innerArray ->
            innerArray.forEachIndexed { y, pheromone ->
                if (pheromone > 1) pheromoneMapReturning[x][y] -= 1
            }
        }
    }

    private fun increasePheromoneLevel(ant: Ant) {
        if (ant.pheromone == Ant.Pheromone.SEARCHING) {
            pheromoneMapSearching[ant.positionX][ant.positionY] += ant.pheromoneLevel
        } else {
            pheromoneMapReturning[ant.positionX][ant.positionY] += ant.pheromoneLevel
        }
    }

    private fun drawWalls() {
        for (index in 0 until sideLength) {
            boardState[0][index] = wallMark
            boardState[sideLength - 1][index] = wallMark
            boardState[index][0] = wallMark
            boardState[index][sideLength - 1] = wallMark
        }
        boardState[sideLength / 2][sideLength / 2] = wallMark
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

        boardState[sideLength / 2][sideLength / 2] = wallMark

        for (ant in ants) {
            boardState[ant.positionX][ant.positionY] = antMark
        }
        boardState[appleLocationX][appleLocationY] = appleMark
        boardState[lairLocationX][lairLocationY] = lairMark
    }
}