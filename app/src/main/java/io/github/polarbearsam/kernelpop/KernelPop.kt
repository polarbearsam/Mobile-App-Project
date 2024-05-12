package io.github.polarbearsam.kernelpop

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ContextThemeWrapper
import android.view.GestureDetector
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/* TODO These constants are for demo purposes and should be deleted for final product.
Instead, we should have a presets container for difficulties that contains these vals. */
const val X_SIZE = 16
const val Y_SIZE = 16
const val NUM_KERNELS = 40

/**
 * Class which handles the game activity
 */
abstract class KernelPop : AppCompatActivity(), GestureDetector.OnGestureListener,
GestureDetector.OnDoubleTapListener {
    // Number button drawables
    private val imgOne = R.drawable.one
    private val imgTwo = R.drawable.two
    private val imgThree = R.drawable.three
    private val imgFour = R.drawable.four
    private val imgFive = R.drawable.five
    private val imgSix = R.drawable.six
    private val imgSeven = R.drawable.seven
    private val imgEight = R.drawable.eight

    // Special button drawables
    private val clickedPop = R.drawable.clicked_pop
    private val empty = R.drawable.empty
    private val unclicked = R.drawable.unclicked

    // Primary data structure used to represent the board of tiles, replaced every game
    private lateinit var board: Board

    private lateinit var kernelCounter: TextView
    private lateinit var timeCounter: TextView

    // Flag that is used to apply unique code to first button press
    private var hasFirstClickOccured = false
    private var initTime : Long = 0 // Seconds

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
        kernelCounter = findViewById(R.id.kernelCounter)
        timeCounter = findViewById(R.id.timeCounter)

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics) // Recently deprecated, may need new method
        val screenWidth = displayMetrics.widthPixels
        newGame(screenWidth, X_SIZE, Y_SIZE, NUM_KERNELS)

        restartButton.setOnClickListener {
            newGame(screenWidth, X_SIZE, Y_SIZE, NUM_KERNELS)
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
        hasFirstClickOccured = false
        grid.columnCount = xSize
        grid.rowCount = ySize

        board = Board(xSize, ySize, kernelNum)

        val timerRoutine = CoroutineScope(Dispatchers.Main)

        kernelCounter.text = board.getKernelNum().toString()

        // Populate board tiles
        for (x in 0..<xSize) {
            for (y in 0..<ySize) {
                val tileData = board.getTile(x, y)
                // Apply correct image for tile
                // The style is needed to prevent standard Android padding from separating tiles
                val thisButton = ImageButton(ContextThemeWrapper(this, androidx.appcompat.R.style.Base_TextAppearance_AppCompat_Widget_Button_Borderless_Colored), null, 0)
                thisButton.setImageDrawable(AppCompatResources.getDrawable(this, unclicked))
                thisButton.isLongClickable = true
                thisButton.scaleType = ImageView.ScaleType.FIT_XY
                thisButton.adjustViewBounds = true
                thisButton.background = null

                // To define the size and location of tiles, LayoutParams of GridLayout is instantiated
                val layoutParams = GridLayout.LayoutParams()
                layoutParams.rowSpec = GridLayout.spec(y)
                layoutParams.columnSpec = GridLayout.spec(x)
                layoutParams.width = screenWidth / xSize
                layoutParams.height = screenWidth / xSize

                if (tileData != null) {
                    thisButton.setTag(R.id.IMAGE_DATA, tileData.num)
                    tileData.curImageButton = thisButton
                }

                grid.addView(thisButton, layoutParams)

                thisButton.setOnClickListener {
                    if (board.getGameState() != 0)
                        return@setOnClickListener
                    if (!hasFirstClickOccured) {
                        // Guarantee safe starting zone
                        hasFirstClickOccured = true
                        board.revealFirstTile(x, y)
                        if (tileData != null) {
                            thisButton.setTag(R.id.IMAGE_DATA, tileData.num)
                        }
                        initTime = System.currentTimeMillis() / 1000L
                        timerRoutine.launch { launchTimerRoutine() }
                    }
                    board.floodFill(x, y)
                    val newGameState = board.checkGameWon()
                    refreshBoard()
                    if (newGameState == 1) {
                        Toast.makeText(this, R.string.win_text, Toast.LENGTH_LONG).show()
                    } else if (newGameState == -1) {
                        Toast.makeText(this, R.string.lose_text, Toast.LENGTH_LONG).show()
                    }
                }

                thisButton.setOnLongClickListener(View.OnLongClickListener() {
                    if (tileData != null) {
                        tileData.isFlagged = !tileData.isFlagged
                    }
                    return@OnLongClickListener true
                })

            }
        }
    }

    private suspend fun launchTimerRoutine() {
        while (board.getGameState() == 0 && hasFirstClickOccured) {
            val timeElapsed = (System.currentTimeMillis() / 1000L) - initTime
            val numSeconds = timeElapsed % 60
            val numMins = timeElapsed / 60
            val numHours = timeElapsed / 3600
            withContext(Dispatchers.Main) {
                val shownText = if (numHours.toInt() == 0)
                    String.format(Locale.US, "%02d:%02d", numMins, numSeconds)
                else
                    String.format(Locale.US, "%02d:%02d:%02d", numHours, numMins, numSeconds)
                timeCounter.text = String.format(Locale.US, shownText)
            }
            delay(1000L)
        }
    }

    /**
     * Updates the board interface to match the Model
     */
    private fun refreshBoard() {
        var numUnclickedKernels = 0
        for (x in 0..<board.xSize) {
            for (y in 0..<board.ySize) {
                val tileData = board.getTile(x, y) ?: continue
                val thisButton = tileData.curImageButton
                thisButton.setTag(R.id.IMAGE_DATA, tileData.num)
                val curDrawable: Int = if (tileData.isVisible) {
                    if (tileData.isKernel) 9 else tileData.num
                } else { -1 }

                if (tileData.isKernel && !tileData.isVisible && !tileData.isFlagged) numUnclickedKernels++
                thisButton.setImageDrawable(AppCompatResources.getDrawable(this, getDrawableFromTileType(curDrawable)))
            }
        }
        kernelCounter.text = numUnclickedKernels.toString() // TODO factor in flags
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