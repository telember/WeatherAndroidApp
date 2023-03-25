package com.example.weatherapp.ui.common

sealed class State<out T> {
    object Loading : State<Nothing>()
    data class Success<T>(val data: T) : State<T>()
    data class Error(val message: String) : State<Nothing>()
}