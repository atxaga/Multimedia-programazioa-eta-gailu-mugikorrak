package com.example.f1wardle

import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.gridlayout.widget.GridLayout
import com.example.f1wardle.data.LocalDataRepository
import com.example.f1wardle.data.Pilot
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout

class MainActivity : AppCompatActivity() {

    private lateinit var repository: LocalDataRepository
    private lateinit var gridAttempts: GridLayout
    private lateinit var inputPiloto: AutoCompleteTextView

    private lateinit var targetPilot: Pilot
    private lateinit var allPilots: List<Pilot>

    private val flagCodeMap = mapOf(
        "flag_gbr" to "gb",
        "flag_deu" to "de",
        "flag_esp" to "es",
        "flag_fin" to "fi",
        "flag_col" to "co",
        "flag_aus" to "au",
        "flag_mex" to "mx",
        "flag_fra" to "fr",
        "flag_can" to "ca",
        "flag_mco" to "mc",
        "flag_tha" to "th",
        "flag_jpn" to "jp",
        "flag_ita" to "it",
        "flag_bra" to "br",
        "flag_chn" to "cn",
        "flag_nld" to "nl",
        "flag_nzl" to "nz",
        "flag_swe" to "se",
        "flag_bel" to "be",
        "flag_che" to "ch",
        "flag_idn" to "id",
        "flag_dnk" to "dk",
        "flag_usa" to "us"
    )

    private var attempt = 0
    private val maxAttempts = 6
    private val cols = 7

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1️⃣ Inicializar el repositorio
        repository = LocalDataRepository(this)

        // 2️⃣ Cargar todos los pilotos desde el JSON
        allPilots = repository.loadPilots()

        // 3️⃣ Seleccionar un piloto objetivo aleatorio
        targetPilot = allPilots.random()

        // 4️⃣ Inicializar vistas
        gridAttempts = findViewById(R.id.gridAttempts)
        inputPiloto = findViewById(R.id.inputPiloto)

        // 5️⃣ Configurar autocompletado de nombres
        val names = allPilots.map { it.name }
        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            names
        )
        (inputPiloto as android.widget.AutoCompleteTextView).setAdapter(adapter)

        // 6️⃣ Configurar el grid y la lógica del input
        setupGrid()
        setupInput()
    }

    private fun setupGrid() {
        gridAttempts.removeAllViews()
        gridAttempts.rowCount = maxAttempts
        gridAttempts.columnCount = cols

        for (i in 0 until maxAttempts * cols) {
            val cellLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 0
                    height = 200  // altura fija, ajusta a tu gusto
                    columnSpec = GridLayout.spec(i % cols, 1f)
                    rowSpec = GridLayout.spec(i / cols)
                    setMargins(4, 4, 4, 4)
                }
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.cell_background)
            }

            // ImageView para bandera/logo
            val image = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(60, 60) // tamaño de imagen
                scaleType = ImageView.ScaleType.FIT_CENTER
                visibility = View.GONE // se muestra solo si hay imagen
            }

            // TextView para el texto
            val text = TextView(this).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                gravity = Gravity.CENTER
                setTextColor(Color.WHITE)
                textSize = 18f // un poco más grande
                text = ""
            }

            cellLayout.addView(image)
            cellLayout.addView(text)

            gridAttempts.addView(cellLayout)
        }
    }


    private fun setupInput() {
        val autoComplete = inputPiloto as AutoCompleteTextView
        autoComplete.isSingleLine = true
        autoComplete.imeOptions = EditorInfo.IME_ACTION_DONE
        autoComplete.threshold = 1 // mostrar sugerencias tras 1 carácter

        // Clic/tap en item de la lista (comportamiento nativo)
        autoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedName = autoComplete.adapter.getItem(position) as String
            handleAttempt(selectedName)
            autoComplete.text.clear()
        }

        // Cuando el usuario pulsa Enter/Done: intentamos completar la sugerencia resaltada
        autoComplete.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_GO ||
                actionId == EditorInfo.IME_ACTION_SEARCH
            ) {
                handleEnterWithCompletion(autoComplete)
                true
            } else false
        }

        // Tecla física Enter (emulador / algunos teclados)
        autoComplete.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                handleEnterWithCompletion(autoComplete)
                true
            } else false
        }
    }

    private fun fillRow(pilot: Pilot, rowIndex: Int) {
        val columns = listOf(
            pilot.flag,                              // 0: bandera
            pilot.name.take(3).uppercase(),          // 1: nombre
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

            // Colorear como antes
            val color = when (colIndex) {
                0 -> colorFlag(pilot.flag, targetPilot.flag)
                1 -> colorString(pilot.name, targetPilot.name)
                2 -> colorTeam(pilot.team, targetPilot)
                3 -> compareNumber(pilot.number, targetPilot.number, reversed = true)
                4 -> compareAge(pilot.age, targetPilot.age)
                5 -> compareNumber(pilot.debut, targetPilot.debut, reversed = true)
                6 -> compareNumber(pilot.wins, targetPilot.wins, reversed = true)
                else -> Color.DKGRAY
            }
            cell.setBackgroundColor(color)

            // 🎌 Poner la bandera solo en la primera columna
            if (colIndex == 0) {
                val isoCode = flagCodeMap[pilot.flag] ?: "unknown"
                val flagResId = resources.getIdentifier(isoCode, "drawable", packageName)
                cell.setCompoundDrawablesWithIntrinsicBounds(flagResId, 0, 0, 0)
                cell.compoundDrawablePadding = 8
            } else {
                cell.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            }
        }
    }

    /**
     * Si el dropdown está abierto y hay una sugerencia resaltada, performCompletion()
     * aplica esa sugerencia al text field. Esperamos brevemente a que se aplique y
     * luego procesamos el texto resultante. Si no hay dropdown, procesamos el texto tal cual.
     */
    private fun handleEnterWithCompletion(autoComplete: AutoCompleteTextView) {
        if (autoComplete.isPopupShowing) {
            // Forzar que se aplique la sugerencia actualmente marcada (si la hay)
            autoComplete.performCompletion()

            // Pequeño delay para que la UI sustituya el texto con la sugerencia seleccionada
            Handler(Looper.getMainLooper()).postDelayed({
                val selectedText = autoComplete.text.toString().trim()
                if (selectedText.isNotEmpty()) {
                    handleAttempt(selectedText)
                    autoComplete.text.clear()
                }
            }, 50) // 50 ms suele ser suficiente; puedes subir a 100 si fallara en algún dispositivo
        } else {
            // Si no hay dropdown abierto, usar el texto tal cual
            val text = autoComplete.text.toString().trim()
            if (text.isNotEmpty()) {
                handleAttempt(text)
                autoComplete.text.clear()
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
