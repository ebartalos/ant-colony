package gui

import Ant

class Simulation(private val sideLength: Int) {
    private val boardState = Array(sideLength) { Array(sideLength) { emptyMark } }

    //        private val delay: Long
//        get() = 50L / ants.size
    private val delay: Long = 1L

    private var appleLocationX: Int = 60
    private var appleLocationY: Int = 10
    private val emptyMark = 0
    private val wallMark = 1
    private val antMark = 2
    private val appleMark = 3

    private val ants = arrayListOf<Ant>()
    private val pheromoneMapSearching = Array(sideLength) { Array(sideLength) { 0 } }
    private val pheromoneMapReturning = Array(sideLength) { Array(sideLength) { 0 } }


    init {
        drawWalls()

        for (i in 0..500) {
            ants.add(Ant())
        }

        updateBoard()
    }

    fun play() {
        val gui = GUI(boardState)
        gui.isVisible = true

        while (true) {
            ants.forEach { ant ->
                var availableSquares = mutableMapOf(
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
                availableSquares = availableSquares.filterValues { it != 0.0 } as MutableMap<Array<Int>, Double>


                if (ant.positionX == appleLocationX && ant.positionY == appleLocationY && ant.pheromone == Ant.Pheromone.SEARCHING) {
                    ant.switchPheromones()
                } else if (ant.positionX == 30 && ant.positionY == 30 && ant.pheromone == Ant.Pheromone.RETURNING) {
                    ant.switchPheromones()
                }

                ant.move(availableSquares, pheromoneMapSearching, pheromoneMapReturning)


                increasePheromoneLevel(ant)

                updateBoard()
            }
            gui.update(boardState)
            Thread.sleep(delay)


            // decrease pheromones
            pheromoneMapSearching.forEachIndexed { x, innerArray ->
                innerArray.forEachIndexed { y, pheromone ->
                    if (pheromone > 0) pheromoneMapSearching[x][y] -= 1
                }
            }
            pheromoneMapReturning.forEachIndexed { x, innerArray ->
                innerArray.forEachIndexed { y, pheromone ->
                    if (pheromone > 0) pheromoneMapSearching[x][y] -= 1
                }
            }
        }

//        gui.quit()
    }

    private fun increasePheromoneLevel(ant: Ant) {
        if (ant.pheromone == Ant.Pheromone.SEARCHING) {
            pheromoneMapSearching[ant.positionX][ant.positionY] += 100
        } else {
            pheromoneMapReturning[ant.positionX][ant.positionY] += 10
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
    }
}