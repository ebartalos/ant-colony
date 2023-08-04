class Ant {

    enum class Direction {
        LEFT, RIGHT, UP, DOWN
    }

    enum class Pheromone {
        SEARCHING, RETURNING
    }

     var positionX: Int = 4
     var positionY: Int = 4

    private lateinit var pheromone: Pheromone

    fun moveRandomly(){
        move(Direction.values().random())
    }

    private fun move(direction: Direction) {
        when (direction) {
            Direction.LEFT -> positionX -= 1
            Direction.RIGHT -> positionX += 1
            Direction.DOWN -> positionY += 1
            Direction.UP -> positionY -= 1
        }
    }
}