package com.example.tp1.Model.api
import retrofit2.http.GET
import retrofit2.http.Path
import java.util.UUID

interface GetDeckService {

    interface GetPaquetCartesService {
        @GET("deck/new/{nb}")
        suspend fun getDecksOfCards(@Path("nb") nb : Int): DeckOfCard

        @GET("deck/{deck_id}/draw/{nb}")
        suspend fun getACardFormADeck(
            @Path("deck_id") deckId: UUID,
            @Path("nb") nb : Int
        ): AnswerClass
    }


}