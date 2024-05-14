package io.github.polarbearsam.kernelpop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.ContextThemeWrapper
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

const val DEFAULT_NAME = "Easy"
const val DEFAULT_X_SIZE = 9
const val DEFAULT_Y_SIZE = 9
const val DEFAULT_NUM_KERNELS = 10

/**
 * Class which handles the game activity
 */
class KernelPop : AppCompatActivity() {
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
    private val winPop = R.drawable.pop
    private val empty = R.drawable.empty
    private val unclicked = R.drawable.unclicked
    private val flagged = R.drawable.flagged

    // Primary data structure used to represent the board of tiles, replaced every game
    private lateinit var board: Board

    private lateinit var kernelCounter: TextView
    private lateinit var timeCounter: TextView

    // Game stats
    private var hasFirstClickOccured = false
    private var initTime : Long = 0 // Seconds
    private var name = DEFAULT_NAME
    private var rows = DEFAULT_X_SIZE
    private var cols = DEFAULT_Y_SIZE
    private var kernels = DEFAULT_NUM_KERNELS

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

        val extras = intent.extras
        if (extras != null) {
            name = extras.getString("name").toString()
            rows = extras.getInt("rows")
            cols = extras.getInt("cols")
            kernels = extras.getInt("kernels")
        }

        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics) // Recently deprecated, may need new method
        val screenWidth = displayMetrics.widthPixels
        newGame(screenWidth, cols, rows, kernels)

        restartButton.setOnClickListener {
            newGame(screenWidth, cols, rows, kernels)
        }

        // Navigation bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_game
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_help -> {
                    startActivity(Intent(applicationContext, HelpPage::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_game -> true
                R.id.navigation_modes -> {
                    val i = Intent(applicationContext, ModesPage::class.java)
                        i.putExtra("name", name)
                            .putExtra("rows", rows)
                            .putExtra("cols", cols)
                            .putExtra("kernels", kernels)
                    startActivity(i)
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
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
        timeCounter.text = "00:00"

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
                        val sharedPref = getSharedPreferences(name, Context.MODE_PRIVATE)
                        val bestTime = sharedPref.getLong("bestTime", 0)
                        val curTime = (System.currentTimeMillis() / 1000L) - initTime
                        if (curTime < bestTime) {
                            val editor = sharedPref.edit()
                            editor.putLong("bestTime", curTime)
                            editor.apply()
                        }
                        Toast.makeText(this, R.string.win_text, Toast.LENGTH_LONG).show()
                    } else if (newGameState == -1) {
                        Toast.makeText(this, R.string.lose_text, Toast.LENGTH_LONG).show()
                    }
                }

                thisButton.setOnLongClickListener(View.OnLongClickListener {
                    if (board.getGameState() != 0)
                        return@OnLongClickListener false
                    if (tileData != null) {
                        if (!tileData.isVisible) {
                            tileData.isFlagged = !tileData.isFlagged
                        }
                    }
                    refreshBoard()
                    return@OnLongClickListener true
                })

            }
        }
    }

    /**
     * launches a coroutine to delay the counter updates on a loop
     */
    private suspend fun launchTimerRoutine() {
        while (board.getGameState() == 0 && hasFirstClickOccured) {
            val timeElapsed = (System.currentTimeMillis() / 1000L) - initTime
            withContext(Dispatchers.Main) {
                timeCounter.text = formatTimeString(timeElapsed)
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
                var curDrawable: Int = if (tileData.isVisible) {
                    if (tileData.isKernel) clickedPop else getDrawableFromTileType(tileData.num)
                } else {unclicked}
                if (tileData.isFlagged && board.getGameState() == 0) {curDrawable = flagged}
                else if (tileData.isKernel && board.getGameState() == 1) {curDrawable = winPop}

                if (tileData.isKernel && !tileData.isVisible && !tileData.isFlagged) numUnclickedKernels++
                thisButton.setImageDrawable(AppCompatResources.getDrawable(this, curDrawable))
            }
        }
        kernelCounter.text = numUnclickedKernels.toString()
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

    companion object {
        /**
         * Converts number of elapsed seconds into a timestamp
         * @param time number of seconds
         * @return string timestamp
         */
        fun formatTimeString(time: Long): String {
            val numSeconds = time % 60
            val numMins = time / 60
            val numHours = time / 3600
            val shownText = if (numHours.toInt() == 0)
                String.format(Locale.US, "%02d:%02d", numMins, numSeconds)
            else
                String.format(Locale.US, "%02d:%02d:%02d", numHours, numMins, numSeconds)
            return shownText
        }
    }
}