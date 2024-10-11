package com.example.tp1.Model.repository
import com.example.tp1.Model.api.DeckOfCard
import com.example.tp1.Model.api.AnswerClass
import com.example.tp1.Model.api.RetroFitInstance
import java.util.UUID

class ApiRepository {

        private val deckCardService = RetroFitInstance.getDeckService

        suspend fun getDeckOfCard(nb : Int): DeckOfCard {
            return deckCardService.getDecksOfCards(nb)
        }
        suspend fun getACard(deckId: UUID, nbCarteApiger: Int): AnswerClass {
            return deckCardService.getACardFormADeck(deckId,nbCarteApiger)
        }



}