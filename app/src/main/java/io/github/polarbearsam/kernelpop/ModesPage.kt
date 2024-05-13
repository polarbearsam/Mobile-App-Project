package io.github.polarbearsam.kernelpop

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.Locale

// Gamemode information
val modeNames = arrayOf("Easy", "Intermediate", "Expert")
val modeCols = arrayOf(9, 12, 12)
val modeRows = arrayOf(9, 22, 40)
val modeKernels = arrayOf(10, 40, 99)

class ModesPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_modes)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Dynamically create cards using the inflater
        val cardContainer = findViewById<LinearLayout>(R.id.cardContainer)
        modeNames.forEachIndexed { index, name ->
            val newCard = this.layoutInflater.inflate(R.layout.mode_card, null)
            val viewGroup = newCard as ViewGroup
            val linearLayout = viewGroup.getChildAt(0) as LinearLayout

            // Populate text
            val textTitle = linearLayout.getChildAt(0) as TextView
            textTitle.text = name
            val infoText = linearLayout.getChildAt(1) as TextView
            infoText.text = String.format(Locale.US, "%d rows, %d columns, %d kernels", modeRows[index], modeCols[index], modeKernels[index])
            // TODO button for each card to instantiate activity of that gamemode
            cardContainer.addView(newCard)
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
                R.id.navigation_game -> {
                    startActivity(Intent(applicationContext, KernelPop::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                R.id.navigation_modes -> true
                else -> false
            }
        }
    }
}