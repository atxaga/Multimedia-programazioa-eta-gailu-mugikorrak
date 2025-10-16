package com.example.f1wardle

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import com.example.f1wardle.data.LocalDataRepository
import com.example.f1wardle.data.Pilot

class MainActivity : AppCompatActivity() {

    private lateinit var repository: LocalDataRepository
    private lateinit var gridAttempts: GridLayout
    private lateinit var inputPiloto: EditText

    private lateinit var targetPilot: Pilot
    private lateinit var allPilots: List<Pilot>
    private var attempt = 0
    private val maxAttempts = 6
    private val cols = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializar repo y datos
        repository = LocalDataRepository(this)
        allPilots = repository.loadPilots()
        targetPilot = allPilots.random()

        gridAttempts = findViewById(R.id.gridAttempts)
        inputPiloto = findViewById(R.id.inputPiloto)

        setupGrid()
        setupInput()

        // Opcional: mostrar primer piloto en título para debug
        val titleText = findViewById<TextView>(R.id.titleText)
        titleText.text = "Adivina: ${targetPilot.name}" // quita o cambia cuando quieras
    }

    private fun setupGrid() {
        gridAttempts.removeAllViews()
        gridAttempts.rowCount = maxAttempts
        gridAttempts.columnCount = cols

        for (i in 0 until maxAttempts * cols) {
            val cell = TextView(this).apply {
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = GridLayout.LayoutParams.WRAP_CONTENT
                    columnSpec = GridLayout.spec(i % cols, 1f)
                    rowSpec = GridLayout.spec(i / cols)
                    setMargins(4, 4, 4, 4)
                }
                setBackgroundResource(R.drawable.cell_background)
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                textSize = 16f
                text = ""
                setPadding(8, 16, 8, 16)
            }
            gridAttempts.addView(cell)
        }
    }

    private fun setupInput() {
        // Asegurarnos por código de que actúa como "single line" y que el IME muestra DONE
        inputPiloto.isSingleLine = true
        inputPiloto.imeOptions = EditorInfo.IME_ACTION_DONE

        // 1) Listener para la acción IME (Done, Search, Go...)
        inputPiloto.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE
                || actionId == EditorInfo.IME_ACTION_SEARCH
                || actionId == EditorInfo.IME_ACTION_GO
            ) {
                val text = inputPiloto.text.toString().trim()
                if (text.isNotEmpty()) {
                    handleAttempt(text)
                    inputPiloto.text.clear()
                }
                true
            } else {
                false
            }
        }

        // 2) Listener para la tecla física Enter (por si IME no envía correctamente)
        inputPiloto.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                val text = inputPiloto.text.toString().trim()
                if (text.isNotEmpty()) {
                    handleAttempt(text)
                    inputPiloto.text.clear()
                }
                true
            } else {
                false
            }
        }
    }

    private fun handleAttempt(name: String) {
        if (attempt >= maxAttempts) {
            Toast.makeText(this, "¡Ya no tienes más intentos!", Toast.LENGTH_SHORT).show()
            return
        }

        val pilot = allPilots.find { it.name.equals(name, ignoreCase = true) }
        if (pilot == null) {
            Toast.makeText(this, "Piloto no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        fillRow(pilot, attempt)
        attempt++

        if (pilot.name.equals(targetPilot.name, ignoreCase = true)) {
            Toast.makeText(this, "🎉 ¡Correcto! Era ${targetPilot.name}", Toast.LENGTH_LONG).show()
        } else if (attempt == maxAttempts) {
            Toast.makeText(this, "❌ Fin. Era ${targetPilot.name}", Toast.LENGTH_LONG).show()
        }
    }

    private fun fillRow(pilot: Pilot, rowIndex: Int) {
        // Asegúrate de que la lista de columnas tenga exactamente 'cols' elementos
        val columns = listOf(
            pilot.flag,                              // 0: bandera (string, luego puedes cambiar por imagen)
            pilot.name.take(3).uppercase(),          // 1: nombre (3 letras)
            pilot.team,                              // 2: equipo
            pilot.number.toString(),                 // 3: número
            pilot.age.toString(),                    // 4: edad
            pilot.debut.toString(),                  // 5: debut
            pilot.wins.toString()                    // 6: victorias
        )

        for (colIndex in 0 until cols) {
            val cellIndex = rowIndex * cols + colIndex
            val cell = gridAttempts.getChildAt(cellIndex) as TextView
            cell.text = columns[colIndex]

            val color = when (colIndex) {
                0 -> colorFlag(pilot.flag, targetPilot.flag)
                1 -> colorString(pilot.name, targetPilot.name)
                2 -> colorTeam(pilot.team, targetPilot)
                3 -> compareNumber(pilot.number, targetPilot.number, reversed = true)  // número de coche
                4 -> compareAge(pilot.age, targetPilot.age)         // edad
                5 -> compareNumber(pilot.debut, targetPilot.debut, reversed = true)   // debut funciona bien
                6 -> compareNumber(pilot.wins, targetPilot.wins, reversed = true)      // victorias

                else -> Color.DKGRAY
            }

            cell.setBackgroundColor(color)
        }
    }

    // === Funciones de coloreado ===
    private fun colorFlag(guess: String, target: String): Int =
        if (guess.equals(target, ignoreCase = true)) Color.parseColor("#00C853") else Color.parseColor("#D50000")

    private fun colorString(guess: String, target: String): Int =
        if (guess.equals(target, ignoreCase = true)) Color.parseColor("#00C853") else Color.parseColor("#D50000")

    private fun colorTeam(guess: String, target: Pilot): Int = when {
        guess.equals(target.team, ignoreCase = true) -> Color.parseColor("#00C853") // verde
        target.pastTeams.any { it.equals(guess, ignoreCase = true) } -> Color.parseColor("#FFD600") // amarillo
        else -> Color.parseColor("#D50000") // rojo
    }

    /**Fer
     * compareNumber:
     * - si igual -> verde
     * - si 'guess' > 'target' -> amarillo (o morado dependiendo de tu regla)
     * - si 'guess' < 'target' -> morado
     *
     * Param reversed = true invierte la comparación (útil para 'debut' si un debut menor es "mejor")
     */
    private fun compareNumber(guess: Int, target: Int, reversed: Boolean = false): Int {
        return when {
            guess == target -> Color.parseColor("#00C853") // verde
            (!reversed && guess > target) || (reversed && guess < target) -> Color.parseColor("#FFD600") // amarillo
            else -> Color.parseColor("#8E24AA") // morado
        }
    }

    private fun compareAge(guess: Int, target: Int): Int {
        return when {
            guess == target -> Color.parseColor("#00C853") // verde
            guess < target -> Color.parseColor("#FFD600")  // amarillo
            else -> Color.parseColor("#8E24AA")           // morado
        }
    }
}
