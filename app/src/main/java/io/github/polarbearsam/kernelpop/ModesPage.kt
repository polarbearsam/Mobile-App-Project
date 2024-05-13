package io.github.polarbearsam.kernelpop

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.RatingBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

// Scoring
const val FIVE_STAR_FACTOR = 4.5
const val ZERO_STAR_FACTOR = 8

// Gamemode information
val modeNames = arrayOf("Easy", "Intermediate", "Expert")
val modeCols = arrayOf(9, 12, 12)
val modeRows = arrayOf(9, 22, 40)
val modeKernels = arrayOf(10, 40, 99)

class ModesPage : AppCompatActivity() {
    // If returning to Game through bottom bar
    private var name = DEFAULT_NAME
    private var rows = DEFAULT_X_SIZE
    private var cols = DEFAULT_Y_SIZE
    private var kernels = DEFAULT_NUM_KERNELS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val extras = intent.extras
        if (extras != null) {
            name = extras.getString("name").toString()
            rows = extras.getInt("rows")
            cols = extras.getInt("cols")
            kernels = extras.getInt("kernels")
        }

        // Dynamically create cards using the inflater
        val cardContainer = findViewById<LinearLayout>(R.id.cardContainer)
        modeNames.forEachIndexed { index, thisName ->
            val newCard = this.layoutInflater.inflate(R.layout.mode_card, null)
            val viewGroup = newCard as ViewGroup
            val linearLayout = viewGroup.getChildAt(0) as LinearLayout

            // Populate text
            val textTitle = linearLayout.getChildAt(0) as TextView
            textTitle.text = thisName

            val infoText = linearLayout.getChildAt(1) as TextView
            infoText.text = String.format(Locale.US, "%d rows, %d columns, %d kernels", modeRows[index], modeCols[index], modeKernels[index])

            val sharedPref = getSharedPreferences(thisName, Context.MODE_PRIVATE)
            var bestTime = sharedPref.getLong("bestTime", Long.MAX_VALUE)
            val timerText = linearLayout.getChildAt(2) as TextView
            if (bestTime == Long.MAX_VALUE) {
                timerText.text = "Your best time: None!"
            } else {
                timerText.text = "Your best time: " + KernelPop.formatTimeString(bestTime)
            }

            val scoreBar = linearLayout.getChildAt(3) as RatingBar
            val thisFactor = (bestTime / modeKernels[index])
            scoreBar.rating = ((1 - (thisFactor - FIVE_STAR_FACTOR)/(ZERO_STAR_FACTOR - FIVE_STAR_FACTOR)) * 100).toFloat()

            val cardButton = linearLayout.getChildAt(5) as Button
            cardButton.setOnClickListener {
                val i = Intent(applicationContext, KernelPop::class.java)
                i.putExtra("name", thisName)
                    .putExtra("rows", modeRows[index])
                    .putExtra("cols", modeCols[index])
                    .putExtra("kernels", modeKernels[index])
                startActivity(i)
            }

            cardContainer.addView(newCard)
        }

        // Navigation bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_modes
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_help -> {
                    startActivity(Intent(applicationContext, HelpPage::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_game -> {
                    val i = Intent(applicationContext, KernelPop::class.java)
                    i.putExtra("name", name)
                        .putExtra("rows", rows)
                        .putExtra("cols", cols)
                        .putExtra("kernels", kernels)
                    startActivity(i)
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_modes -> true
                else -> false
            }
        }
    }
}