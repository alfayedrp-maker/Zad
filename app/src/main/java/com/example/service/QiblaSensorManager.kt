package com.example.service

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.roundToInt

class QiblaSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
    private val accelerometer = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val magnetometer = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
    private val rotationVectorSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private val _azimuthHeading = MutableStateFlow(0f)
    val azimuthHeading: StateFlow<Float> = _azimuthHeading.asStateFlow()

    private val _isSensorAvailable = MutableStateFlow(true)
    val isSensorAvailable: StateFlow<Boolean> = _isSensorAvailable.asStateFlow()

    private val gravity = FloatArray(3)
    private val geomagnetic = FloatArray(3)
    private var hasGravity = false
    private var hasGeomagnetic = false

    private val rMatrix = FloatArray(9)
    private val iMatrix = FloatArray(9)
    private val orientation = FloatArray(3)

    private var smoothedHeading = 0f
    private val alpha = 0.15f // Low-pass filter smoothing coefficient

    fun startListening() {
        val hasSensor = rotationVectorSensor != null || (accelerometer != null && magnetometer != null)
        _isSensorAvailable.value = hasSensor

        if (rotationVectorSensor != null) {
            sensorManager?.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelerometer?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            magnetometer?.let { sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMatrix, event.values)
            SensorManager.getOrientation(rMatrix, orientation)
            val azimuthRad = orientation[0]
            var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
            if (azimuthDeg < 0) azimuthDeg += 360f

            applySmoothedHeading(azimuthDeg)
        } else {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, gravity, 0, 3)
                hasGravity = true
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, geomagnetic, 0, 3)
                hasGeomagnetic = true
            }

            if (hasGravity && hasGeomagnetic) {
                val success = SensorManager.getRotationMatrix(rMatrix, iMatrix, gravity, geomagnetic)
                if (success) {
                    SensorManager.getOrientation(rMatrix, orientation)
                    val azimuthRad = orientation[0]
                    var azimuthDeg = Math.toDegrees(azimuthRad.toDouble()).toFloat()
                    if (azimuthDeg < 0) azimuthDeg += 360f

                    applySmoothedHeading(azimuthDeg)
                }
            }
        }
    }

    private fun applySmoothedHeading(rawHeading: Float) {
        // Handle wrap-around difference between 0 and 360
        var diff = rawHeading - smoothedHeading
        if (diff > 180f) diff -= 360f
        if (diff < -180f) diff += 360f

        smoothedHeading = (smoothedHeading + alpha * diff + 360f) % 360f
        _azimuthHeading.value = (smoothedHeading * 10).roundToInt() / 10f
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}
