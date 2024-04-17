package io.github.polarbearsam.kernelpop

import android.util.Log
import kotlin.random.Random

class Board(xSize: Int, ySize: Int, kernelNum: Int) {
    private var board = Array(xSize) {Array(ySize) {Tile()} }
    private var boardX : Int
    private var boardY : Int

    init {
        boardX = xSize
        boardY = ySize

        for (i in 1..kernelNum) {
            var notUpdated = true
            while (notUpdated) {
                val xPos = Random.nextInt(0, boardX)
                val yPos = Random.nextInt(0, boardY)
                if (board[xPos][yPos].num != 9) {
                    board[xPos][yPos].num = 9

                    for (x in xPos-1..xPos+1) {
                        for (y in yPos-1..yPos+1) {
                            if (x in 0..<boardX && y in 0..<boardY) {
                                if (board[x][y].num != 9) {
                                    board[x][y].num++
                                }
                            }
                        }
                    }

                    notUpdated = false
                }
            }
        }
    }

    fun getTileNum(x: Int, y : Int): Int {
        return if (x < boardX && y < boardY) {
            board[x][y].num
        } else {
            -1
        }
    }

    fun getTile(x: Int, y: Int): Tile? {
        return if (x < boardX && y < boardY) {
            board[x][y]
        } else {
            null
        }
    }

    fun isMine(tile: Tile): Boolean {
        return tile.num >= 9
    }

    fun isMine(num: Int): Boolean {
        return num >= 9
    }

    fun debugPrintBoard() {
        for (y in 0..<boardY) {
            var output = ""
            for (x in 0..<boardX) {
                output += board[x][y].num.toString() + ", "
            }
            Log.d("BOARD", output)
        }
    }
}