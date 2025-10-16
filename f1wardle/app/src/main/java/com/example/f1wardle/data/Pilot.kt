package com.example.f1wardle.data

data class Pilot(
    val name: String,
    val team: String,
    val number: Int,
    val age: Int,
    val debut: Int,
    val wins: Int,
    val flag: String,
    val pastTeams: List<String>
)
