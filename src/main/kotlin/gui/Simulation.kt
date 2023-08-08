package gui

import Ant
import Constants
import kotlin.math.max
import kotlin.random.Random

class Simulation(private val sideLength: Int) {
    private val boardState = Array(sideLength) { Array(sideLength) { emptyMark } }
    private val delay: Long = 1L

    private var appleLocationX: Int = 20
    private var appleLocationY: Int = 20

    private var lairLocationX: Int = 47
    private var lairLocationY: Int = 47

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
        drawWalls()
        drawApples()

        for (i in 1..Constants.MAX_ANTS) {
            ants.add(Ant(Pair(lairLocationX, lairLocationY)))
        }

        updateBoard()
    }

    fun play() {
        val gui = GUI(boardState)
        gui.isVisible = true

        while (true) {
            ants.forEach { ant ->
                if (Random.nextInt() % 20 == 1) {
//                    ant.move(calculateAvailableSquares(ant), pheromoneMapSearching, pheromoneMapReturning)
                    ant.moveRandomly(calculateAvailableSquares(ant))
                } else {
                    ant.moveByMax(calculateAvailableSquares(ant), pheromoneMapSearching, pheromoneMapReturning)
                }

                ant.decreasePheromoneProduction()

                updateBoard()

                if ((isAppleEaten(ant) && ant.pheromone == Ant.Pheromone.SEARCHING)
                    || (ant.positionX == lairLocationX && ant.positionY == lairLocationY && (ant.pheromone == Ant.Pheromone.RETURNING))
                ) {
                    ant.fillPheromoneLevel()
                    ant.switchPheromones()
                }
            }
            gui.update(boardState)
            Thread.sleep(delay)

            ants.forEach { ant -> increasePheromoneLevel(ant) }

            evaporate()
        }

//        gui.quit()
    }

    private fun isAppleEaten(ant: Ant): Boolean {
        val location = arrayListOf<Pair<Int, Int>>(
            Pair(appleLocationX, appleLocationY),
        )


        for (spot in location) {
            if (ant.positionX == spot.first && ant.positionY == spot.second) {
                return true
            }
        }
        return false
    }

    private fun calculateAvailableSquares(ant: Ant): MutableMap<Array<Int>, Double> {
        val availableSquares = mutableMapOf(
//            arrayOf(ant.positionX - 1, ant.positionY - 1) to 1.0,
            arrayOf(ant.positionX - 1, ant.positionY) to 1.0,
//            arrayOf(ant.positionX - 1, ant.positionY + 1) to 1.0,
            arrayOf(ant.positionX, ant.positionY - 1) to 1.0,
            arrayOf(ant.positionX, ant.positionY + 1) to 1.0,
//            arrayOf(ant.positionX + 1, ant.positionY - 1) to 1.0,
            arrayOf(ant.positionX + 1, ant.positionY) to 1.0,
//            arrayOf(ant.positionX + 1, ant.positionY + 1) to 1.0
        )

        // remove walls from equation
        for ((position, _) in availableSquares) {
            if (boardState[position[0]][position[1]] == wallMark) {
                availableSquares[position] = 0.0
            }
        }
        return availableSquares.filterValues { it != 0.0 } as MutableMap<Array<Int>, Double>
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

        boardState[45][45] = wallMark
        boardState[44][46] = wallMark
        boardState[44][45] = wallMark
        boardState[44][47] = wallMark
        boardState[44][48] = wallMark
        boardState[44][49] = wallMark
        boardState[44][50] = wallMark
        boardState[44][51] = wallMark
        boardState[44][52] = wallMark
        boardState[44][53] = wallMark
        boardState[44][54] = wallMark
        boardState[44][55] = wallMark
        boardState[45][55] = wallMark
        boardState[46][55] = wallMark
        boardState[47][55] = wallMark
        boardState[48][55] = wallMark
        boardState[49][55] = wallMark
        boardState[50][55] = wallMark
        boardState[51][55] = wallMark


        boardState[46][45] = wallMark
        boardState[47][45] = wallMark
        boardState[48][45] = wallMark
        boardState[49][45] = wallMark
        boardState[50][45] = wallMark
        boardState[51][45] = wallMark
        boardState[52][45] = wallMark
        boardState[53][45] = wallMark
        boardState[54][45] = wallMark
        boardState[55][45] = wallMark
        boardState[55][46] = wallMark
        boardState[55][47] = wallMark
        boardState[55][48] = wallMark
        boardState[55][49] = wallMark
        boardState[55][50] = wallMark
        boardState[55][51] = wallMark


    }

    private fun drawApples() {
        boardState[appleLocationX][appleLocationY] = appleMark
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
}