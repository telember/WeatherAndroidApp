package com.example.weatherapp.data.model


sealed class Error : Throwable() {
    object NetworkError : Error()
    data class ServerError(val statusCode: Int, override val message: String) : Error()
    data class DatabaseError(override val message: String) : Error()
    data class UnknownError(override val message: String) : Error()
}