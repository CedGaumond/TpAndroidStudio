package com.example.tp1.Model

import com.example.tp1.Model.api.Card

class CalculatePoints {

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

    fun calculateScoreIgnoringFirstCard(cards: List<Card>): Int {
        if (cards.size <= 1) return calculateScore(cards)

        var score = 0
        var aces = 0

        // Start calculating from the second card
        for (card in cards.drop(1)) {
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
}
