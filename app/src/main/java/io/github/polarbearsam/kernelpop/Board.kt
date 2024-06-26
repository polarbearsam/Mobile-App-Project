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
    private var gameState = 0 // -1 if lost, 0 if ongoing, 1 if won
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
        gameState = 0
    }

    /**
     * Reveals all empty tiles around a tile.
     * @param xPos x position of the tile
     * @param yPos y position of the tile
     */
    fun floodFill(xPos: Int, yPos: Int) {
        val tile = board[xPos][yPos]

        if (!tile.isVisible && !tile.isFlagged) {
            if (tile.num == 0 && !tile.isKernel) {
                for (x in xPos-1..xPos+1) {
                    for (y in yPos-1..yPos+1) {
                        if (x in 0..<xSize && y in 0..<ySize) {
                            if (!board[x][y].isVisible && !board[x][y].isFlagged) {
                                tile.isVisible = true
                                floodFill(x, y)
                            }
                        }
                    }
                }
            }
            tile.isVisible = true
        }
    }

    private fun onGameEnd() {
        for (x in 0..<xSize) {
            for (y in 0..<ySize) {
                val thisTile = getTile(x, y) ?: continue
                if (!thisTile.isKernel) continue
                thisTile.isVisible = true
            }
        }
    }

    private fun getAdjacentTiles(xPos: Int, yPos: Int): Array<Tile?> {
        val array = Array<Tile?>(9) {null}

        for (x in xPos-1..xPos+1) {
            for (y in yPos-1..yPos+1) {
                if (x in 0..<xSize && y in 0..<ySize) {
                    array[(x - xPos - 1) * (y - yPos - 1)] = board[x][y]
                }
            }
        }

        return array
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
        return if (x in 0..< xSize && y in 0..< ySize) {
            board[x][y]
        } else {
            null
        }
    }

    /**
     * Returns the current state of the game
     * -1 if lost, 0 if ongoing, 1 if won
     */
    fun getGameState(): Int { return gameState }

    /**
     * Gets the position of a tile on the board
     * @param tile the tile which position is to be determined
     * @return returns a pair of two integers representing the x and y positions respectively
     */
    fun getTilePos(tile: Tile): Pair<Int, Int> {
        for (x in 0..<xSize) {
            for (y in 0..<ySize) {
                if (tile == board[x][y]) {
                    return Pair<Int, Int>(x, y)
                }
            }
        }
        return Pair<Int, Int>(-1, -1)
    }

    /**
     * Reveals a given tile and returns it, guarantees the tile will always be a zero.
     * @param xPos x position of the tile
     * @param yPos y position of the tile
     * @return returns the tile, must be inbounds
     */
    fun revealFirstTile(xPos: Int, yPos: Int): Tile {
        // Checks if the position is valid
        return if (xPos < xSize && yPos < ySize) {
            var kernels = 0

            // For all nearby tiles
            for (x in xPos-1..xPos+1) {
                for (y in yPos - 1..yPos + 1) {
                    // Checks if the position is valid
                    if (x in 0..<xSize && y in 0..<ySize) {
                        val tile = board[x][y]

                        if (!tile.isFlagged) {
                            if (tile.isKernel) {
                                tile.isKernel = false
                                kernels++
                                currentKernels--

                                updateTiles(x, y, false)
                            }
                        }
                    }
                }
            }

            populateBoard(kernels)
            floodFill(xPos, yPos)

            board[xPos][yPos]
        } else {
            throw ArrayIndexOutOfBoundsException()
        }
    }

    /**
     * Checks if a game in the current state is won, lost, or ongoing.
     * @return board.gameState (-1 if lost, 0 if ongoing, 1 if won)
     */
    fun checkGameWon(): Int {
        var numUnclicked = 0
        var lost = false
        Log.d("GAME STATE", "Current Kernels: $currentKernels")
        for (x in 0..<xSize) {
            for (y in 0..<ySize) {
                val tileData = getTile(x, y)
                if (tileData != null) {
                    if (!tileData.isVisible) numUnclicked++
                    else if (tileData.isKernel) {
                        gameState = -1
                        lost = true
                        break
                    }
                }
            }
            if (lost) break
        }
        if (gameState == -1) onGameEnd()
        if (numUnclicked == getKernelNum()) gameState = 1
        return gameState
    }

    /**
     * Places a given number of kernels on the board.
     * @param kernelNum number of kernels to add to the board.
      */
    private fun populateBoard(kernelNum: Int) {
        var kernels = kernelNum // Number of kernels to add

        // Checks if the number of kernels requested can fit onto the board
        if (currentKernels + kernelNum >= xSize * ySize - 1) {
            kernels = (xSize * ySize - 1) - currentKernels // Maximum allowed number of kernels
        }

        // Adds kernels to the board
        for (i in 1..kernels) {
            var kernelAdded = false

            // Loops until a suitable placement can be found (infinite loop is possible)
            while (!kernelAdded) {
                // Gets a random tile from the board
                val xPos = Random.nextInt(0, xSize)
                val yPos = Random.nextInt(0, ySize)
                val tile = board[xPos][yPos]

                // Checks that a mine can be placed in this position
                if (!tile.isKernel && !tile.isVisible) {
                    tile.isKernel = true
                    currentKernels++
                    kernelAdded = true

                    // Updates all nearby tiles accordingly
                    updateTiles(xPos, yPos, true)
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
        // For all tiles surrounding the given tile
        for (x in xPos-1..xPos+1) {
            for (y in yPos-1..yPos+1) {
                // If the tile position is valid
                if (x in 0..<xSize && y in 0..<ySize) {
                    // Checks that it is not the original tile.
                    if (x != xPos || y != yPos) {
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

    /**
     * Prints out the values of all tiles in the board for debugging purposes
     */
    fun debugPrintBoard() {
        for (y in 0..<ySize) {
            var output = ""

            for (x in 0..<xSize) {
                val tile = board[x][y]
                output += if (tile.isKernel) {
                    "X, "
                } else {
                    board[x][y].num.toString() + ", "
                }
            }

            Log.d("BOARD", output)
        }
    }
}
