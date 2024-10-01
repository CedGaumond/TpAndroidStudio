package com.example.tp1.Views

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.times
import androidx.compose.ui.zIndex
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.tp1.Model.GameState
import com.example.tp1.Model.ModelBetting
import com.example.tp1.Model.ModelTable
import com.example.tp1.Model.api.Card
import com.example.tp1.R
import kotlinx.coroutines.delay

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
    val winner by modelTable.winner.collectAsState()
    var showWinner by remember { mutableStateOf(false) }

    LaunchedEffect(winner) {
        winner?.let {
            showWinner = true
            delay(3000) // Show for 3 seconds
            showWinner = false
            modelTable.resetGame() // Reset the game state if necessary
        }
    }

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
                    CardStack(
                        cards = modelTable.cardsDealer.collectAsState().value,
                        label = "Dealer's Cards"
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    CardStack(
                        cards = modelTable.cardsPlayer.collectAsState().value,
                        label = "Player's Cards"
                    )

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

        // Winner popup
        AnimatedVisibility(visible = showWinner) {
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .background(Color.Black.copy(alpha = 0.7f), shape = MaterialTheme.shapes.medium)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = winner ?: "",
                    style = MaterialTheme.typography.titleLarge.copy(color = Color.White),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
fun CardStack(cards: List<Card>, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Create a Box to stack the cards on top of each other
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center
        ) {
            cards.forEachIndexed { index, card ->
                CardImage(
                    card = card,
                    index = index,
                    totalCards = cards.size
                )
            }
        }
    }
}

@Composable
fun CardImage(card: Card, index: Int, totalCards: Int) {
    val cardWidth = 120.dp
    val cardHeight = 180.dp
    val horizontalOffset = 30.dp // Adjust this value for more/less overlap

    val painter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .decoderFactory(SvgDecoder.Factory())
            .data("https://420c56.drynish.synology.me${card.imageUrl}")
            .size(240, 360)
            .build()
    )

    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .offset(x = index * horizontalOffset) // Apply horizontal offset for overlap
            .zIndex(index.toFloat())
            .drawBehind {
                drawRect(Color.White)
            }
    ) {
        Image(
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}
