import gui.Simulation

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val sim = Simulation(80)
        sim.play()
    }
}