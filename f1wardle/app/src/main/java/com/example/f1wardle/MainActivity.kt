package com.example.f1wardle

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.gridlayout.widget.GridLayout

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val gridAttempts = findViewById<GridLayout>(R.id.gridAttempts)
        val rows = 6
        val cols = 7

        // Limpia cualquier view previa
        gridAttempts.removeAllViews()

        for (i in 0 until rows) {
            for (j in 0 until cols) {
                val cell = TextView(this).apply {
                    layoutParams = GridLayout.LayoutParams().apply {
                        width = 0
                        height = GridLayout.LayoutParams.WRAP_CONTENT
                        columnSpec = GridLayout.spec(j, 1f)
                        rowSpec = GridLayout.spec(i)
                        setMargins(4, 4, 4, 4)
                    }
                    setBackgroundResource(R.drawable.cell_background) // Pon un drawable con borde y fondo
                    gravity = Gravity.CENTER
                    setTextColor(Color.WHITE)
                    textSize = 18f
                    text = ""  // Aquí pondrás la letra o palabra
                    setPadding(8, 16, 8, 16)
                }
                gridAttempts.addView(cell)
            }
        }
    }
}