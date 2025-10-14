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
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        /*val textView = findViewById<TextView>(R.id.titleText)
        db.collection("prueba").document("1").get().addOnSuccessListener { document ->
            if (document.exists()) {
                textView.text = document.getString("nombre")
            }
        }*/

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