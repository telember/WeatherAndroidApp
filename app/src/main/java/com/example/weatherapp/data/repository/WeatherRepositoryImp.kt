package com.example.weatherapp.data.repository

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.api.model.WeatherResponse
import com.example.weatherapp.data.api.service.WeatherService
import com.example.weatherapp.data.common.NetworkStateChecker
import com.example.weatherapp.data.db.WeatherDatabase
import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.data.db.toDao
import com.example.weatherapp.data.model.Error
import com.example.weatherapp.domain.repository.WeatherRepository
import javax.inject.Inject

class WeatherRepositoryImp @Inject constructor(
    private val weatherService: WeatherService,
    private val weatherDatabase: WeatherDatabase,
    private val networkStateChecker: NetworkStateChecker
): WeatherRepository {

    override suspend fun getWeather(city: String): Result<WeatherEntity> {
        return if (networkStateChecker.isNetworkAvailable()) {
            val weatherResponse = executeSafeApiCall(city)
            if (weatherResponse.isSuccess) {
                weatherResponse.getOrNull()?.let {
                    weatherDatabase.weatherDAO().insert(it.toDao())
                }
            }
            getWeatherFromDatabaseOrError()
        } else {
            getWeatherFromDatabaseOrError(Error.NetworkError)
        }
    }

    private suspend fun getWeatherFromDatabaseOrError(error: Error? = null): Result<WeatherEntity> {
        val localData = weatherDatabase.weatherDAO().getData()
        return if (localData == null) {
            Result.failure(error ?: Error.DatabaseError("No data available"))
        } else {
            Result.success(localData)
        }
    }

    private suspend fun executeSafeApiCall(city: String): Result<WeatherResponse> {
        return try {
            val response = weatherService.getWeather(city, BuildConfig.API_KEY)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Error.ServerError(200,"Weather response body is null"))
            } else {
                Result.failure(Error.ServerError(response.code(), response.message()))
            }
        } catch (e: Exception) {
            Result.failure(Error.UnknownError(e.message.toString()))
        }
    }
}