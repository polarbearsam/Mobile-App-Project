package io.github.polarbearsam.kernelpop

import android.app.Activity
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
    val img_one = R.drawable.one
    val img_two = R.drawable.two
    val img_three = R.drawable.three
    val img_four = R.drawable.four
    val img_five = R.drawable.five
    val img_six = R.drawable.six
    val img_seven = R.drawable.seven
    val img_eight = R.drawable.eight

    val clickedpop = R.drawable.clickedpop
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
        var board = Board(X_SIZE, Y_SIZE, 10)
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
            1 -> img_one
            2 -> img_two
            3 -> img_three
            4 -> img_four
            5 -> img_five
            6 -> img_six
            7 -> img_seven
            8 -> img_eight
        }
    }
})