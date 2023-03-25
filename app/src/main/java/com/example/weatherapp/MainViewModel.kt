package com.example.weatherapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherapp.data.model.Error
import com.example.weatherapp.domain.model.WeatherModel
import com.example.weatherapp.domain.usecase.GetWeatherUseCase
import com.example.weatherapp.ui.common.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val getWeatherUseCase: GetWeatherUseCase) : ViewModel() {

    private val _weather = MutableStateFlow<State<WeatherModel>>(State.Loading)
    val weather: StateFlow<State<WeatherModel>>
        get() = _weather

    fun getWeather(city: String) {
        viewModelScope.launch {
            _weather.value = State.Loading
            getWeatherUseCase.execute(city).collect { result ->
                if (result.isSuccess) {
                    result.getOrNull()?.let {
                        _weather.value = State.Success(it)
                    }
                } else {
                   val message =  when(val error = result.exceptionOrNull()){
                       is Error.NetworkError -> "Cannot reach the server"
                       is Error.ServerError -> "${error.message} with ${error.statusCode}"
                       else -> error?.message ?: "An error occurred"
                   }
                    _weather.value = State.Error(message)
                }
            }
        }
    }
}