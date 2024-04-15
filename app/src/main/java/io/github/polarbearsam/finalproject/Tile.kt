package io.github.polarbearsam.kernelpop

class Tile(isMine: Boolean) {
    var num : Int = 0

    init {
        if (isMine) {
            num = 9
        }
    }
}