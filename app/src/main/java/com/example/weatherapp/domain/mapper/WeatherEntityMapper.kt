package com.example.weatherapp.domain.mapper

import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.utils.isNightTime

fun WeatherEntity.toWeatherModel(): WeatherModel {
    return WeatherModel(
        description = description,
        icon = "https://openweathermap.org/img/wn/$icon@2x.png",
        temp = temp,
        tempMin = tempMin,
        tempMax = tempMax,
        humidity = humidity,
        isNightTime = isNightTime(sunset, sunrise, System.currentTimeMillis()),
        sunset = sunset,
        sunrise = sunrise,
        syncAt = updatedTime
    )
}