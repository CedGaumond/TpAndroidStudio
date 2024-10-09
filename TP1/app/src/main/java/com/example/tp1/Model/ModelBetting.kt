package com.example.tp1.Model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.StateFlow

class ModelBetting : ViewModel() {
    val totalBet: StateFlow<Int> get() = MoneyManager.totalBet
    val errorMessage: StateFlow<String?> get() = MoneyManager.errorMessage
    val balance: StateFlow<Int> get() = MoneyManager.balance

    fun updateTotalBet(amount: Int) {
        MoneyManager.updateTotalBet(amount)
    }

    fun resetBet() {
        MoneyManager.resetBet()
    }

    fun updateBalance(amount: Int) {
        MoneyManager.updateBalance(amount)
    }
}
