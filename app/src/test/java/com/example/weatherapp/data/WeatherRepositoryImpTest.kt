package com.example.weatherapp.data

import com.example.weatherapp.BuildConfig
import com.example.weatherapp.data.api.model.Clouds
import com.example.weatherapp.data.api.model.Coord
import com.example.weatherapp.data.api.model.Main
import com.example.weatherapp.data.api.model.Sys
import com.example.weatherapp.data.api.model.Weather
import com.example.weatherapp.data.api.model.WeatherResponse
import com.example.weatherapp.data.api.model.Wind
import com.example.weatherapp.data.api.service.WeatherService
import com.example.weatherapp.data.common.NetworkStateChecker
import com.example.weatherapp.data.db.WeatherDAO
import com.example.weatherapp.data.db.WeatherDatabase
import com.example.weatherapp.data.db.WeatherEntity
import com.example.weatherapp.data.db.toDao
import com.example.weatherapp.data.model.Error
import com.example.weatherapp.data.repository.WeatherRepositoryImp
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response


class WeatherRepositoryImpTest {

    private lateinit var weatherService: WeatherService
    private lateinit var weatherDatabase: WeatherDatabase
    private lateinit var weatherDAO: WeatherDAO
    private lateinit var networkStateChecker: NetworkStateChecker
    private lateinit var weatherRepository: WeatherRepositoryImp

    @Before
    fun setUp() {
        weatherService = mockk()
        weatherDatabase = mockk()
        weatherDAO = mockk()
        networkStateChecker = mockk()

        every { weatherDatabase.weatherDAO() } returns weatherDAO

        weatherRepository = WeatherRepositoryImp(weatherService, weatherDatabase, networkStateChecker)
    }

    @Test
    fun `test getWeather with network available`() = runBlocking {
        val city = "TestCity"
        val weatherResponse = mockWeatherResponse
        val weatherEntity = weatherResponse.toDao()

        every { networkStateChecker.isNetworkAvailable() } returns true
        coEvery { weatherService.getWeather(city, BuildConfig.API_KEY) } returns Response.success(weatherResponse)
        coEvery { weatherDAO.insert(any()) } returns Unit
        coEvery { weatherDAO.getData() } returns weatherEntity

        val result = weatherRepository.getWeather(city)

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { weatherService.getWeather(city, BuildConfig.API_KEY) }
        coVerify(exactly = 1) { weatherDAO.insert(any()) }
        assertEquals(weatherEntity, result.getOrNull())
    }

    @Test
    fun `test getWeather with network unavailable`() = runBlocking {
        val city = "TestCity"
        val weatherEntity = mockWeatherEntity

        every { networkStateChecker.isNetworkAvailable() } returns false
        coEvery { weatherDAO.getData() } returns weatherEntity

        val result = weatherRepository.getWeather(city)

        assertTrue(result.isSuccess)
        assertEquals(weatherEntity, result.getOrNull())
    }

    @Test
    fun `getWeather should return network error when network is not available and no data in database`() { runBlocking {
            val city = "TestCity"

            coEvery { networkStateChecker.isNetworkAvailable() } returns false
            coEvery { weatherDatabase.weatherDAO().getData() } returns null

            val result = weatherRepository.getWeather(city)

            coVerify(exactly = 1) { weatherDatabase.weatherDAO().getData() }
            assertTrue(result.isFailure)

            val error = result.exceptionOrNull()
            assertTrue(error is Error.NetworkError)
        }
    }
}

val mockWeatherResponse = WeatherResponse(
    base = "stations",
    clouds = Clouds(all = 20),
    cod = 200,
    coord = Coord(lat = 37.7749, lon = -122.4194),
    dt = 1632529200,
    id = 123456,
    main = Main(
        feelsLike = 12.0,
        humidity = 87,
        temp = 12.5,
        tempMax = 14.0,
        tempMin = 11.0
    ),
    name = "San Francisco",
    sys = Sys(
        country = "US",
        id = 1,
        sunrise = 1632494285,
        sunset = 1632537986,
        type = 1
    ),
    timezone = -25200,
    visibility = 10000,
    weather = listOf(
        Weather(
            description = "few clouds",
            icon = "02d",
            id = 801,
            main = "Clouds"
        )
    ),
    wind = Wind(deg = 270, speed = 3.09)
)

val mockWeatherEntity = WeatherEntity(
    id = 123456,
    description = "few clouds",
    icon = "02d",
    temp = 12.5,
    tempMin = 11.0,
    tempMax = 14.0,
    humidity = 87,
    sunrise = 1632494285,
    sunset = 1632537986,
    updatedTime = System.currentTimeMillis()
)
