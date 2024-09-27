package com.example.tp1.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.tp1.Model.ModelBlackJack
import com.example.tp1.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewBlackJack(viewModel: ModelBlackJack = ModelBlackJack(), modifier: Modifier = Modifier) {

    Box(modifier = modifier.fillMaxSize()) {

        Scaffold(
            topBar = {
                TopAppBar(
                    colors = topAppBarColors(
                        containerColor =  MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.secondary,
                    ),
                    title = {
                        Text("Black Jack (Not Rigged)",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            )


                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor =  Color(0xFF28743E),
                    contentColor = MaterialTheme.colorScheme.primary,


                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Bottom app bar",
                        color =  MaterialTheme.colorScheme.secondary,

                    )
                }
            },

            ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(50.dp),
            ) {
                Image(
                    painter = painterResource(id = R.drawable.background),
                    contentDescription = "Demo Background",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}





