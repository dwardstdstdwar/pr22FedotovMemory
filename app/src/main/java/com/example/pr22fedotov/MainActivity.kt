package com.example.pr22fedotov

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import androidx.compose.material3.MaterialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                MemoriaGame()
            }
        }
    }
}

data class CardItem(
    val id: Int,
    val value: String,
    var isOpen: Boolean = false,
    var isMatched: Boolean = false
)

@Composable
fun MemoriaGame() {
    var gameStarted by remember { mutableStateOf(false) }

    if (!gameStarted) {
        StartScreen {
            gameStarted = true
        }
    } else {
        GameScreen()
    }
}

@Composable
fun StartScreen(onStart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE3F2FD)),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Игра Memoria",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onStart) {
            Text("Начать игру")
        }
    }
}

@Composable
fun GameScreen() {
    val images = listOf(
        "🐶", "🐱", "🐭", "🐹", "🐰", "🦊",
        "🐻", "🐼", "🐸", "🐵", "🐔", "🐧",
        "🐦", "🐤", "🦁", "🐯", "🐨", "🐷"
    )

    var cards by remember {
        mutableStateOf(
            (images + images)
                .shuffled()
                .mapIndexed { index, value ->
                    CardItem(id = index, value = value)
                }
        )
    }

    var firstCardIndex by remember { mutableStateOf<Int?>(null) }
    var secondCardIndex by remember { mutableStateOf<Int?>(null) }
    var moves by remember { mutableStateOf(0) }
    var gameOver by remember { mutableStateOf(false) }

    LaunchedEffect(secondCardIndex) {
        val first = firstCardIndex
        val second = secondCardIndex

        if (first != null && second != null) {
            delay(700)

            cards = cards.toMutableList().also { list ->
                if (list[first].value == list[second].value) {
                    list[first] = list[first].copy(isMatched = true)
                    list[second] = list[second].copy(isMatched = true)
                } else {
                    list[first] = list[first].copy(isOpen = false)
                    list[second] = list[second].copy(isOpen = false)
                }
            }

            firstCardIndex = null
            secondCardIndex = null

            if (cards.all { it.isMatched }) {
                gameOver = true
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF8E1))
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ходы: $moves",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(6),
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(4.dp)
        ) {
            itemsIndexed(cards) { index, card ->
                MemoryCard(
                    card = card,
                    onClick = {
                        if (!card.isOpen && !card.isMatched && secondCardIndex == null) {
                            cards = cards.toMutableList().also { list ->
                                list[index] = list[index].copy(isOpen = true)
                            }

                            if (firstCardIndex == null) {
                                firstCardIndex = index
                            } else {
                                secondCardIndex = index
                                moves++
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (gameOver) {
            Text(
                text = "Игра окончена!",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(onClick = {
                cards = (images + images)
                    .shuffled()
                    .mapIndexed { index, value ->
                        CardItem(id = index, value = value)
                    }
                moves = 0
                gameOver = false
                firstCardIndex = null
                secondCardIndex = null
            }) {
                Text("Начать заново")
            }
        }
    }
}

@Composable
fun MemoryCard(card: CardItem, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(3.dp)
            .aspectRatio(1f)
            .background(
                when {
                    card.isMatched -> Color.Transparent
                    card.isOpen -> Color.White
                    else -> Color(0xFF64B5F6)
                }
            )
            .clickable(enabled = !card.isMatched) {
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        if (card.isOpen && !card.isMatched) {
            Text(
                text = card.value,
                style = MaterialTheme.typography.headlineMedium
            )
        } else if (!card.isMatched) {
            Text(
                text = "?",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
