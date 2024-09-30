package com.example.tp1.Model.api

import com.google.gson.annotations.SerializedName
import java.util.UUID

data class DeckOfCard(

    @SerializedName("deck_id")
    val deckId : UUID,

    @SerializedName("remaining")
    val cardLeft : Int

)
