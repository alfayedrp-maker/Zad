package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey
    val id: String, // e.g. "1:1", "2:255"
    val surahNumber: Int,
    val ayahNumber: Int,
    val surahNameArabic: String,
    val surahNameEnglish: String,
    val ayahText: String,
    val addedAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "tasbeeh_counters")
data class TasbeehEntity(
    @PrimaryKey
    val id: String,
    val arabicText: String,
    val translation: String,
    val count: Int,
    val target: Int,
    val totalDone: Int,
    val updatedAt: Long = System.currentTimeMillis()
)
