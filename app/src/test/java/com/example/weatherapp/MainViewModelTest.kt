package com.example.weatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.weatherapp.data.model.Error
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.domain.usecase.GetWeatherUseCase
import com.example.weatherapp.ui.common.State
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
@FlowPreview
class MainViewModelTest {

    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val dispatcher: TestDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(dispatcher)
    private val mockGetWeatherUseCase = mockk<GetWeatherUseCase>()
    private lateinit var viewModel: MainViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = MainViewModel(mockGetWeatherUseCase)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getWeather should update weather state with success result`() = testScope.runTest {
        // Arrange
        val city = "London"
        val weatherModel = mockk<WeatherModel>()
        val flow = flow {
            emit(Result.success(weatherModel))
        }
        every { mockGetWeatherUseCase.execute(city) } returns flow

        // Assert check Loading
        assertEquals(State.Loading, viewModel.weather.value)

        // Act
        viewModel.getWeather(city)

        // Assert
        assertEquals(State.Success(weatherModel), viewModel.weather.value)

    }

    @Test
    fun `getWeather should update weather state with error result`() = testScope.runTest {
        // Arrange
        val city = "Paris"
        val errorMessage = "An error occurred"
        val flow = flow {
            emit(Result.failure<WeatherModel>(Exception(errorMessage)))
        }
        every { mockGetWeatherUseCase.execute(city) } returns flow

        // Assert check Loading
        assertEquals(State.Loading, viewModel.weather.value)

        // Act
        viewModel.getWeather(city)

        // Assert
        assertEquals(State.Error(errorMessage), viewModel.weather.value)
    }

    @Test
    fun `getWeather should update weather state with error result with no connection`() = testScope.runTest {
        // Arrange
        val city = "Paris"
        val errorMessage = "Cannot reach the server"
        val flow = flow {
            emit(Result.failure<WeatherModel>(Error.NetworkError))
        }
        every { mockGetWeatherUseCase.execute(city) } returns flow

        // Assert check Loading
        assertEquals(State.Loading, viewModel.weather.value)

        // Act
        viewModel.getWeather(city)

        // Assert
        assertEquals(State.Error(errorMessage), viewModel.weather.value)
    }

    @Test
    fun `getWeather should update weather state with error result with server error`() = testScope.runTest {
        // Arrange
        val city = "Paris"
        val errorMessage = "Internal Server Error"
        val errorCode = 500
        val flow = flow {
            emit(Result.failure<WeatherModel>(Error.ServerError(errorCode, errorMessage)))
        }
        every { mockGetWeatherUseCase.execute(city) } returns flow

        // Assert check Loading
        assertEquals(State.Loading, viewModel.weather.value)

        // Act
        viewModel.getWeather(city)

        // Assert
        assertEquals(State.Error("$errorMessage with $errorCode"), viewModel.weather.value)
    }
}