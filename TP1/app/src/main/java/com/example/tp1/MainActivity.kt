package com.example.tp1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
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
            val miseDepart = backStackEntry.arguments?.getString("miseDepart")?.toIntOrNull()
            ViewBlackJack(modelTable ,navController = navController, modelBetting)
        }
    }

}




