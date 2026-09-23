package com.danielearl.wslgrounds.data

data class Team(
    val name: String,
    val logoRes: Int,
    val stationCode: String,
    val stadiumLat: Double,
    val stadiumLon: Double,
    val stadiumName: String,
    val carInfo: String,
    val trainInfo: String,
    val drinkInfo: String,
    val fixturesUrl: String,
)
