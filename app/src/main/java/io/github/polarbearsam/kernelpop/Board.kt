package io.github.polarbearsam.kernelpop

import kotlin.random.Random

class Board(xSize: Int, ySize: Int, kernelNum: Int) {
    var board = Array<Array<Tile>>(xSize) {Array<Tile>(ySize) {Tile()} }

    init {
        for (i in 1..kernelNum) {
            var notUpdated = true
            while (notUpdated) {
                val xPos = Random.nextInt(0, xSize)
                val yPos = Random.nextInt(0, ySize)
                if (board[xPos][yPos].num != 9) {
                    board[xPos][yPos].num = 9
                    notUpdated = false
                }
            }
        }
    }
}