package com.example.weatherapp.data.api.service

import com.example.weatherapp.data.api.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("weather?units=metric")
    suspend fun getWeather(@Query("q")city:String, @Query("appid")appId:String): Response<WeatherResponse>
}