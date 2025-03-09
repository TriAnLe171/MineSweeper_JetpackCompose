package com.example.minesweeper.ui.MS

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlin.random.Random

class MineSweeperViewModel: ViewModel() {
    private var n by mutableIntStateOf(9)
    var board by mutableStateOf(Array(n) { Array(n) { null as Mode?} })
    var flags by mutableIntStateOf(0)
    private var gameOver by mutableStateOf(false)
    var currentMode by mutableStateOf(Mode.CHECK)
    var selectedDifficulty by mutableStateOf(value = "Easy")
    private var adjacentMines by mutableIntStateOf(0)
    private var mineLocations = mutableSetOf<Pair<Int, Int>>()
    var backgroundColor by mutableStateOf(Color.Cyan)

    init {
        reset()
    }

    fun onCellClicked(row: Int, col: Int) {
        if (!gameOver) {
            val newBoard = board.copyOf()
            if (currentMode == Mode.FLAG) {
                if (newBoard[row][col] == Mode.FLAG) {
                    newBoard[row][col] = null
                    flags--
                } else if (flags < mineCount()) {
                    newBoard[row][col] = Mode.FLAG
                    flags++
                }
            } else {
                if (newBoard[row][col] != Mode.FLAG) {
                    newBoard[row][col] = Mode.CHECK
                    adjacentMines = countAdjacentMines(row, col)
                }
            }
            board = newBoard
            backgroundColor = Color.White
        }
    }

    fun countAdjacentMines(row: Int, col: Int): Int {
        var mineCount = 0
        for (r in row - 1..row + 1) {
            for (c in col - 1..col + 1) {
                if (isMine(r, c)) {
                    mineCount++
                }
            }
        }
        return mineCount
    }

    fun mineCount(): Int {
        return when (selectedDifficulty) {
            "Easy" -> (n * n * 0.1).toInt()
            "Medium" -> (n * n * 0.2).toInt()
            else -> (n * n * 0.3).toInt()
        }
    }

    private fun generateMines(n: Int) {
        mineLocations.clear()

        var placedMines = 0
        while (placedMines < mineCount()) {
            val x = Random.nextInt(n)
            val y = Random.nextInt(n)
            if (Pair(x, y) !in mineLocations) {
                mineLocations.add(Pair(x, y))
                placedMines++
            }
        }
        println(mineLocations)
    }

    fun isMine(row: Int, col: Int): Boolean {
        return Pair(row, col) in mineLocations
    }

    fun isGameOver(): Boolean {
        for (row in board.indices) {
            for (col in board[row].indices) {
                val cell = board[row][col]
                if (cell == Mode.CHECK && isMine(row, col)) {
                    gameOver = true
                    backgroundColor = Color.Red
                    return true
                }
            }
        }
        return false
    }

    fun isWin(): Boolean {
        for (row in board.indices) {
            for (col in board[row].indices) {
                val cell = board[row][col]
                if (isMine(row, col) && cell != Mode.FLAG) {
                    return false
                }
            }
        }
        gameOver = true
        backgroundColor = Color.Green
        return true
    }

    fun reset() {
        board = Array(n) { Array(n) { null as Mode? } }
        gameOver = false
        flags = 0
        currentMode = Mode.CHECK
        generateMines(n)
        backgroundColor = Color.Cyan
    }
}

data class Cell(
    val row: Int,
    val col: Int
)

enum class Mode{
    CHECK,FLAG
}
