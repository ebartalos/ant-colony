import kotlin.random.Random

class Ant {

    enum class Pheromone {
        SEARCHING, RETURNING
    }

    var positionX: Int = 40
    var positionY: Int = 40

    var pheromone: Pheromone = Pheromone.SEARCHING

    fun switchPheromones() {
        pheromone = if (pheromone == Pheromone.SEARCHING) {
            Pheromone.RETURNING
        } else {
            Pheromone.SEARCHING
        }
    }

    fun move(
        availableSquares: MutableMap<Array<Int>, Double>,
        pheromoneMapSearching: Array<Array<Int>>,
        pheromoneMapReturning: Array<Array<Int>>
    ) {
        val pheromoneTrail = if (pheromone == Pheromone.SEARCHING) {
            pheromoneMapReturning
        } else {
            pheromoneMapSearching
        }

        var total = 0.0
        for (square in availableSquares.keys) {
            total += pheromoneTrail[square[0]][square[1]]
            availableSquares[square] = total
        }

        if (total <= 0.0) {
            val spot = availableSquares.keys.random()
            positionX = spot[0]
            positionY = spot[1]
        } else {
            val selector = Random.nextDouble(total)
            val result = availableSquares.reversed().toSortedMap()

            for ((chance, spot) in result) {
                if (selector <= chance) {
                    positionX = spot[0]
                    positionY = spot[1]
                }
            }
        }

    }

    private fun <K, V> Map<K, V>.reversed() = HashMap<V, K>().also { newMap ->
        entries.forEach { newMap[it.value] = it.key }
    }
}