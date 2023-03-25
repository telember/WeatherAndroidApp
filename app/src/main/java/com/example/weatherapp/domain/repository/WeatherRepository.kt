package com.example.weatherapp.domain.repository

import com.example.weatherapp.data.db.WeatherEntity

interface WeatherRepository {
    suspend fun getWeather(city: String): Result<WeatherEntity>
}