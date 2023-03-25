package com.example.weatherapp.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "data")
class WeatherEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "icon")
    var icon: String,
    @ColumnInfo(name = "temp")
    var temp: Double,
    @ColumnInfo(name = "temp_min")
    var tempMin: Double,
    @ColumnInfo(name = "temp_max")
    var tempMax: Double,
    @ColumnInfo(name = "humidity")
    var humidity: Int,
    @ColumnInfo(name = "sunrise")
    var sunrise: Long,
    @ColumnInfo(name = "sunset")
    var sunset: Long,
    @ColumnInfo(name = "updatedTime")
    var updatedTime: Long
)