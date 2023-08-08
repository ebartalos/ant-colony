import kotlin.random.Random

class Ant(spawnPoint: Pair<Int, Int>) {

    enum class Pheromone {
        SEARCHING, RETURNING
    }

    var positionX: Int = spawnPoint.first
    var positionY: Int = spawnPoint.second

    var pheromone: Pheromone = Pheromone.SEARCHING
    var pheromoneLevel: Int = 0

    init {
        fillPheromoneLevel()
    }

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
        val pheromoneTrail = if (isSearching()) {
            pheromoneMapReturning
        } else {
            pheromoneMapSearching
        }

        var total = 0.0
        for (square in availableSquares.keys) {
            total += pheromoneTrail[square[0]][square[1]]
            availableSquares[square] = total
        }

        val selector = Random.nextDouble(total)
        val result = availableSquares.reversed().toSortedMap()

        for ((chance, spot) in result) {
            if (selector <= chance) {
                positionX = spot[0]
                positionY = spot[1]
                break
            }
        }
    }

    fun moveByMax(
        availableSquares: MutableMap<Array<Int>, Double>,
        pheromoneMapSearching: Array<Array<Int>>,
        pheromoneMapReturning: Array<Array<Int>>
    ) {
        val pheromoneTrail = if (isSearching()) {
            pheromoneMapReturning
        } else {
            pheromoneMapSearching
        }


        for (square in availableSquares.keys) {
            availableSquares[square] = pheromoneTrail[square[0]][square[1]].toDouble()
        }

        val spot = if (availableSquares.values.all { it == 1.0 }) {
            availableSquares.keys.random()
        } else {
            availableSquares.maxBy { it.value }.key
        }

        positionX = spot[0]
        positionY = spot[1]
    }

    fun decreasePheromoneProduction() {
        if (pheromoneLevel >= Constants.ANT_PHEROMONE_WEAKENING_RATE) {
            pheromoneLevel -= Constants.ANT_PHEROMONE_WEAKENING_RATE
        } else {
            pheromoneLevel = 1
        }
    }

    fun fillPheromoneLevel() {
        pheromoneLevel = Constants.MAX_PHEROMONE_LEVEL
    }

    private fun isSearching(): Boolean {
        return pheromone == Pheromone.SEARCHING
    }

    private fun <K, V> Map<K, V>.reversed() = HashMap<V, K>().also { newMap ->
        entries.forEach { newMap[it.value] = it.key }
    }
}