import gui.Simulation

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        Simulation(Constants.WINDOW_SIDE_LENGTH).play()
    }
}