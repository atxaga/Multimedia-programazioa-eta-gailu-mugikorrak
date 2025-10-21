package com.example.f1wardle.data

import java.text.SimpleDateFormat
import java.util.*

data class Pilot(
    val name: String,
    val team: String,
    val number: Int,
    val birthDate: String,
    val debut: Int,
    val wins: Int,
    val flag: String,
    val pastTeams: List<String>
) {
    val age: Int
        get() {
            return try {
                val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                val birth = sdf.parse(birthDate)
                val today = Calendar.getInstance()

                val birthCal = Calendar.getInstance()
                birthCal.time = birth!!

                var age = today.get(Calendar.YEAR) - birthCal.get(Calendar.YEAR)

                // Ajustar si aún no ha cumplido años este año
                if (today.get(Calendar.DAY_OF_YEAR) < birthCal.get(Calendar.DAY_OF_YEAR)) {
                    age--
                }

                age
            } catch (e: Exception) {
                0
            }
        }
}