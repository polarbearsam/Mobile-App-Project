package io.github.polarbearsam.kernelpop

import android.os.Bundle
import android.widget.GridLayout
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

const val X_SIZE = 9
const val Y_SIZE = 9

class KernelPop : AppCompatActivity() {
    private val imgOne = R.drawable.one
    private val imgTwo = R.drawable.two
    private val imgThree = R.drawable.three
    private val imgFour = R.drawable.four
    private val imgFive = R.drawable.five
    private val imgSix = R.drawable.six
    private val imgSeven = R.drawable.seven
    private val imgEight = R.drawable.eight

    val clickedPop = R.drawable.clicked_pop
    val empty = R.drawable.empty
    val unclicked = R.drawable.unclicked

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kernel_pop)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val grid = findViewById<GridLayout>(R.id.gridLayout)
        val board = Board(X_SIZE, Y_SIZE, 10)
        // Populate board tiles
        for (x in 0..<X_SIZE) {
            for (y in 0..<Y_SIZE) {
                var tileData = board.getTile(x, y)
                var thisButton = ImageButton(this)
                //thisButton.setImageDrawable()
                //grid.addView()
            }
        }
    }

    fun getDrawableFromTileType(type : Int) {
        when (type) {
            1 -> imgOne
            2 -> imgTwo
            3 -> imgThree
            4 -> imgFour
            5 -> imgFive
            6 -> imgSix
            7 -> imgSeven
            8 -> imgEight
        }
    }
}