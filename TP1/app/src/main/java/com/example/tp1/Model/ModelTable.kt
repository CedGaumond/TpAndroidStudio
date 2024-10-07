package com.example.tp1.Model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tp1.Model.api.Card
import com.example.tp1.Model.api.DeckOfCard
import com.example.tp1.Model.repository.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

enum class GameState {
    NOT_STARTED,
    PLAYER_TURN,
    DEALER_TURN,
    GAME_OVER,
}

class ModelTable : ViewModel() {
    private val repository = ApiRepository()
    private val _deckOfCard = MutableStateFlow<DeckOfCard?>(null)
    private val _cardsPlayer = MutableStateFlow<List<Card>>(emptyList())
    val cardsPlayer: StateFlow<List<Card>> get() = _cardsPlayer

    private val _cardsDealer = MutableStateFlow<List<Card>>(emptyList())
    val cardsDealer: StateFlow<List<Card>> get() = _cardsDealer

    private val _gameState = MutableStateFlow(GameState.NOT_STARTED)
    val gameState: StateFlow<GameState> get() = _gameState

    private val _winner = MutableStateFlow<String?>(null)
    val winner: StateFlow<String?> get() = _winner

    private var isDeckFetched = false

    init {
        fetchDeckOfCard()
    }

    private fun fetchDeckOfCard() {
        if (isDeckFetched) return

        viewModelScope.launch {
            try {
                val deck = repository.getPaquetCartes(1)
                _deckOfCard.value = deck
                isDeckFetched = true
                Log.d("Deck", "Deck fetched: ${deck.cardLeft} cards left.")
            } catch (e: Exception) {
                Log.e("Deck", "Error fetching deck: ${e.message}")
            }
        }
    }

    private suspend fun checkAndFetchNewDeckIfNeeded() {

        val deck = _deckOfCard.value
        if (deck == null || deck.cardLeft <= 1) {
            Log.d("ModelTable", "Deck is empty or not fetched, fetching a new deck.")
            isDeckFetched = false
            fetchDeckOfCard()
        }
    }

    private suspend fun fetchCard(): Card? {
        Log.e("ModelTable", "nbcards left ${_deckOfCard.value?.cardLeft}")

        // Check if we need to fetch a new deck
        checkAndFetchNewDeckIfNeeded()

        val deck = _deckOfCard.value ?: return null // Ensure deck is not null
        val deckId = deck.deckId // Get the UUID from the current deck

        return try {
            // Fetch a card from the deck
            val response = repository.getCartes(deckId, 1) // Draw a card from the deck

            if (response.cartes.isNullOrEmpty()) {
                Log.e("ModelTable", "No cards returned in the response.")
                return null
            }


            _deckOfCard.value = DeckOfCard(deckId, response.cartesRestantes)

            response.cartes.firstOrNull()
        } catch (e: Exception) {
            Log.e("ModelTable", "Error fetching card: ${e.message}")
            null
        }
    }






    fun startGame() {
        if (_deckOfCard.value == null) {
            Log.e("ModelTable", "Deck not fetched. Cannot start the game.")
            return
        }
        resetGame()
        _gameState.value = GameState.PLAYER_TURN
        dealInitialCards()
    }

    fun resetGame() {
        _winner.value = null
        _cardsPlayer.value = emptyList()
        _cardsDealer.value = emptyList()
        _gameState.value = GameState.NOT_STARTED
    }

    private fun dealInitialCards() {
        viewModelScope.launch {
            repeat(2) {
                fetchCardForPlayer()
                fetchCardForDealer()
            }

            if (_cardsPlayer.value.isNotEmpty() && _cardsDealer.value.isNotEmpty()) {
                _gameState.value = GameState.PLAYER_TURN
            } else {
                Log.e("ModelTable", "Failed to deal initial cards.")
            }
        }
    }

    private suspend fun fetchCardForPlayer() {
        val card = fetchCard() ?: return
        _cardsPlayer.value += card
        Log.d("ModelTable", "Player fetched card: ${card.codeCarte}. Current cards: ${_cardsPlayer.value.joinToString(", ") { it.codeCarte }}")

        if (calculateScore(_cardsPlayer.value) > 21) {
            Log.d("ModelTable", "Player has busted!")
            _gameState.value = GameState.GAME_OVER
            determineWinner()
        }
    }

    private suspend fun fetchCardForDealer() {
        val card = fetchCard() ?: return
        _cardsDealer.value += card
        Log.d("ModelTable", "Dealer fetched card: ${card.codeCarte}. Current cards: ${_cardsDealer.value.joinToString(", ") { it.codeCarte }}")
    }

    fun playerHit() {
        if (_gameState.value == GameState.PLAYER_TURN) {
            viewModelScope.launch {
                fetchCardForPlayer()
            }
        }
    }

    fun playerStand() {
        if (_gameState.value != GameState.PLAYER_TURN) {
            Log.e("ModelTable", "Invalid game state: ${_gameState.value}. Cannot stand.")
            return
        }

        Log.d("ModelTable", "Player stands. Transitioning to dealer's turn.")
        _gameState.value = GameState.DEALER_TURN
        viewModelScope.launch {
            dealerPlay()
        }
    }

    private suspend fun dealerPlay() {
        Log.d("ModelTable", "Dealer's turn starts. Current cards: ${_cardsDealer.value.joinToString(", ") { it.codeCarte }}")

        while (calculateScore(_cardsDealer.value) <= 17) {
            fetchCardForDealer()
            Log.d("ModelTable", "Dealer draws a card. Nb cartes: ${_cardsDealer.value.size}")
        }

        _gameState.value = GameState.GAME_OVER
        determineWinner()
    }

    fun calculateScore(cards: List<Card>): Int {
        var score = 0
        var aces = 0

        for (card in cards) {
            val cardValue = card.valeur
            score += when (cardValue) {
                "1" -> { aces++; 1 } // Ace as 1 initially
                "11", "12", "13" -> 10 // Face cards are worth 10
                else -> cardValue.toIntOrNull() ?: 0
            }
        }

        // Adjust for Aces: convert Aces from 1 to 11 where possible
        for (i in 0 until aces) {
            if (score + 10 <= 21) { // If adding 10 keeps us under 21, count Ace as 11
                score += 10
            }
        }

        return score
    }

    private fun determineWinner() {
        val playerScore = calculateScore(_cardsPlayer.value)
        val dealerScore = calculateScore(_cardsDealer.value)

        when {
            playerScore > 21 -> _winner.value = "Dealer"
            dealerScore > 21 -> _winner.value = "Player"
            playerScore > dealerScore -> _winner.value = "Player"
            dealerScore > playerScore -> _winner.value = "Dealer"
            else -> _winner.value = "Tie"
        }
    }
}
