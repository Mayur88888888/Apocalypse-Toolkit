package com.example.sensors

import android.content.Context
import android.hardware.*
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.BatteryManager
import android.os.Bundle
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.roundToInt

class SensorHelper(private val context: Context) : SensorEventListener {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

    // Compass
    private val _compassAzimuth = MutableStateFlow(0f)
    val compassAzimuth: StateFlow<Float> = _compassAzimuth

    // Accelerometer / Level
    private val _pitch = MutableStateFlow(0f)
    val pitch: StateFlow<Float> = _pitch
    private val _roll = MutableStateFlow(0f)
    val roll: StateFlow<Float> = _roll

    // Barometer & Altitude
    private val _pressure = MutableStateFlow<Float?>(null)
    val pressure: StateFlow<Float?> = _pressure

    // Temp & Humidity
    private val _ambientTemp = MutableStateFlow<Float?>(null)
    val ambientTemp: StateFlow<Float?> = _ambientTemp
    private val _humidity = MutableStateFlow<Float?>(null)
    val humidity: StateFlow<Float?> = _humidity

    // Step counter
    private val _stepCount = MutableStateFlow(0)
    val stepCount: StateFlow<Int> = _stepCount

    // GPS Location
    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)

    fun startSensors() {
        try {
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
            }
            sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
            sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
            sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }
            sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)?.let {
                sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_NORMAL)
            }

            // Start GPS updates safely
            try {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        1000L,
                        1f,
                        locationListener
                    )
                }
                if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    locationManager.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        2000L,
                        5f,
                        locationListener
                    )
                }
            } catch (e: SecurityException) {
                // Permission handled by UI
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopSensors() {
        try {
            sensorManager.unregisterListener(this)
            locationManager.removeUpdates(locationListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            _currentLocation.value = location
        }
        @Deprecated("Deprecated in Java")
        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                System.arraycopy(event.values, 0, gravity, 0, 3)
                val ax = event.values[0]
                val ay = event.values[1]
                val az = event.values[2]
                _pitch.value = Math.toDegrees(atan2(ay.toDouble(), az.toDouble())).toFloat()
                _roll.value = Math.toDegrees(atan2(-ax.toDouble(), Math.sqrt((ay * ay + az * az).toDouble()))).toFloat()
                updateOrientation()
            }
            Sensor.TYPE_MAGNETIC_FIELD -> {
                System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                updateOrientation()
            }
            Sensor.TYPE_PRESSURE -> {
                _pressure.value = event.values[0]
            }
            Sensor.TYPE_AMBIENT_TEMPERATURE -> {
                _ambientTemp.value = event.values[0]
            }
            Sensor.TYPE_RELATIVE_HUMIDITY -> {
                _humidity.value = event.values[0]
            }
            Sensor.TYPE_STEP_COUNTER -> {
                _stepCount.value = event.values[0].toInt()
            }
        }
    }

    private fun updateOrientation() {
        val r = FloatArray(9)
        val i = FloatArray(9)
        if (SensorManager.getRotationMatrix(r, i, gravity, geomagnetic)) {
            val orientation = FloatArray(3)
            SensorManager.getOrientation(r, orientation)
            var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
            if (azimuth < 0) azimuth += 360f
            _compassAzimuth.value = azimuth
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    fun getBatteryInfo(): BatteryStats {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val currentNow = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CURRENT_NOW) // uA
        val isCharging = bm.isCharging
        return BatteryStats(
            levelPercent = if (level >= 0) level else 50,
            isCharging = isCharging,
            currentMicroAmps = abs(currentNow)
        )
    }

    data class BatteryStats(
        val levelPercent: Int,
        val isCharging: Boolean,
        val currentMicroAmps: Int
    )
}
