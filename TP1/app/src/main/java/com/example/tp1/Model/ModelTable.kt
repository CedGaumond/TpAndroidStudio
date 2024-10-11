package com.example.tp1.Model

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.hockey.db.CardsDB
import com.example.tp1.MainActivity

import com.example.tp1.Model.api.Card
import com.example.tp1.Model.api.DeckOfCard
import com.example.tp1.Model.repository.ApiRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class GameState {
    NOT_STARTED,
    PLAYER_TURN,
    DEALER_TURN,
    GAME_OVER,
}

class ModelTable : ViewModel() {
    private val repository = ApiRepository()
    private val _deckOfCard = MutableStateFlow<DeckOfCard?>(null)
    val deckOfCard: StateFlow<DeckOfCard?> get() = _deckOfCard

    private var CardsDAO = CardsDB
        .getInstance(
            MainActivity.getAppContext()
        )





    private val _cardOdds = MutableStateFlow<Map<String, Double>>(emptyMap())
    val cardOdds: StateFlow<Map<String, Double>> = _cardOdds

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
                val deck = repository.getPaquetCartes(7)
                _deckOfCard.value = deck
                isDeckFetched = true
                startGame()
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
        val deckId = deck.deckId

        return try {

            val response = repository.getCartes(deckId, 1) // Draw a card from the deck
            calculateCardOdds()
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
        Log.d("ModelTable", "Player fetched card: ${card.code}. Current cards: ${_cardsPlayer.value.joinToString(", ") { it.code }}")

        // Insert the card into the database
        insertCard(card)

        if (calculateScore(_cardsPlayer.value) > 21) {
            Log.d("ModelTable", "Player has busted!")
            _gameState.value = GameState.GAME_OVER
            determineWinner()
        }
    }

    private suspend fun fetchCardForDealer() {
        val card = fetchCard() ?: return
        _cardsDealer.value += card
        Log.d("ModelTable", "Dealer fetched card: ${card.code}. Current cards: ${_cardsDealer.value.joinToString(", ") { it.code }}")

        // Insert the card into the database
        insertCard(card)
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
        Log.d("ModelTable", "Dealer's turn starts. Current cards: ${_cardsDealer.value.joinToString(", ") { it.code }}")

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
            val cardValue = card.value
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
            playerScore > 21 -> {
                _winner.value = "Dealer"
                MoneyManager.updateBalance(-MoneyManager.totalBet.value)
            }
            dealerScore > 21 -> {
                _winner.value = "Player"
                MoneyManager.updateBalance(MoneyManager.totalBet.value)
            }
            playerScore > dealerScore -> {
                _winner.value = "Player"
                MoneyManager.updateBalance(MoneyManager.totalBet.value)
            }
            dealerScore > playerScore -> {
                _winner.value = "Dealer"
                MoneyManager.updateBalance(-MoneyManager.totalBet.value)
            }
            else -> {
                _winner.value = "Tie"
            }
        }
    }


    private suspend fun insertCard(card: Card) {
        withContext(Dispatchers.IO) {
            // Insert the card into the database
            CardsDAO.dao.insertCard(card) // Make sure `insertCard` is defined in your DAO
        }
    }

    private suspend fun fetchPlayedCards(): List<Card> {
        return withContext(Dispatchers.IO) {
            // Fetch played cards from your database
            CardsDAO.dao.getAllCards()
        }
    }


    fun calculateCardOdds() {
        viewModelScope.launch {
            val totalCards = 7 * 52 // Total number of cards in 7 decks
            val remainingCards = _deckOfCard.value?.cardLeft ?: totalCards

            // Initialize card counts with string keys
            val cardCounts = mutableMapOf(
                "1" to 28,  // Ace
                "2" to 28,
                "3" to 28,
                "4" to 28,
                "5" to 28,
                "6" to 28,
                "7" to 28,
                "8" to 28,
                "9" to 28,
                "10" to 112, // Combine counts for "10", "JACK", "QUEEN", "KING"
                "11" to 0,   // JACK
                "12" to 0,   // QUEEN
                "13" to 0    // KING
            )

            val playedCards: List<Card> = fetchPlayedCards()

            // Subtract played cards
            for (card in playedCards) {
                when (card.value) {
                    "10" -> cardCounts["10"] = cardCounts["10"]?.minus(1) ?: 0
                    "11" -> cardCounts["10"] = cardCounts["10"]?.minus(1) ?: 0 // Add JACK to "10"
                    "12" -> cardCounts["10"] = cardCounts["10"]?.minus(1) ?: 0 // Add QUEEN to "10"
                    "13" -> cardCounts["10"] = cardCounts["10"]?.minus(1) ?: 0 // Add KING to "10"
                    "1", "2", "3", "4", "5", "6", "7", "8", "9" -> {
                        cardCounts[card.value] = cardCounts[card.value]?.minus(1) ?: 0
                    }
                }
            }

            // Calculate odds
            val odds = cardCounts.mapValues { (_, count) ->
                if (remainingCards > 0) {
                    (count.toDouble() / remainingCards) * 100
                } else {
                    0.0 // Handle division by zero
                }
            }

            // Update the card odds state
            _cardOdds.value = odds
        }
    }






}
