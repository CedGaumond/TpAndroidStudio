package com.example.tp1.Model

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object MoneyManager {
    private const val MIN_BET = 1
    private const val MAX_BET = 100
    private const val INITIAL_BALANCE = 999

    private val _totalBet = MutableStateFlow(MIN_BET)
    val totalBet: StateFlow<Int> get() = _totalBet

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _balance = MutableStateFlow(INITIAL_BALANCE)
    val balance: StateFlow<Int> get() = _balance

    fun updateTotalBet(amount: Int) {
        val newTotal = _totalBet.value + amount

        when {
            newTotal < MIN_BET || newTotal > MAX_BET -> {
                _errorMessage.value = "Votre mise doit être entre $MIN_BET et $MAX_BET."
                // No automatic reset, just inform the user
            }
            newTotal > _balance.value -> {
                _errorMessage.value = "Vous ne pouvez pas parier plus que votre solde disponible."
                // No automatic reset, just inform the user
            }
            else -> {
                _totalBet.value = newTotal
                _balance.value -= amount
                _errorMessage.value = null
            }
        }
    }

    fun resetBet() {
        // Restore balance to what it was before the last bet
        val betAmount = _totalBet.value - MIN_BET
        if (betAmount > 0) {
            _balance.value += betAmount
        }
        _totalBet.value = MIN_BET
    }

    fun updateBalance(amount: Int) {
        _balance.value += amount
    }

    fun setBalance(amount: Int) {
        _balance.value = amount
    }
}
