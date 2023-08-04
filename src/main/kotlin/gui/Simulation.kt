package gui

import Ant
import kotlin.random.Random

class Simulation(private val sideLength: Int) {
    private val boardState = Array(sideLength) { Array(sideLength) { 0 } }
    private val delay: Long = 50L

    private var appleLocationX: Int = Random.nextInt(1, sideLength - 1)
    private var appleLocationY: Int = Random.nextInt(1, sideLength - 1)

    private val emptyMark = 0
    private val wallMark = 1
    private val antMark = 2
    private val appleMark = 3

    private val ants = arrayListOf<Ant>()

    init {
        drawWalls()

        ants.add(Ant())
        ants.add(Ant())
        ants.add(Ant())

        updateBoard()
    }

    fun play() {
        val gui = GUI(boardState)
        gui.isVisible = true

        while (true) {
            ants.forEach {
                it.moveRandomly()
                updateBoard()
                gui.update(boardState)
                Thread.sleep(delay)
            }
        }




//        gui.quit()

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
     * Set random position for apple - O - that is inside of board.
     */
    private fun setRandomApplePosition() {
        boardState[appleLocationX][appleLocationY] = emptyMark

        appleLocationX = Random.nextInt(1, sideLength - 1)
        appleLocationY = Random.nextInt(1, sideLength - 1)

        updateBoard()
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