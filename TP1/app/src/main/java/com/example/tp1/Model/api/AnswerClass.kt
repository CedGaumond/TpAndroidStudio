package com.example.tp1.Model.api

import com.google.gson.annotations.SerializedName

data class AnswerClass(
    @SerializedName("deck_id")
    val deckId: String,

    @SerializedName("cards")
    val cartes: List<Card>,

    @SerializedName("remaining")
    val cartesRestantes: Int
)
