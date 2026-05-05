package com.example.spellcoach.presentation.practice

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.presentation.components.LearningCard
import com.example.spellcoach.presentation.components.PrimaryButton
import com.example.spellcoach.presentation.components.SpellCoachTopBar

@Composable
fun PracticeScreen(
    onBack: () -> Unit,
    onFinished: () -> Unit,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    val bounce by animateFloatAsState(
        targetValue = if (state.animationHint == PracticeAnimHint.BounceOk) 1.04f else 1f,
        label = "bounce"
    )
    val shake by animateFloatAsState(
        targetValue = if (state.animationHint == PracticeAnimHint.ShakeWrong) 3f else 0f,
        label = "shake"
    )

    LaunchedEffect(state.animationHint) {
        if (state.animationHint != PracticeAnimHint.None) {
            kotlinx.coroutines.delay(350)
            viewModel.clearAnimationHint()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
        val progressText = if (state.words.isEmpty()) {
            "0 / 0 words"
        } else {
            "${(state.currentIndex + 1).coerceAtMost(state.words.size)} / ${state.words.size} words"
        }
        SpellCoachTopBar(
            showBack = true,
            onBack = onBack,
            brandTitle = "Practice",
            brandAccent = null,
            screenTitle = progressText,
            heroTitle = null,
            subtitleBelowBrand = null
        )

        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Spacer(Modifier.height(12.dp))

            LearningCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = bounce
                        scaleY = bounce
                        rotationZ = if (shake > 0f) shake else 0f
                    }
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(76.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF0B6B8C))
                            .clickable { viewModel.listen() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.VolumeUp,
                            contentDescription = "Listen",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "LISTEN",
                        color = Color(0xFF0B6B8C),
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Tap to hear the word again",
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                    Spacer(Modifier.height(14.dp))

                    OutlinedTextField(
                        value = state.input,
                        onValueChange = viewModel::onInputChange,
                        placeholder = {
                            Text(
                                text = "Type here",
                                color = Color(0xFFCBD5E1),
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Medium
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(92.dp),
                        shape = RoundedCornerShape(0.dp)
                    )
                    Spacer(Modifier.height(14.dp))
                    PrimaryButton(
                        text = "Check Word   →",
                        onClick = { viewModel.checkWord(onFinished) },
                        containerColor = Color(0xFF0B6B8C)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFE3F2FD)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "i", color = Color(0xFF0B6B8C), fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "Need a hint? Use a letter chip below.",
                        color = Color(0xFF334155),
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.letters) { ch ->
                    LetterChip(letter = ch, onClick = { viewModel.appendLetter(ch) })
                }
            }
        }
    }
}

@Composable
private fun LetterChip(letter: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(width = 54.dp, height = 54.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.uppercase(),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0F172A)
        )
    }
}

