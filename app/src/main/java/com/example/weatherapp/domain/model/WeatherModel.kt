package com.example.weatherapp.domain.model

data class WeatherModel(
    val description: String,
    val icon: String,
    val temp: Double,
    val tempMin: Double,
    val tempMax: Double,
    val humidity: Int,
    val isNightTime: Boolean,
    val sunset: Long,
    val sunrise: Long,
    val syncAt: Long)