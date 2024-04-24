package io.github.polarbearsam.kernelpop

import android.os.Bundle
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
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

    private val clickedPop = R.drawable.clicked_pop
    private val empty = R.drawable.empty
    private val unclicked = R.drawable.unclicked

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
                //thisButton.setLayoutParams(RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT))
                thisButton.setImageDrawable(AppCompatResources.getDrawable(this, unclicked))
                thisButton.scaleType = ImageView.ScaleType.FIT_CENTER;
                thisButton.adjustViewBounds = true;
                // Below, I specified image size as 1x1px just as a text, but it still ends up huge! Why?
                // Also, GridView never wraps around to the second row no matter what I do. Why?
                if (tileData != null) {
                    thisButton.setTag(R.id.IMAGE_DATA, tileData.num)
                }
                // TODO auto-determine image sizes to perfectly fit screen width
                grid.addView(thisButton, ViewGroup.LayoutParams(200, 200))

                thisButton.setOnClickListener {
                    val data = thisButton.getTag(R.id.IMAGE_DATA) as Int
                    thisButton.setImageDrawable(AppCompatResources.getDrawable(this, getDrawableFromTileType(data)))
                }

            }
        }
    }

    fun getDrawableFromTileType(type : Int): Int {
        return when (type) {
            0 -> empty
            1 -> imgOne
            2 -> imgTwo
            3 -> imgThree
            4 -> imgFour
            5 -> imgFive
            6 -> imgSix
            7 -> imgSeven
            8 -> imgEight
            9 -> clickedPop
            else -> unclicked
        }
    }
}