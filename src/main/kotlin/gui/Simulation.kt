package gui

import Ant

class Simulation(private val sideLength: Int) {
    private val boardState = Array(sideLength) { Array(sideLength) { emptyMark } }
    private val delay: Long
        get() = 50L / ants.size

    private var appleLocationX: Int = 10
    private var appleLocationY: Int = 10
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
            ants.forEach { ant ->
                val neighbors = mutableMapOf(
                    Pair(ant.positionX - 1, ant.positionX - 1) to 1.0,
                    Pair(ant.positionX - 1, ant.positionX) to 1.0,
                    Pair(ant.positionX - 1, ant.positionX + 1) to 1.0,
                    Pair(ant.positionX, ant.positionX - 1) to 1.0,
                    Pair(ant.positionX, ant.positionX + 1) to 1.0,
                    Pair(ant.positionX + 1, ant.positionX - 1) to 1.0,
                    Pair(ant.positionX + 1, ant.positionX) to 1.0,
                    Pair(ant.positionX + 1, ant.positionX + 1) to 1.0
                )

                for ((position, _) in neighbors) {
                    if (boardState[position.first][position.second] == wallMark) {
                        neighbors[position] = 0.0
                    }
                }


                ant.move(neighbors.filterValues { it != 0.0 })
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