package com.example.tp1

import com.example.tp1.Model.CalculatePoints
import com.example.tp1.Model.api.Card
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test


class CalculatePointsTest {

    private lateinit var calculatePoints: CalculatePoints

    @Before
    fun setup() {
        calculatePoints = CalculatePoints()
    }

    @Test
    fun testCalculerPointageJoueurAvecUnAs() {
        val cartesJoueur = listOf(
            Card(code = "6c", imageUrl = "image_url_6c", value = "6", signe = "c"),
            Card(code = "1c", imageUrl = "image_url_1c", value = "1", signe = "c"),  // Ace
            Card(code = "10c", imageUrl = "image_url_10c", value = "10", signe = "c")
        )
        val result = calculatePoints.calculateScore(cartesJoueur)
        assertEquals(17, result)
    }

    @Test
    fun testCalculerPointageJoueurAvecDeuxAs() {
        val cartesJoueur = listOf(
            Card(code = "1h", imageUrl = "image_url_1h", value = "1", signe = "h"),  // Ace
            Card(code = "1c", imageUrl = "image_url_1c", value = "1", signe = "c"),  // Ace
            Card(code = "10c", imageUrl = "image_url_10c", value = "10", signe = "c")
        )
        val result = calculatePoints.calculateScore(cartesJoueur)
        assertEquals(12, result)
    }
}
