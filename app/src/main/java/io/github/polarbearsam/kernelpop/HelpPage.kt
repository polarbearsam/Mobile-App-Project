package io.github.polarbearsam.kernelpop

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView

class HelpPage : AppCompatActivity() {
    // If returning to Game through bottom bar
    private var name = DEFAULT_NAME
    private var rows = DEFAULT_X_SIZE
    private var cols = DEFAULT_Y_SIZE
    private var kernels = DEFAULT_NUM_KERNELS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_help_page)
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

        // Navigation bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_nav_view)
        bottomNavigationView.selectedItemId = R.id.navigation_game
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_help -> true
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
                R.id.navigation_modes -> {
                    startActivity(Intent(applicationContext, ModesPage::class.java))
                    overridePendingTransition(0, 0)
                    true
                }
                else -> false
            }
        }
    }
}