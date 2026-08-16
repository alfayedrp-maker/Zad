package com.example.service

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.util.Log
import com.example.data.prayer.CityPreset
import com.example.data.prayer.WORLD_CITIES
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.util.Locale
import kotlin.coroutines.resume

data class LocationResult(
    val cityName: String,
    val countryName: String,
    val latitude: Double,
    val longitude: Double,
    val isGps: Boolean
)

class LocationHelper(private val context: Context) {

    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    suspend fun getCurrentGpsLocation(): LocationResult? = withContext(Dispatchers.IO) {
        try {
            val location: Location? = suspendCancellableCoroutine { continuation ->
                val cts = CancellationTokenSource()
                fusedLocationClient.getCurrentLocation(
                    Priority.PRIORITY_BALANCED_POWER_ACCURACY,
                    cts.token
                ).addOnSuccessListener { loc ->
                    continuation.resume(loc)
                }.addOnFailureListener {
                    continuation.resume(null)
                }

                continuation.invokeOnCancellation {
                    cts.cancel()
                }
            }

            if (location != null) {
                val (city, country) = getAddressFromCoordinates(location.latitude, location.longitude)
                return@withContext LocationResult(
                    cityName = city.ifEmpty { "GPS Location" },
                    countryName = country,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    isGps = true
                )
            }
        } catch (e: Exception) {
            Log.e("LocationHelper", "Failed to get GPS location", e)
        }
        return@withContext null
    }

    private fun getAddressFromCoordinates(lat: Double, lon: Double): Pair<String, String> {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (!list.isNullOrEmpty()) {
                val addr = list[0]
                val city = addr.locality ?: addr.subAdminArea ?: addr.adminArea ?: ""
                val country = addr.countryName ?: ""
                return Pair(city, country)
            }
        } catch (e: Exception) {
            Log.e("LocationHelper", "Geocoder failed", e)
        }
        return Pair("", "")
    }

    fun getDefaultCity(): CityPreset {
        // Find best match according to device country, or default to Makkah
        val deviceCountry = Locale.getDefault().country.uppercase()
        return when (deviceCountry) {
            "SA" -> WORLD_CITIES.first { it.nameEn == "Makkah" }
            "EG" -> WORLD_CITIES.first { it.nameEn == "Cairo" }
            "AE" -> WORLD_CITIES.first { it.nameEn == "Dubai" }
            "TR" -> WORLD_CITIES.first { it.nameEn == "Istanbul" }
            "ID" -> WORLD_CITIES.first { it.nameEn == "Jakarta" }
            "PK" -> WORLD_CITIES.first { it.nameEn == "Karachi" }
            "GB", "UK" -> WORLD_CITIES.first { it.nameEn == "London" }
            "FR" -> WORLD_CITIES.first { it.nameEn == "Paris" }
            "US" -> WORLD_CITIES.first { it.nameEn == "New York" }
            "CA" -> WORLD_CITIES.first { it.nameEn == "Toronto" }
            "ES" -> WORLD_CITIES.first { it.nameEn == "Madrid" }
            "IT" -> WORLD_CITIES.first { it.nameEn == "Rome" }
            else -> WORLD_CITIES.first { it.nameEn == "Cairo" }
        }
    }
}
