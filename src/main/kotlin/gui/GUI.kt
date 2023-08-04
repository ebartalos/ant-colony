package gui

import javax.swing.JFrame

class GUI(boardState: Array<Array<Int>>) : JFrame() {

    private var board: Board

    init {
        board = Board(boardState)
        add(board)

        title = "Eater"
        isResizable = false

        pack()

        setLocationRelativeTo(null)
        defaultCloseOperation = EXIT_ON_CLOSE
    }

    fun update(boardState: Array<Array<Int>>) {
        board.update(boardState)
    }

    fun quit() {
        this.dispose()
    }
}