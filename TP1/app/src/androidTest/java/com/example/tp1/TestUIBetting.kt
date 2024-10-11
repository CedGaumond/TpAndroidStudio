package com.example.tp1

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.tp1.Model.ModelBetting
import com.example.tp1.Model.MoneyManager
import com.example.tp1.Views.ViewBetting
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@RunWith(AndroidJUnit4::class)
class TestUIBetting {
    @get:Rule
    val composeTestRule = createComposeRule()




    @Test
    fun TestShowBalance() {
        val balance = 12664
        val modelBetting = ModelBetting()


        MoneyManager.setBalance(balance)

        composeTestRule.setContent {
            ViewBetting(navController = rememberNavController(), modelBetting = modelBetting)
        }


        composeTestRule.waitForIdle()


        composeTestRule

            .onNodeWithText("Votre Solde est de $balance")
            .assertIsDisplayed()

    }
}
