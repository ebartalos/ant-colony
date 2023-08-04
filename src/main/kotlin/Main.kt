import gui.GUI
import gui.Simulation

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val sim = Simulation(15)
        sim.play()
    }
}