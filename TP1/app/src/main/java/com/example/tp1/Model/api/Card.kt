package com.example.tp1.Model.api

import com.google.gson.annotations.SerializedName

data class Card(
    @SerializedName("code")
    val codeCarte: String,

    @SerializedName("image")
    val imageUrl: String,

    @SerializedName("rank")
    val valeur: String,

    @SerializedName("suit")
    val signe: String
)
