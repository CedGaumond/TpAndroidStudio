package com.example.tp1.Model.api

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "cards")
data class Card(
    @PrimaryKey(autoGenerate = true) val id: Long = 0, // Unique identifier for the table
    @SerializedName("code") val code: String,
    @SerializedName("image") val imageUrl: String,
    @SerializedName("rank") val value: String,
    @SerializedName("suit") val signe: String
)
