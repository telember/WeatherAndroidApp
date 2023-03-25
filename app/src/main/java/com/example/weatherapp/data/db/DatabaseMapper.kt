package com.example.weatherapp.data.db

import com.example.weatherapp.data.api.model.WeatherResponse

fun WeatherResponse.toDao(): WeatherEntity {
    return WeatherEntity(
        id = 1,
        humidity = main.humidity,
        temp = main.temp,
        tempMax = main.tempMax,
        tempMin = main.tempMin,
        updatedTime = System.currentTimeMillis(),
        icon = weather.firstOrNull()?.icon.orEmpty(),
        description = weather.firstOrNull()?.description.orEmpty(),
        sunrise = sys.sunrise * 1000, //converted from Unix time to milliseconds
        sunset = sys.sunset * 1000, //converted from Unix time to milliseconds
    )
}