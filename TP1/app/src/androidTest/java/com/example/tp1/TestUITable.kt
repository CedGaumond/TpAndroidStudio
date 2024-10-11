package com.example.tp1

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import com.example.tp1.Model.api.Card
import com.example.tp1.Views.CardImage
import org.junit.Rule
import org.junit.Test

class TestUI {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun TestShowCards() {
        val cards = listOf(
            Card(code = "3s", imageUrl = "/static/3s.svg", value = "3", signe = "s"),
            Card(code = "3c", imageUrl = "/static/3c.svg", value = "3", signe = "c"),
            Card(code = "3h", imageUrl = "/static/3h.svg", value = "3", signe = "h")
        )

        composeTestRule.setContent {
            LazyRow {
                items(cards) { card ->
                    CardImage(card = card, index = cards.indexOf(card), isDealer = false)
                }
            }
        }

        composeTestRule.waitForIdle()

        cards.forEach { card ->
            composeTestRule
                .onNodeWithTag(card.imageUrl) // Use the imageUrl as the tag
                .assertIsDisplayed()
        }
    }
}
