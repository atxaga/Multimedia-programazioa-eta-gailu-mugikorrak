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
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.compose.material3.Button

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

        repository = LocalDataRepository(this)

        allPilots = repository.loadPilots()

        targetPilot = allPilots.random()

        gridAttempts = findViewById(R.id.gridAttempts)
        inputPiloto = findViewById(R.id.inputPiloto)


        val names = allPilots.map { it.name }
        val adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_dropdown_item_1line,
            names
        )
        (inputPiloto as android.widget.AutoCompleteTextView).setAdapter(adapter)
        inputPiloto.apply {
            threshold = 1
            dropDownVerticalOffset = -height
            dropDownHeight = 600
        }

        findViewById<Button>(R.id.btnReiniciar).setOnClickListener { resetGame()
        }

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
                    height = 200
                    columnSpec = GridLayout.spec(i % cols, 1f)
                    rowSpec = GridLayout.spec(i / cols)
                    setMargins(4, 4, 4, 4)
                }
                gravity = Gravity.CENTER
                setBackgroundResource(R.drawable.cell_background)
            }

            val image = ImageView(this).apply {
                layoutParams = LinearLayout.LayoutParams(60, 60) // tamaño de imagen
                scaleType = ImageView.ScaleType.FIT_CENTER
                visibility = View.GONE
            }

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
        autoComplete.threshold = 1

        autoComplete.setOnItemClickListener { _, _, position, _ ->
            val selectedName = autoComplete.adapter.getItem(position) as String
            handleAttempt(selectedName)
            autoComplete.text.clear()
        }

        autoComplete.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_GO ||
                actionId == EditorInfo.IME_ACTION_SEARCH
            ) {
                handleEnterWithCompletion(autoComplete)
                true
            } else false
        }

        autoComplete.setOnKeyListener { _, keyCode, keyEvent ->
            if (keyEvent.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                handleEnterWithCompletion(autoComplete)
                true
            } else false
        }
    }

    private fun fillRow(pilot: Pilot, attemptIndex: Int) {
        val attributes = listOf(
            pilot.name.split(" ").last().take(3).uppercase(), // 0 Apellido
            pilot.flag,                                       // 1 Bandera
            pilot.team,                                       // 2 Equipo
            pilot.number.toString(),                           // 3 Número de coche
            pilot.age.toString(),                              // 4 Edad
            pilot.debut.toString(),                            // 5 Debut
            pilot.wins.toString()                              // 6 Victorias
        )

        for (i in attributes.indices) {
            val cellIndex = attemptIndex * cols + i
            val cellLayout = gridAttempts.getChildAt(cellIndex) as LinearLayout
            val cellImage = cellLayout.getChildAt(0) as ImageView
            val cellText = cellLayout.getChildAt(1) as TextView

            val attr = attributes[i]

            when (i) {
                0 -> { // Apellido
                    cellImage.visibility = View.GONE
                    cellText.text = attr
                    if (pilot.name == targetPilot.name) {
                        cellLayout.setBackgroundColor(Color.parseColor("#00C853"))
                    } else {
                        cellLayout.setBackgroundResource(R.drawable.cell_background)
                    }
                }

                1 -> {
                    cellText.text = ""
                    cellImage.visibility = View.VISIBLE
                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                        setMargins(8, 8, 8, 8) // margen para que no se corte
                    }
                    cellImage.layoutParams = params
                    cellImage.scaleType = ImageView.ScaleType.FIT_CENTER
                    cellImage.setImageResource(getFlagResource(flagCodeMap[pilot.flag] ?: "us"))

                    if (pilot.flag == targetPilot.flag) {
                        cellLayout.setBackgroundColor(Color.parseColor("#00C853")) // verde
                    } else {
                        cellLayout.setBackgroundColor(Color.parseColor("#D50000")) // rojo
                    }
                }

                2 -> {
                    cellText.text = ""
                    cellImage.visibility = View.VISIBLE

                    val params = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                    ).apply {
                        setMargins(8, 8, 8, 8)
                    }
                    cellImage.layoutParams = params
                    cellImage.scaleType = ImageView.ScaleType.FIT_CENTER

                    val teamLogoRes = getTeamLogoResource(pilot.team)
                    if (teamLogoRes != 0) {
                        cellImage.setImageResource(teamLogoRes)
                    } else {

                        cellImage.visibility = View.GONE
                        cellText.text = pilot.team
                    }

                    cellLayout.setBackgroundColor(colorTeam(pilot.team, targetPilot))
                }

                3 -> { // Número
                    cellImage.visibility = View.GONE
                    cellText.text = attr
                    cellLayout.setBackgroundColor(compareNumber(attr.toInt(), targetPilot.number))
                }

                4 -> { // Edad
                    cellImage.visibility = View.GONE
                    cellText.text = attr
                    cellLayout.setBackgroundColor(compareAge(attr.toInt(), targetPilot.age))
                }

                5 -> { // Debut
                    cellImage.visibility = View.GONE
                    cellText.text = attr
                    cellLayout.setBackgroundColor(compareNumber(attr.toInt(), targetPilot.debut))
                }

                6 -> { // Victorias
                    cellImage.visibility = View.GONE
                    cellText.text = attr
                    // verde si coincide, amarillo si < target, morado si > target
                    cellLayout.setBackgroundColor(
                        when {
                            attr.toInt() == targetPilot.wins -> Color.parseColor("#00C853")
                            attr.toInt() < targetPilot.wins -> Color.parseColor("#FFD600")
                            else -> Color.parseColor("#8E24AA")
                        }
                    )
                }
            }
        }
    }



    private fun getFlagResource(code: String): Int {
        return resources.getIdentifier(code, "drawable", packageName)
    }

    private fun getTeamLogoResource(teamName: String): Int {
        val resourceName = "logo_" + teamName
            .lowercase()
            .replace(" ", "")
            .replace("-", "")
        return resources.getIdentifier(resourceName, "drawable", packageName)
    }

    private fun handleEnterWithCompletion(autoComplete: AutoCompleteTextView) {
        if (autoComplete.isPopupShowing) {
            autoComplete.performCompletion()

            Handler(Looper.getMainLooper()).postDelayed({
                val selectedText = autoComplete.text.toString().trim()
                if (selectedText.isNotEmpty()) {
                    handleAttempt(selectedText)
                    autoComplete.text.clear()
                }
            }, 50)

            val text = autoComplete.text.toString().trim()
            if (text.isNotEmpty()) {
                handleAttempt(text)
                autoComplete.text.clear()
            }
        }
    }



    private fun handleAttempt(name: String) {
        if (attempt >= maxAttempts) {
            Toast.makeText(this, "Ez daukazu saiakera gehiago!", Toast.LENGTH_SHORT).show()
            return
        }

        val pilot = allPilots.find { it.name.equals(name, ignoreCase = true) }
        if (pilot == null) {
            Toast.makeText(this, "Gidaria ez da aurkitu", Toast.LENGTH_SHORT).show()
            return
        }

        fillRow(pilot, attempt)
        attempt++

        if (pilot.name.equals(targetPilot.name, ignoreCase = true)) {
            Toast.makeText(this, "🎉 Ondo! gidaria ${targetPilot.name} zen", Toast.LENGTH_LONG).show()
        } else if (attempt == maxAttempts) {
            Toast.makeText(this, "❌ Bukaera. gidaria ${targetPilot.name} zen", Toast.LENGTH_LONG).show()
        }
    }

    private fun colorFlag(guess: String, target: String): Int =
        if (guess.equals(target, ignoreCase = true)) Color.parseColor("#00C853") else Color.parseColor("#D50000")

    private fun colorString(guess: String, target: String): Int =
        if (guess.equals(target, ignoreCase = true)) Color.parseColor("#00C853") else Color.parseColor("#D50000")

    private fun colorTeam(guess: String, target: Pilot): Int = when {
        guess.equals(target.team, ignoreCase = true) -> Color.parseColor("#00C853")
        target.pastTeams.any { it.equals(guess, ignoreCase = true) } -> Color.parseColor("#FFD600")
        else -> Color.parseColor("#D50000") // rojo
    }
    private fun compareNumber(guess: Int, target: Int): Int {
        return when {
            guess == target -> Color.parseColor("#00C853")
            guess < target -> Color.parseColor("#FFD600")
            else -> Color.parseColor("#8E24AA")
        }
    }

    private fun compareAge(guess: Int, target: Int): Int {
        return when {
            guess == target -> Color.parseColor("#00C853")
            guess < target -> Color.parseColor("#FFD600")
            else -> Color.parseColor("#8E24AA")
        }
    }

    private fun resetGame() {
        attempt = 0
        gridAttempts.removeAllViews()
        setupGrid()

        targetPilot = allPilots.random()

        Toast.makeText(this, "🔄 Hasi da partida berria!", Toast.LENGTH_SHORT).show()
    }
}
