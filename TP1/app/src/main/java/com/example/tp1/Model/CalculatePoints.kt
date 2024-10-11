package com.example.tp1.Model

import com.example.tp1.Model.api.Card

class CalculatePoints {

    fun calculateScore(cards: List<Card>): Int {
        var score = 0
        var aces = 0

        for (card in cards) {
            val cardValue = card.value
            score += when (cardValue) {
                "1" -> { aces++; 1 }
                "11", "12", "13" -> 10
                else -> cardValue.toIntOrNull() ?: 0
            }
        }


        for (i in 0 until aces) {
            if (score + 10 <= 21) {
                score += 10
            }
        }

        return score
    }

    fun calculateScoreIgnoringFirstCard(cards: List<Card>): Int {
        if (cards.size <= 1) return calculateScore(cards)

        var score = 0
        var aces = 0


        for (card in cards.drop(1)) {
            val cardValue = card.value
            score += when (cardValue) {
                "1" -> { aces++; 1 }
                "11", "12", "13" -> 10
                else -> cardValue.toIntOrNull() ?: 0
            }
        }


        for (i in 0 until aces) {
            if (score + 10 <= 21) {
                score += 10
            }
        }

        return score
    }
}
