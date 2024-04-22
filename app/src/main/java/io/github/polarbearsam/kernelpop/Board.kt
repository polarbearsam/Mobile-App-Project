package io.github.polarbearsam.kernelpop

import android.util.Log
import kotlin.random.Random

/**
 * Creates and manages the game board.
 * @param xSize the size of the board in the x plane
 * @param ySize the size of the board in the y plane
 * @param kernelNum number of kernels to place on the board
 * @constructor Initializes the board with the given size and number of kernels
  */
class Board(val xSize: Int, val ySize: Int, kernelNum: Int) {
    private var board = Array(xSize) {Array(ySize) {Tile()} } // Creates a 2D array of Tiles
    var currentKernels = kernelNum
        set(value) {
            field = if (value <= xSize * ySize - 1) {
                value
            } else {
                xSize * ySize - 1
            }
        }

    /**
     * Initializes the board
     */
    init {
        populateBoard(currentKernels)
    }

    /**
     * Gets the number of a given tile
     * @param x x position of the tile
     * @param y y position of the tile
     * @return returns the number value of the tile or -1 if the position is out of bounds
     */
    fun getTileNum(x: Int, y : Int): Int {
        return if (x < xSize && y < ySize) {
            board[x][y].num
        } else {
            -1
        }
    }

    /**
     * Gets a tile on the board
     * @param x x position of the tile
     * @param y y position of the tile
     * @return returns the tile or null if the position is out of bounds
     */
    fun getTile(x: Int, y: Int): Tile? {
        return if (x < xSize && y < ySize) {
            board[x][y]
        } else {
            null
        }
    }

    /**
     * Prints out the values of all tiles in the board for debugging purposes
     */
    fun debugPrintBoard() {
        for (y in 0..<ySize) {
            var output = ""
            for (x in 0..<xSize) {
                output += board[x][y].num.toString() + ", "
            }
            Log.d("BOARD", output)
        }
    }

    /**
     * Places a given number of kernels on the board.
     * @param kernelNum number of kernels to add to the board.
      */
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