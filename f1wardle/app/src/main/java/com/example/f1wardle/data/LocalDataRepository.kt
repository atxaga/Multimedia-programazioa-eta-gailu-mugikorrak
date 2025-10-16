package com.example.f1wardle.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class LocalDataRepository(private val context: Context) {

    // Carga el archivo JSON con los pilotos desde assets/pilots.json
    fun loadPilots(): List<Pilot> {
        val json = context.assets.open("pilots.json").bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Pilot>>() {}.type
        return Gson().fromJson(json, type)
    }

    // Devuelve un piloto aleatorio (puedes usarlo como el "piloto objetivo" del juego)
    fun getRandomPilot(pilots: List<Pilot>) = pilots.random()
}
