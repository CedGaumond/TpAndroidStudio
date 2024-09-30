package com.example.tp1.Model
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class ModelBetting : ViewModel() {
    private val _totalBet = MutableStateFlow(1)
    val totalBet: StateFlow<Int> get() = _totalBet

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    private val _balance = MutableStateFlow(999)
    val balance: StateFlow<Int> get() = _balance

    private var previousBet: Int = 1

    fun updateTotalBet(amount: Int) {
        val newTotal = _totalBet.value + amount

        when {
            newTotal < 1 || newTotal > 100 -> {


                _errorMessage.value = "Votre mise doit être entre 1 et 100."
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
        _balance.value += _totalBet.value - 1
        _totalBet.value = 1
    }

    fun updateBalance(amount: Int) {
        _balance.value += amount
    }
}
