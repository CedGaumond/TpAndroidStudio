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

    private var previousBet: Int = MIN_BET

    fun updateTotalBet(amount: Int) {
        val newTotal = _totalBet.value + amount

        when {
            newTotal < MIN_BET || newTotal > MAX_BET -> {
                _errorMessage.value = "Votre mise doit être entre $MIN_BET et $MAX_BET."
                resetBet()
            }
            newTotal > _balance.value -> {
                _errorMessage.value = "Vous ne pouvez pas parier plus que votre solde disponible."
                resetBet()
            }
            else -> {
                previousBet = _totalBet.value
                _totalBet.value = newTotal
                _balance.value -= amount
                _errorMessage.value = null
            }
        }
    }

    fun resetBet() {
        _balance.value += _totalBet.value - MIN_BET
        _totalBet.value = MIN_BET
    }

    fun updateBalance(amount: Int) {
        _balance.value += amount
    }
}
