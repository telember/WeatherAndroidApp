package com.example.weatherapp.domain.usecase

import com.example.weatherapp.domain.mapper.toWeatherModel
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.domain.repository.WeatherRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

interface GetWeatherUseCase {
    fun execute(city: String): Flow<Result<WeatherModel>>
}

class GetWeatherUseCaseImpl @Inject constructor(private val weatherRepository: WeatherRepository): GetWeatherUseCase {

    override fun execute(city: String)= flow {
        val result = weatherRepository.getWeather(city)
        if(result.isSuccess) {
            emit(Result.success(result.getOrThrow().toWeatherModel()))
        } else {
            emit(Result.failure(result.exceptionOrNull() ?: error("unknown")))
        }
    }
}