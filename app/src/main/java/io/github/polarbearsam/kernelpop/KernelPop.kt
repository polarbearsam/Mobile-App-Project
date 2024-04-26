package io.github.polarbearsam.kernelpop

import android.os.Bundle
import android.util.DisplayMetrics
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/* TODO These constants are for demo purposes and should be deleted for final product.
Instead, we should have a presets container for difficulties that contains these vals. */
const val X_SIZE = 9
const val Y_SIZE = 9

/**
 * Class which handles the game activity
 */
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

    lateinit var board: Board;

    /**
     * Creates the UI for the game
     * @param savedInstanceState
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_kernel_pop)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val restartButton = findViewById<Button>(R.id.master_button)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics) // Recently deprecated, may need new method
        val screenWidth = displayMetrics.widthPixels
        newGame(screenWidth, X_SIZE, Y_SIZE, 10)

        restartButton.setOnClickListener {
            newGame(screenWidth, X_SIZE, Y_SIZE, 10)
        }
    }

    /**
     * Starts a new game
     * @param xSize width of game board in squares
     * @param ySize height of game board in squares
     * @param kernelNum number of squares that will become kernels
     */
    private fun newGame(screenWidth: Int, xSize: Int, ySize: Int, kernelNum: Int) {
        val grid = findViewById<GridLayout>(R.id.gridLayout)
        board = Board(xSize, ySize, kernelNum)
        // Populate board tiles
        for (x in 0..<xSize) {
            for (y in 0..<ySize) {
                val tileData = board.getTile(x, y)
                val thisButton = ImageButton(this)
                //thisButton.setLayoutParams(RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT))
                thisButton.setImageDrawable(AppCompatResources.getDrawable(this, unclicked))
                thisButton.scaleType = ImageView.ScaleType.FIT_CENTER
                thisButton.adjustViewBounds = true

                val layoutParams = GridLayout.LayoutParams()
                layoutParams.rowSpec = GridLayout.spec(x)
                layoutParams.columnSpec = GridLayout.spec(y)
                layoutParams.width = screenWidth / xSize
                layoutParams.height = screenWidth / ySize

                if (tileData != null) {
                    thisButton.setTag(R.id.IMAGE_DATA, tileData.num)
                }

                grid.addView(thisButton, layoutParams)

                thisButton.setOnClickListener {
                    val data = thisButton.getTag(R.id.IMAGE_DATA) as Int
                    thisButton.setImageDrawable(AppCompatResources.getDrawable(this, getDrawableFromTileType(data)))
                }

            }
        }
    }

    /**
     * Converts the integer value of a tile to the appropriate image
     * @param type integer value of the tile for which an image is requested
     * @return returns image drawable for that tile
     */
    private fun getDrawableFromTileType(type : Int): Int {
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