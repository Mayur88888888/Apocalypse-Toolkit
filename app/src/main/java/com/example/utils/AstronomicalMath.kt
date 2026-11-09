package com.example.utils

import java.util.Calendar
import kotlin.math.*

object AstronomicalMath {
    /**
     * Approximates Sunrise and Sunset for given lat, long, and day of year.
     */
    fun calculateSunriseSunset(lat: Double, lng: Double, calendar: Calendar = Calendar.getInstance()): Pair<String, String> {
        val dayOfYear = calendar.get(Calendar.DAY_OF_YEAR)
        // Approximate solar declination
        val declination = 23.45 * sin(Math.toRadians(360.0 / 365.0 * (dayOfYear - 81)))
        
        val latRad = Math.toRadians(lat)
        val decRad = Math.toRadians(declination)
        
        val cosHourAngle = -tan(latRad) * tan(decRad)
        val hourAngle = Math.toDegrees(acos(cosHourAngle.coerceIn(-1.0, 1.0)))
        
        val solarNoonUtc = 12.0 - (lng / 15.0)
        val sunriseUtc = solarNoonUtc - (hourAngle / 15.0)
        val sunsetUtc = solarNoonUtc + (hourAngle / 15.0)
        
        val tzOffset = calendar.timeZone.rawOffset / (1000.0 * 3600.0)
        val localSunrise = (sunriseUtc + tzOffset + 24) % 24
        val localSunset = (sunsetUtc + tzOffset + 24) % 24
        
        return Pair(formatDecimalHours(localSunrise), formatDecimalHours(localSunset))
    }

    private fun formatDecimalHours(hours: Double): String {
        val h = hours.toInt()
        val m = ((hours - h) * 60).toInt()
        return String.format("%02d:%02d", (h + 24) % 24, abs(m))
    }

    /**
     * Calculates current Moon Phase (0.0 = New Moon, 0.25 = First Qtr, 0.5 = Full Moon, 0.75 = Last Qtr)
     */
    fun getMoonPhase(calendar: Calendar = Calendar.getInstance()): MoonPhaseInfo {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        var c = 0
        var e = 0
        var jd = 0.0
        var b = 0.0

        if (month < 3) {
            val y = year - 1
            val m = month + 12
            jd = (365.25 * y).toInt() + (30.6001 * (m + 1)).toInt() + day + 1720994.5
        } else {
            jd = (365.25 * year).toInt() + (30.6001 * (month + 1)).toInt() + day + 1720994.5
        }

        val daysSinceNew = (jd - 2451549.5) % 29.53058867
        val phaseFraction = (daysSinceNew / 29.53058867)

        val name = when {
            phaseFraction < 0.03 || phaseFraction > 0.97 -> "New Moon"
            phaseFraction < 0.22 -> "Waxing Crescent"
            phaseFraction < 0.28 -> "First Quarter"
            phaseFraction < 0.47 -> "Waxing Gibbous"
            phaseFraction < 0.53 -> "Full Moon"
            phaseFraction < 0.72 -> "Waning Gibbous"
            phaseFraction < 0.78 -> "Last Quarter"
            else -> "Waning Crescent"
        }

        val illumination = (0.5 - 0.5 * cos(2.0 * Math.PI * phaseFraction)) * 100

        return MoonPhaseInfo(name, phaseFraction.toFloat(), illumination.toInt())
    }

    data class MoonPhaseInfo(
        val name: String,
        val phaseFraction: Float,
        val illuminationPercent: Int
    )
}
