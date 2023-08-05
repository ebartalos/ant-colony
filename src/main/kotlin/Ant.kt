class Ant {

    enum class Pheromone {
        SEARCHING, RETURNING
    }

    var positionX: Int = 4
    var positionY: Int = 4

    var pheromone: Pheromone = Pheromone.SEARCHING


    fun move(chance: Map<Pair<Int, Int>, Double>) {
        val spot = chance.keys.random()
        positionX = spot.first
        positionY = spot.second
    }
}