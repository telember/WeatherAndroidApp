package com.example.weatherapp.utils

fun isNightTime(sunset: Long, sunrise: Long, currentTime: Long): Boolean{
    return currentTime < sunrise || currentTime >= sunset
}