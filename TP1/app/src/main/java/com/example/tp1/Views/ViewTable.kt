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
    val cardsDealer by modelTable.cardsDealer.collectAsState()
    val cardsPlayer by modelTable.cardsPlayer.collectAsState()

    var showWinner by remember { mutableStateOf(false) }
    var playerStayed by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(true) }

    // Start the game automatically if it's the first time playing
    LaunchedEffect(Unit) {
        if (gameState == GameState.NOT_STARTED) {
            modelTable.startGame()
        }
    }

    // Update loading state based on card availability
    LaunchedEffect(cardsDealer, cardsPlayer) {
        isLoading = cardsDealer.isEmpty() && cardsPlayer.isEmpty()
    }

    // Show winner dialog
    LaunchedEffect(winner) {
        if (winner != null) {
            showWinner = true
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
                // TopAppBar implementation...
            },
            bottomBar = {
                // BottomAppBar implementation...
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
                    // Only show the card stacks if loading is done
                    if (!isLoading) {
                        CardStack(
                            cards = cardsDealer,
                            label = "Cartes du croupier",
                            playerStayed = playerStayed,
                            gameState = gameState
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(25.dp)
                                .background(Color.Black)
                        )

                        CardStack(
                            cards = cardsPlayer,
                            label = "Vos Cartes",
                            playerStayed = playerStayed,
                            gameState = gameState,
                        )

                        Spacer(modifier = Modifier.weight(1f))

                        // Action buttons for player
                        if (gameState != GameState.NOT_STARTED && gameState != GameState.GAME_OVER) {
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
                                    onClick = {
                                        playerStayed = true
                                        modelTable.playerStand()
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                    )
                                ) {
                                    Text("Rester")
                                }
                            }
                        }
                    } else {

                        CircularProgressIndicator()
                    }
                }

                // Winner dialog setup
                AnimatedVisibility(visible = showWinner) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .width(300.dp)
                                .background(Color.White, shape = MaterialTheme.shapes.medium)
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Text(
                                text = when (winner) {
                                    "Player" -> "Joueur Gagne"
                                    "Dealer" -> "Croupier Gagne"
                                    else -> "Match Nul"
                                },
                                style = MaterialTheme.typography.titleLarge.copy(color = Color.Black),
                                textAlign = TextAlign.Center
                            )

                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        modelTable.resetGame()
                                        playerStayed = false
                                        modelTable.startGame()
                                        showWinner = false
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                    )
                                ) {
                                    Text("Rejouer")
                                }

                                OutlinedButton(
                                    onClick = {
                                        navController.navigate("Betting")
                                        showWinner = false
                                    },
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                    )
                                ) {
                                    Text("Mise")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CardStack(
    cards: List<Card>,
    label: String,
    playerStayed: Boolean,
    gameState: GameState,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(fontSize = 48.sp),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentAlignment = Alignment.Center
        ) {
            if (cards.isEmpty()) {
                Text("Pas de cartes à afficher")
            } else {
                cards.forEachIndexed { index, card ->
                    CardImage(
                        card = card,
                        index = index,
                        totalCards = cards.size,
                        isDealer = label == "Cartes du croupier" && (index == 0 && !playerStayed && gameState != GameState.GAME_OVER)
                    )
                }
            }
        }
    }
}

@Composable
fun CardImage(card: Card, index: Int, totalCards: Int, isDealer: Boolean) {
    val cardWidth = 120.dp
    val cardHeight = 180.dp
    val horizontalOffset = 30.dp

    val painter = if (isDealer) {
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .decoderFactory(SvgDecoder.Factory())
                .data("https://420c56.drynish.synology.me/static/back.svg")
                .size(240, 360)
                .build()
        )
    } else {
        rememberAsyncImagePainter(
            model = ImageRequest.Builder(LocalContext.current)
                .decoderFactory(SvgDecoder.Factory())
                .data("https://420c56.drynish.synology.me${card.imageUrl}")
                .size(240, 360)
                .build()
        )
    }

    Box(
        modifier = Modifier
            .width(cardWidth)
            .height(cardHeight)
            .offset(x = index * horizontalOffset)
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
