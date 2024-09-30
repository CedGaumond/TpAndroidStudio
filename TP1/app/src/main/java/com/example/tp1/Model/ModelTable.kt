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
    CONTINUING
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

    private var isDeckFetched = false

    init {
        fetchDeckOfCard()
    }

    private fun fetchDeckOfCard() {
        if (isDeckFetched) return

        viewModelScope.launch {
            try {
                val deck = repository.getPaquetCartes(6)
                _deckOfCard.value = deck
                isDeckFetched = true
                Log.d("Carte Restante", "Carte Restante : ${_deckOfCard.value?.cardLeft}")
            } catch (e: Exception) {

            }
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
        }
    }

    private suspend fun fetchCardForPlayer() {
        val card = fetchCard() ?: return
        _cardsPlayer.value += card
        Log.d(
            "ModelTable",
            "Player fetched card: ${card.codeCarte}. Current cards: ${_cardsPlayer.value.joinToString(", ") { it.codeCarte }}. Player's score: ${calculateScore(_cardsPlayer.value)}. Card image URL: https://420c56.drynish.synology.me${card.imageUrl}"
        )


        if (calculateScore(_cardsPlayer.value) > 21) {
            Log.d("ModelTable", "Player has busted!")
            _gameState.value = GameState.GAME_OVER
            determineWinner()
        }
    }

    private suspend fun fetchCardForDealer() {
        val card = fetchCard() ?: return
        _cardsDealer.value += card
        Log.d("ModelTable", "Dealer fetched card: ${card.codeCarte}. Current cards: ${_cardsDealer.value.joinToString(", ") { it.codeCarte }} dealer score: ${calculateScore(_cardsDealer.value)}")
    }

    private suspend fun fetchCard(): Card? {
        return try {
            val deckId = _deckOfCard.value?.deckId ?: return null
            val response = repository.getCartes(deckId, 1)
            response.cartes.firstOrNull()
        } catch (e: Exception) {
            Log.e("ModelTable", "Error fetching card: ${e.message}")
            null
        }
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

    private fun calculateScore(cards: List<Card>): Int {
        var score = 0
        var aces = 0

        for (card in cards) {
            val cardValue = card.valeur
            score += when (cardValue) {
                "1" -> { aces++; 1 }
                "11", "12", "13" -> 10
                else -> cardValue.toIntOrNull() ?: 0
            }
        }

        while (score > 21 && aces > 0) {
            score -= 10
            aces--
        }

        return score
    }

    private fun determineWinner() {
        val playerScore = calculateScore(_cardsPlayer.value)
        val dealerScore = calculateScore(_cardsDealer.value)

        when {
            playerScore > 21 -> Log.d("Game Result", "Player Busts! Dealer Wins!")
            dealerScore > 21 -> Log.d("Game Result", "Dealer Busts! Player Wins!")
            playerScore > dealerScore -> Log.d("Game Result", "Player Wins!")
            dealerScore > playerScore -> Log.d("Game Result", "Dealer Wins!")
            else -> Log.d("Game Result", "It's a Tie!")
        }
    }
}

