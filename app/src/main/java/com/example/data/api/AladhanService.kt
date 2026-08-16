package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class AladhanTimingsResponse(
    val code: Int?,
    val status: String?,
    val data: AladhanData?
)

@JsonClass(generateAdapter = true)
data class AladhanData(
    val timings: Map<String, String>?,
    val date: AladhanDateInfo?,
    val meta: AladhanMeta?
)

@JsonClass(generateAdapter = true)
data class AladhanDateInfo(
    val readable: String?,
    val timestamp: String?,
    val hijri: AladhanHijri?
)

@JsonClass(generateAdapter = true)
data class AladhanHijri(
    val date: String?,
    val day: String?,
    val month: AladhanHijriMonth?,
    val year: String?
)

@JsonClass(generateAdapter = true)
data class AladhanHijriMonth(
    val number: Int?,
    val en: String?,
    val ar: String?
)

@JsonClass(generateAdapter = true)
data class AladhanMeta(
    val latitude: Double?,
    val longitude: Double?,
    val timezone: String?
)

interface AladhanApi {
    @GET("v1/timings")
    suspend fun getTimings(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int
    ): AladhanTimingsResponse

    companion object {
        private const val BASE_URL = "https://api.aladhan.com/"

        fun create(): AladhanApi {
            val client = OkHttpClient.Builder()
                .connectTimeout(5, TimeUnit.SECONDS)
                .readTimeout(5, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()
                .create(AladhanApi::class.java)
        }
    }
}
