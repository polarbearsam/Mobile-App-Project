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
    private var currentKernels = 0
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
        populateBoard(kernelNum)
    }

    /**
     * Gets the current number of kernels on the board
     * @return number of kernels on the board
     */
    fun getKernelNum() : Int {
        return currentKernels
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
     * Gets the number of a given tile, guarantees the tile will never be a mine.
     * @param x x position of the tile
     * @param y y position of the tile
     * @return returns the tile or null if the position is out of bounds
     */
    fun getFirstTile(x: Int, y: Int): Tile? {
        return if (x < xSize && y < ySize) {
            val tile = board[x][y]
            tile.isVisible = true

            if (tile.isKernel()) {
                populateBoard(1)
                updateTiles(x, y, false)
            }

            tile
        } else null
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
        var kernels = kernelNum

        if (currentKernels + kernelNum >= xSize * ySize - 1) {
            kernels = xSize * ySize - 1
        }

        for (i in 1..kernels) {
            var updated = false

            while (!updated) {
                val xPos = Random.nextInt(0, xSize)
                val yPos = Random.nextInt(0, ySize)
                val tile = board[xPos][yPos]

                if (tile.num != 9 && !tile.isVisible) {
                    tile.num = 9
                    updateTiles(xPos, yPos, true)
                    updated = true
                }
            }
        }
    }

    /**
     * Updates the tiles around a tile when removing or adding a mine
     * @param xPos tile x position
     * @param yPos tile y position
     * @param increment when true adds one to surrounding tiles, otherwise subtracts one from tiles
     */
    private fun updateTiles(xPos: Int, yPos: Int, increment: Boolean) {
        for (x in xPos-1..xPos+1) {
            for (y in yPos-1..yPos+1) {
                if (x in 0..<xSize && y in 0..<ySize) {
                    if (increment) {
                        board[x][y].num++
                    } else {
                        board[x][y].num--
                    }
                }
            }
        }
    }
}