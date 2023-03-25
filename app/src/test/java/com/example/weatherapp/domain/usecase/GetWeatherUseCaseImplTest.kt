package com.example.weatherapp.domain.usecase

import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.data.model.Error
import com.example.weatherapp.domain.mapper.toWeatherModel
import com.example.weatherapp.domain.repository.WeatherRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class GetWeatherUseCaseImplTest {

    private lateinit var mockWeatherRepository: WeatherRepository
    private lateinit var getWeatherUseCase: GetWeatherUseCaseImpl

    @Before
    fun setup() {
        mockWeatherRepository = mockk()
        getWeatherUseCase = GetWeatherUseCaseImpl(mockWeatherRepository)
    }

    @Test
    fun `execute() should emit a successful result with WeatherModel when given a valid city`() =
        runBlocking {
            // Arrange
            val city = "London"
            val weatherEntity = WeatherEntity(
                id = 1,
                description = "clear sky",
                icon = "01d",
                temp = 10.0,
                tempMin = 8.0,
                tempMax = 12.0,
                humidity = 80,
                sunset = 1648210150,
                sunrise = 1648164141,
                updatedTime = System.currentTimeMillis()
            )
            val expectedWeatherModel = weatherEntity.toWeatherModel()
            val mockWeatherRepository = mockk<WeatherRepository> {
                coEvery { getWeather(city) } returns Result.success(weatherEntity)
            }
            val getWeatherUseCase = GetWeatherUseCaseImpl(mockWeatherRepository)

            // Act
            val result = getWeatherUseCase.execute(city).first()

            // Assert
            assertTrue(result.isSuccess)
            assertEquals(expectedWeatherModel, result.getOrNull())
            coVerify(exactly = 1) { mockWeatherRepository.getWeather(city) }
        }

    @Test
    fun `execute() should emit a failure result with an error message when given an invalid city`() =
        runBlocking {
            // Arrange
            val city = "InvalidCity"
            val errorMessage = "City not found"
            val mockWeatherRepository = mockk<WeatherRepository> {
                coEvery { getWeather(city) } returns Result.failure(
                    Error.ServerError(
                        500,
                        errorMessage
                    )
                )
            }
            val getWeatherUseCase = GetWeatherUseCaseImpl(mockWeatherRepository)

            // Act
            val result = getWeatherUseCase.execute(city).first()

            // Assert
            assertTrue(result.isFailure)
            assertEquals(errorMessage, result.exceptionOrNull()?.message)
            coVerify(exactly = 1) { mockWeatherRepository.getWeather(city) }
        }
}