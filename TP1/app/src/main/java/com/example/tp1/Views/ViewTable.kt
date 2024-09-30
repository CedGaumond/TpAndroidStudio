package com.example.tp1.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.tp1.Model.GameState
import com.example.tp1.Model.ModelBetting
import com.example.tp1.Model.ModelTable
import com.example.tp1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewBlackJack(
    modelTable: ModelTable = ModelTable(),
    navController: NavController,
    modelBetting: ModelBetting = ModelBetting()
) {
    val bet by modelBetting.totalBet.collectAsState()
    val balance by modelBetting.balance.collectAsState()
    val gameState by modelTable.gameState.collectAsState()
    val playerCards by modelTable.cardsPlayer.collectAsState()
    val dealerCards by modelTable.cardsDealer.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Demo Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.secondary,
                    ),
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            OutlinedButton(
                                onClick = { navController.navigate("Betting") },
                                modifier = Modifier.padding(end = 20.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = Color.Black,
                                    contentColor = MaterialTheme.colorScheme.secondary
                                )
                            ) {
                                Text("← Mise")
                            }
                            Text(
                                text = "Votre mise est de $bet",
                                textAlign = TextAlign.Left,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                )
            },
            bottomBar = {
                BottomAppBar(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.primary,
                ) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        text = "Votre Solde est de $balance",
                        color = Color(0xFF000000),
                        style = androidx.compose.ui.text.TextStyle(fontSize = 40.sp)
                    )
                }
            },
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    // Player's Cards
                    Text(
                        text = "Player's Cards:",
                        modifier = Modifier.padding(top = 16.dp),
                        style = androidx.compose.ui.text.TextStyle(fontSize = 30.sp)
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        showPlayerCards(modelTable)
                    }

                    // Dealer's Cards
                    Text(
                        text = "Dealer's Cards:",
                        modifier = Modifier.padding(top = 16.dp),
                        style = androidx.compose.ui.text.TextStyle(fontSize = 30.sp)
                    )

                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        showDealerCards(modelTable)
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    if (gameState == GameState.NOT_STARTED || gameState == GameState.GAME_OVER) {
                        OutlinedButton(
                            onClick = { modelTable.startGame() },
                            modifier = Modifier.padding(bottom = 16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Text("Jouer")
                        }
                    } else {
                        Row(
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            OutlinedButton(
                                onClick = { modelTable.playerHit() },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Text("Tirer")
                            }

                            OutlinedButton(
                                onClick = { modelTable.playerStand() },
                                colors = ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.onSecondary
                                )
                            ) {
                                Text("Rester")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun showPlayerCards(modelTable: ModelTable) {
    val playerCards by modelTable.cardsPlayer.collectAsState()

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp), // Optional padding
        horizontalArrangement = Arrangement.Center // Center cards in the row
    ) {
        for ((index, card) in playerCards.withIndex()) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .decoderFactory(SvgDecoder.Factory())
                    .data("https://420c56.drynish.synology.me${card.imageUrl}")
                    .size(800, 600)
                    .build()
            )
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp) // Adjust size as needed
                    .offset(x = (index * -20).dp) // Adjust offset for overlapping effect
            )
        }
    }
}

@Composable
fun showDealerCards(modelTable: ModelTable) {
    val dealerCards by modelTable.cardsDealer.collectAsState()

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(horizontal = 16.dp), // Optional padding
        horizontalArrangement = Arrangement.Center // Center cards in the row
    ) {
        for ((index, card) in dealerCards.withIndex()) {
            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .decoderFactory(SvgDecoder.Factory())
                    .data("https://420c56.drynish.synology.me${card.imageUrl}")
                    .size(900, 900)
                    .build()
            )

            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp) // Adjust size as needed
                    .offset(x = (index * -90).dp) // Adjust offset for overlapping effect
            )
        }
    }
}
