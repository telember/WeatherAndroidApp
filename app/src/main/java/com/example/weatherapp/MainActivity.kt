package com.example.weatherapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.toColorInt
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.rememberAsyncImagePainter
import com.example.weatherapp.ui.common.State
import com.example.weatherapp.ui.theme.WeatherAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            WeatherAppTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    val viewModel = hiltViewModel<MainViewModel>()
                    WeatherScreen(viewModel, "Bangkok")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WeatherScreen(
    viewModel: MainViewModel,
    city: String
) {
    val weatherState by viewModel.weather.collectAsStateWithLifecycle(State.Loading)
    val isRefreshing = remember {  mutableStateOf(false) }
    val pullRefreshState = rememberPullRefreshState(isRefreshing.value, { viewModel.getWeather(city) })

    Column {
        // Add a top app bar with the city name as the title
        TopAppBar(
            title = { Text(text = city, fontSize = 28.sp) },
            backgroundColor = Color.LightGray,
            contentColor = Color.Black
        )

        Box{
            when (weatherState) {
                is State.Loading -> {
                    // Show a loading spinner
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        isRefreshing.value = false
                        CircularProgressIndicator(modifier = Modifier.size(50.dp))
                        Text("Loading")
                    }
                }
                is State.Success -> {
                    // Show the weather data
                    val weatherModel = (weatherState as State.Success).data
                    val simpleDateFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .pullRefresh(pullRefreshState)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "${weatherModel.temp}°", fontSize = 45.sp, fontWeight = FontWeight.Bold, color = Color("#660000".toColorInt()))
                        Image(
                            painter = rememberAsyncImagePainter(weatherModel.icon),
                            contentDescription = null,
                            modifier = Modifier.size(50.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = weatherModel.description, fontSize = 25.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "H: ${weatherModel.tempMax}° L: ${weatherModel.tempMin}°", fontSize = 18.sp)
                        Spacer(modifier = Modifier.height(16.dp))

                        TwoTexts(title = "Humidity", detail = "${weatherModel.humidity} %")
                        Spacer(modifier = Modifier.height(8.dp))

                        if(weatherModel.isNightTime) {
                            val sunriseDate = Date(weatherModel.sunrise)
                            val formattedSunrise = simpleDateFormat.format(sunriseDate)
                            TwoTexts(title = "Sunrise", detail = formattedSunrise)
                        } else {
                            val sunriseDate = Date(weatherModel.sunset)
                            val formattedSunrise = simpleDateFormat.format(sunriseDate)
                            TwoTexts(title = "Sunset", detail= formattedSunrise)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        val formattedSyncAt = simpleDateFormat.format(weatherModel.syncAt)
                        TwoTexts(title = "Synced At", detail = formattedSyncAt)
                    }
                }
                is State.Error -> {
                    // Show an error message with a retry button
                    val errorMessage = (weatherState as State.Error).message
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxSize()
                            .pullRefresh(pullRefreshState)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_connection_failed),
                            contentDescription = "Error Image",
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = "Error", fontSize = 28.sp, color = Color.Black)
                        Text(text = errorMessage, fontSize = 18.sp, color = Color.Black)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { viewModel.getWeather(city) }) {
                            Text("Retry")
                        }
                    }
                }
            }
            PullRefreshIndicator(isRefreshing.value, pullRefreshState, Modifier.align(Alignment.TopCenter))
        }

    }

    // Fetch weather data when the screen is first shown or when the city changes
    LaunchedEffect(city) {
        viewModel.getWeather(city)
    }
}

@Composable
fun TwoTexts(modifier: Modifier = Modifier, title: String, detail: String) {
    Row(modifier = modifier) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .wrapContentWidth(Alignment.Start),
            fontWeight = FontWeight.Bold,
            text = title
        )
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .wrapContentWidth(Alignment.End),

            text = detail
        )
    }
}


