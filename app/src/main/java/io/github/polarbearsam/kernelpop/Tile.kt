package io.github.polarbearsam.kernelpop

import android.widget.ImageButton

/**
 * An object for every tile on the game board.
  */
class Tile() {
    var isFlagged = false
    var isVisible = false
    lateinit var curImageButton : ImageButton
    var num : Int = 0
        set(value) { // Limits allowed values for a tile to 0 through 9.
            if (value in 0..9) {
                field = value
            }
        }

    /**
     * Checks if a tile is a kernel
     * @return returns true if the tile is a kernel, otherwise returns false
     */
    fun isKernel(): Boolean {
        return num == 9
    }
}