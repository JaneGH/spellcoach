package com.example.spellcoach.presentation.practice

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.TextButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.presentation.components.LearningCard
import com.example.spellcoach.presentation.components.PrimaryButton
import com.example.spellcoach.presentation.components.SpellCoachTopBar
import com.example.spellcoach.presentation.theme.PrimaryBlueStrong

@Composable
fun PracticeScreen(
    onBack: () -> Unit,
    onFinished: () -> Unit,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    var showWrongAnswerCard by rememberSaveable { mutableStateOf(false) }
    var showCorrectAnswerCard by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

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

    LaunchedEffect(state.currentIndex, state.feedbackCorrect) {
        when (state.feedbackCorrect) {
            false -> {
                showWrongAnswerCard = true
                showCorrectAnswerCard = false
            }
            true -> {
                showWrongAnswerCard = false
                showCorrectAnswerCard = true
            }
            null -> Unit
        }
    }

    LaunchedEffect(showWrongAnswerCard, showCorrectAnswerCard) {
        if (!showWrongAnswerCard && !showCorrectAnswerCard) {
            kotlinx.coroutines.delay(60)
            focusRequester.requestFocus()
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

        AnimatedVisibility(
            visible = showCorrectAnswerCard,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 })
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                val total = state.words.size.coerceAtLeast(0)
                val completed = state.currentIndex.coerceIn(0, total)
                CorrectAnswerSuccessCard(
                    completed = completed,
                    total = total,
                    onNextWord = {
                        showCorrectAnswerCard = false
                        viewModel.onInputChange("")
                        viewModel.listen()
                    }
                )
            }
        }

        AnimatedVisibility(
            visible = !showCorrectAnswerCard,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 })
        ) {
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
                        val correctWord = state.words.getOrNull(state.currentIndex)?.text.orEmpty()
                        val spacedCorrectWord = remember(correctWord) {
                            correctWord
                                .trim()
                                .uppercase()
                                .toCharArray()
                                .joinToString(" ")
                        }

                        Box(
                            modifier = Modifier
                                .size(76.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF0B6B8C))
                                .clickable { viewModel.listen() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.VolumeUp,
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

                        Spacer(Modifier.height(18.dp))

                        AnimatedVisibility(
                            visible = showWrongAnswerCard,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
                            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 })
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xFFFFF1F2))
                                    .border(
                                        width = 1.dp,
                                        color = Color(0xFFFCA5A5),
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Outlined.Lightbulb,
                                    contentDescription = null,
                                    tint = Color(0xFFDC2626),
                                    modifier = Modifier.size(28.dp)
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                    text = "Keep trying!",
                                    color = Color(0xFFDC2626),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 28.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = "The correct spelling is:",
                                    color = Color(0xFF7F1D1D),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(10.dp))
                                Text(
                                    text = spacedCorrectWord,
                                    color = Color(0xFFDC2626),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 22.sp,
                                    letterSpacing = 6.sp,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(Modifier.height(14.dp))
                                Text(
                                    text = "You're doing great! Let's practice it one more time.",
                                    color = Color(0xFF991B1B),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 16.sp,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(Modifier.height(18.dp))
                                Button(
                                    onClick = {
                                        viewModel.onInputChange("")
                                        showWrongAnswerCard = false
                                        viewModel.listen()
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(52.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF0B6B8C),
                                        contentColor = Color.White
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = "Try Again",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp
                                        )
                                        Spacer(Modifier.width(10.dp))
                                        Icon(
                                            imageVector = Icons.Filled.Refresh,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }

                        AnimatedVisibility(
                            visible = !showWrongAnswerCard,
                            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
                            exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 })
                        ) {
                            Column {
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
                                        .height(92.dp)
                                        .focusRequester(focusRequester),
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
                    }
                }

                Spacer(Modifier.height(14.dp))

                if (state.hintsEnabled) {
                    val nudgeHints = state.feedbackCorrect == false && !state.showHints

                    OutlinedButton(
                        onClick = viewModel::showHints,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (nudgeHints) Color(0xFFF59E0B) else PrimaryBlueStrong)
                    ) {
                        Text(
                            text = if (state.showHints) "Letter hints shown" else "Show Letter Hints",
                            fontWeight = FontWeight.Bold,
                            color = if (nudgeHints) Color(0xFFB45309) else PrimaryBlueStrong,
                            fontSize = 16.sp
                        )
                    }
                    if (nudgeHints) {
                        Spacer(Modifier.height(8.dp))
                        TextButton(onClick = viewModel::showHints, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                            Text(
                                text = "Tip: try a hint to get unstuck",
                                color = Color(0xFFB45309),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    AnimatedVisibility(
                        visible = state.showHints,
                        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
                        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 4 })
                    ) {
                        Column {
                            Spacer(Modifier.height(12.dp))
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
                                        text = "Tap letters to help spell the word.",
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
            }
        }
    }
}

@Composable
private fun CorrectAnswerSuccessCard(
    completed: Int,
    total: Int,
    onNextWord: () -> Unit
) {
    val progress = if (total <= 0) 0f else (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(targetValue = progress, label = "correct_progress")

    val cardShape = RoundedCornerShape(24.dp)
    val titleGreen = Color(0xFF15803D)
    val badgeBg = Color(0xFFDDF7E7)
    val badgeIcon = Color(0xFF166534)
    val subtitle = Color(0xFF6B7280)
    val progressLabel = Color(0xFF374151)
    val progressAccent = Color(0xFF15803D)
    val track = Color(0xFFE5E7EB)
    val fill = Color(0xFF34D399)
    val buttonBlue = Color(0xFF0B6B8C)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(elevation = 10.dp, shape = cardShape, clip = false),
        shape = cardShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(badgeBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = badgeIcon,
                    modifier = Modifier.size(30.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = "Correct!",
                color = titleGreen,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Great job, you're a spelling star!",
                color = subtitle,
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PROGRESS",
                    color = progressLabel,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = "${completed} / ${total} WORDS",
                    color = progressAccent,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(CircleShape)
                    .background(track)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(fill)
                )
            }

            Spacer(Modifier.height(26.dp))

            Button(
                onClick = onNextWord,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = buttonBlue,
                    contentColor = Color.White
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Next Word",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
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

