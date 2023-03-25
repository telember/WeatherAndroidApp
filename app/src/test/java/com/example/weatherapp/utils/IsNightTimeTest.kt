package com.example.weatherapp.utils

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class IsNightTimeTest {

    @Test
    fun `should return true when current time is outside sunrise and sunset`() {
        val sunset = 1616635200L // March 25, 2021 7:00 PM EST
        val sunrise = 1616597760L // March 25, 2021 6:02 AM EST
        val currentTime = 1616595000L // March 25, 2021 12:10 AM EST (before sunrise)

        val result = isNightTime(sunset, sunrise, currentTime)

        assertTrue(result)
    }

    @Test
    fun `should return false when current time is between sunrise and sunset`() {
        val sunset = 1616635200L // March 25, 2021 7:00 PM EST
        val sunrise = 1616597760L // March 25, 2021 6:02 AM EST
        val currentTime = 1616611380L // March 25, 2021 10:43 AM EST

        val result = isNightTime(sunset, sunrise, currentTime)

        assertFalse(result)
    }
}
