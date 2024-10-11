package com.example.tp1.Views

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import androidx.navigation.NavController
import com.example.tp1.Model.ModelBetting
import com.example.tp1.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewBetting(navController: NavController, modelBetting: ModelBetting) {
    val total by modelBetting.totalBet.collectAsState()
    val errorMessage by modelBetting.errorMessage.collectAsState()
    val balance by modelBetting.balance.collectAsState()
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) {
            showDialog = true
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                showDialog = false
                modelBetting.resetBet()
            },
            title = { Text(text = "Erreur") },
            text = { Text(text = errorMessage!!) },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    modelBetting.resetBet()
                }) {
                    Text("OK")
                }
            }
        )
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
                        Text(
                            text = stringResource(id = R.string.betting),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                        )
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
                        text = if (balance > 0) "Votre Solde est de $balance" else "Aucun solde disponible",
                        color = Color.Black,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            },
        ) { innerPadding ->
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val isPortrait = maxHeight > maxWidth
                if (isPortrait) {
                    PortraitLayout(total, modelBetting, navController)
                } else {
                    LandscapeLayout(total, modelBetting, navController)
                }
            }
        }
    }
}


@Composable
fun PortraitLayout(total: Int, modelBetting: ModelBetting, navController: NavController) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ) {
        // Show player total bet
        BettingInfo(total)

        // Display for dealer, hide first card score
        BettingInfo(total, isDealerFirstCard = true) // Hiding score for dealer's first card

        ChipRow { value -> modelBetting.updateTotalBet(value) }
        ActionButtons(modelBetting, navController)
    }
}

@Composable
fun LandscapeLayout(total: Int, modelBetting: ModelBetting, navController: NavController) {
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.weight(1f)
        ) {

            BettingInfo(total)


            BettingInfo(total, isDealerFirstCard = true)

            ActionButtons(modelBetting, navController)
        }
        ChipGrid(
            onChipClicked = { value -> modelBetting.updateTotalBet(value) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun BettingInfo(total: Int, isDealerFirstCard: Boolean = false) {
    Text(
        text = "Votre Mise",
        style = MaterialTheme.typography.headlineLarge
    )
    if (!isDealerFirstCard) {
        Text(
            text = "$total",
            style = MaterialTheme.typography.displayLarge
        )
    }
}

@Composable
fun ActionButtons(modelBetting: ModelBetting, navController: NavController) {
    Button(
        onClick = { modelBetting.resetBet() },
        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
    ) {
        Text("Réinitialiser la mise")
    }
    Spacer(modifier = Modifier.height(16.dp))
    Button(
        onClick = { navController.navigate("Table") },
        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
    ) {
        Text("Placer la mise")
    }
}

@Composable
fun ChipGrid(onChipClicked: (Int) -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Chips(chipItem(1, R.drawable.c1), onChipClicked)
            Spacer(modifier = Modifier.width(16.dp))
            Chips(chipItem(5, R.drawable.c5), onChipClicked)
            Spacer(modifier = Modifier.width(16.dp))
            Chips(chipItem(25, R.drawable.c25), onChipClicked)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Chips(chipItem(50, R.drawable.c50), onChipClicked)
            Spacer(modifier = Modifier.width(16.dp))
            Chips(chipItem(99, R.drawable.c100), onChipClicked)
        }
    }
}
@Composable
fun ChipRow(onChipClicked: (Int) -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Chips(chipItem(1, R.drawable.c1), onChipClicked)
            Spacer(modifier = Modifier.width(16.dp))
            Chips(chipItem(5, R.drawable.c5), onChipClicked)
            Spacer(modifier = Modifier.width(16.dp))
            Chips(chipItem(25, R.drawable.c25), onChipClicked)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Chips(chipItem(50, R.drawable.c50), onChipClicked)
            Spacer(modifier = Modifier.width(16.dp))
            Chips(chipItem(99, R.drawable.c100), onChipClicked)

        }
    }
}

data class chipItem(val value: Int, val imageRes: Int)

@Composable
fun Chips(chip: chipItem, onChipClicked: (Int) -> Unit) {
    Image(
        painter = painterResource(id = chip.imageRes),
        contentDescription = "Chip of value ${chip.value}",
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(80.dp)
            .clickable { onChipClicked(chip.value) }
    )
}