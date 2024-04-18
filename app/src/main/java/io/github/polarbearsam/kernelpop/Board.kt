package io.github.polarbearsam.kernelpop

import android.util.Log
import kotlin.random.Random

// Creates and manages the game board.
class Board(xSize: Int, ySize: Int, kernelNum: Int) {
    private var board = Array(xSize) {Array(ySize) {Tile()} } // Creates a 2D array of Tiles
    val xSize : Int
    val ySize : Int

    init {
        this.xSize = xSize
        this.ySize = ySize

        populateBoard(kernelNum)
    }

    fun getTileNum(x: Int, y : Int): Int {
        return if (x < xSize && y < ySize) {
            board[x][y].num
        } else {
            -1
        }
    }

    fun getTile(x: Int, y: Int): Tile? {
        return if (x < xSize && y < ySize) {
            board[x][y]
        } else {
            null
        }
    }

    fun isKernel(tile: Tile): Boolean {
        return tile.num == 9
    }

    fun isKernel(num: Int): Boolean {
        return num == 9
    }

    fun debugPrintBoard() {
        for (y in 0..<ySize) {
            var output = ""
            for (x in 0..<xSize) {
                output += board[x][y].num.toString() + ", "
            }
            Log.d("BOARD", output)
        }
    }

    // Places a given number of kernels on the board.
    private fun populateBoard(kernelNum: Int) {
        for (i in 1..kernelNum) {
            var updated = false
            while (!updated) {
                val xPos = Random.nextInt(0, xSize)
                val yPos = Random.nextInt(0, ySize)
                if (board[xPos][yPos].num != 9) {
                    board[xPos][yPos].num = 9

                    for (x in xPos-1..xPos+1) {
                        for (y in yPos-1..yPos+1) {
                            if (x in 0..<xSize && y in 0..<ySize) {
                                board[x][y].num++
                            }
                        }
                    }

                    updated = true
                }
            }
        }
    }
}