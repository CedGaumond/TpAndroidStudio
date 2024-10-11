package com.example.tp1

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tp1.Model.ModelBetting
import com.example.tp1.Model.ModelTable
import com.example.tp1.Views.ViewBetting
import com.example.tp1.Views.ViewBlackJack
import com.example.tp1.ui.theme.TP1Theme

class MainActivity : ComponentActivity() {

    init { app = this }
    companion object {
        private lateinit var app: MainActivity
        fun getAppContext(): Context =
            app.applicationContext
    }


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TP1Theme {
                route()
                }
            }
        }
    }


@Composable
fun route(){
    val modelBetting: ModelBetting = viewModel()
    val modelTable : ModelTable = viewModel()
    val navController = rememberNavController()
    NavHost(navController, startDestination = "Betting") {
        composable(route = "Betting") {

            ViewBetting(navController = navController, modelBetting)
        }
        composable(route = "Table") { backStackEntry ->
            ViewBlackJack(modelTable ,navController = navController, modelBetting)
        }
    }

}




