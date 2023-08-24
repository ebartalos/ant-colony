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

    /**
     * Swap pheromones between each other.
     */
    fun swapPheromone() {
        pheromone = if (pheromone == Pheromone.SEARCHING) {
            Pheromone.RETURNING
        } else {
            Pheromone.SEARCHING
        }
    }

    /**
     * Move to available spot with the highest attractive pheromone level.
     *
     * @param availableSquares map of available spots
     * @param pheromoneMap map of attractive pheromone levels
     */
    fun move(
        availableSquares: MutableMap<Array<Int>, Int>,
        pheromoneMap: Array<Array<Int>>
    ) {
        for (square in availableSquares.keys) {
            availableSquares[square] = pheromoneMap[square[0]][square[1]]
        }

        val spot = if (availableSquares.values.all { it == 1 }) {
            availableSquares.keys.random()
        } else {
            availableSquares.maxBy { it.value }.key
        }

        positionX = spot[0]
        positionY = spot[1]
    }

    /**
     * Move to a random available spot.
     *
     * @param availableSquares map of available spots
     */
    fun moveRandomly(availableSquares: MutableMap<Array<Int>, Int>) {
        val spot = availableSquares.keys.random()

        positionX = spot[0]
        positionY = spot[1]
    }

    /**
     * Weaken pheromone level by Constants.ANT_PHEROMONE_WEAKENING_RATE
     */
    fun decreasePheromoneProduction() {
        if (pheromoneLevel >= Constants.ANT_PHEROMONE_WEAKENING_RATE) {
            pheromoneLevel -= Constants.ANT_PHEROMONE_WEAKENING_RATE
        } else {
            pheromoneLevel = 1
        }
    }

    /**
     * Fill pheromone level to the Constants.MAX_PHEROMONE_LEVEL
     */
    fun fillPheromoneLevel() {
        pheromoneLevel = Constants.MAX_PHEROMONE_LEVEL
    }

    /**
     * Return true if ant is searching for food
     */
    fun isSearching(): Boolean {
        return pheromone == Pheromone.SEARCHING
    }

    /**
     * Return true if ant is searching for home
     */
    fun isReturning(): Boolean {
        return pheromone == Pheromone.RETURNING
    }
}