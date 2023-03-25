package com.example.weatherapp.di

import com.example.weatherapp.domain.usecase.GetWeatherUseCase
import com.example.weatherapp.domain.usecase.GetWeatherUseCaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class UseCaseModule {

    @Binds
    @Singleton
    abstract fun bindWeatherUseCase(
        getWeatherUseCaseImpl: GetWeatherUseCaseImpl
    ): GetWeatherUseCase

}