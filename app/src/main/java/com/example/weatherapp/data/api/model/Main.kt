package com.example.weatherapp.data.api.model


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Main(
    @SerialName("feels_like")
    val feelsLike: Double,
    @SerialName("humidity")
    val humidity: Int,
    @SerialName("temp")
    val temp: Double,
    @SerialName("temp_max")
    val tempMax: Double,
    @SerialName("temp_min")
    val tempMin: Double
)