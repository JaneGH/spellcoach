package com.example.spellcoach.presentation.practice

import android.os.SystemClock
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.R
import com.example.spellcoach.presentation.components.SpellCoachTopBar
import com.example.spellcoach.presentation.components.glass.AmbientBackground
import com.example.spellcoach.presentation.components.glass.GlassButton
import com.example.spellcoach.presentation.components.glass.GlassCard
import com.example.spellcoach.presentation.components.glass.GlassOrbIconButton
import com.example.spellcoach.presentation.components.glass.GlassSegmentedControl
import com.example.spellcoach.presentation.components.glass.SegmentedOption
import com.example.spellcoach.presentation.components.glass.GlassTextField
import com.example.spellcoach.presentation.theme.PrimaryBlueStrong
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

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
    var inputMode by rememberSaveable { mutableStateOf(PracticeInputMode.Keyboard) }

    val recognizer = remember {
        val id = requireNotNull(DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US")) {
            "Digital ink model identifier missing for en-US"
        }
        val model = DigitalInkRecognitionModel.builder(id).build()
        DigitalInkRecognition.getClient(
            DigitalInkRecognizerOptions.builder(model).build()
        )
    }


    LaunchedEffect(state.animationHint) {
        if (state.animationHint != PracticeAnimHint.None) {
            kotlinx.coroutines.delay(350)
            viewModel.clearAnimationHint()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.events.collect { e ->
            if (e is PracticeEvent.Finished) onFinished()
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

    LaunchedEffect(
        showWrongAnswerCard,
        showCorrectAnswerCard,
        state.words.isEmpty(),
        state.loading,
        inputMode
    ) {
        if (
            !state.loading &&
            state.words.isNotEmpty() &&
            !state.sessionComplete &&
            inputMode == PracticeInputMode.Keyboard &&
            !showWrongAnswerCard &&
            !showCorrectAnswerCard
        ) {
            kotlinx.coroutines.delay(60)
            runCatching { focusRequester.requestFocus() }
        }
    }

    AmbientBackground {
        Column(modifier = Modifier.fillMaxSize()) {
            val totalWords = state.allWords.size
            val required = state.requiredCorrectAnswers.coerceAtLeast(1)
            val masteredWords = state.allWords.count { it.correctCount >= required }

            val progressText = if (totalWords <= 0) {
                "0 / 0 mastered"
            } else {
                "$masteredWords / $totalWords mastered"
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

            if (!state.loading && state.sessionComplete && !showCorrectAnswerCard) {
                val totalWords = state.allWords.size
                val masteredWords = state.masteredWordsCount.coerceIn(0, totalWords)
                val wordsNeedingReview = state.wordsNeedingReviewCount.coerceAtLeast(0)

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        cornerRadius = 32.dp,
                        contentPadding = PaddingValues(
                            horizontal = 22.dp,
                            vertical = 24.dp
                        )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (totalWords <= 0) "No words to practice yet" else "Practice completed!",
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 28.sp,
                                color = Color(0xFF0F172A),
                                textAlign = TextAlign.Center
                            )

                            Spacer(Modifier.height(10.dp))

                            Text(
                                text = if (totalWords <= 0) {
                                    "Add some words to start your spelling practice."
                                } else if (wordsNeedingReview <= 0) {
                                    "Great job. All words are ready for review!"
                                } else {
                                    "Great progress today. $masteredWords / $totalWords words mastered."
                                },
                                fontWeight = FontWeight.Medium,
                                fontSize = 16.sp,
                                color = Color(0xFF475569),
                                textAlign = TextAlign.Center
                            )

                            if (totalWords > 0) {
                                Spacer(Modifier.height(8.dp))

                                Text(
                                    text = if (wordsNeedingReview <= 0) {
                                        "No review needed today."
                                    } else {
                                        "$wordsNeedingReview words still need practice"
                                    },
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 16.sp,
                                    color = Color(0xFF475569),
                                    textAlign = TextAlign.Center
                                )
                            }

                            Spacer(Modifier.height(22.dp))

                            GlassButton(
                                text = "Practice again",
                                onClick = viewModel::practiceAgain,
                                gradient = Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF0B6B8C),
                                        Color(0xFF22D3EE)
                                    )
                                )
                            )

                            Spacer(Modifier.height(10.dp))

                            OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(16.dp)
                            ) {
                                Text(text = "Back to Lists", fontWeight = FontWeight.SemiBold)
                            }

                            Spacer(Modifier.height(6.dp))

                            TextButton(
                                onClick = viewModel::resetListProgress,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Reset progress",
                                    color = Color(0xFFE11D48),
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
                return@Column
            }

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
                    CorrectAnswerSuccessCard(
                        completed = masteredWords.coerceIn(0, totalWords),
                        total = totalWords.coerceAtLeast(0),
                        showWordMastered = state.wordJustMastered,
                        wordProgressText = run {
                            val curWord = state.words.getOrNull(state.currentIndex)
                            if (curWord == null) {
                                ""
                            } else {
                                val cur = curWord.correctCount.coerceIn(0, required)
                                "Word progress: $cur of $required correct"
                            }
                        },
                        onNextWord = {
                            showCorrectAnswerCard = false
                            viewModel.onNextWord()
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
                    GlassCard(
                        modifier = Modifier.fillMaxWidth() .weight(1f),
                        cornerRadius = 28.dp,
                        contentPadding = PaddingValues(
                            horizontal = 16.dp,
                            vertical = 18.dp
                        )
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

                            GlassOrbIconButton(
                                icon = Icons.AutoMirrored.Filled.VolumeUp,
                                contentDescription = "Listen",
                                onClick = viewModel::listen
                            )

                            Spacer(Modifier.height(10.dp))

                            GlassSegmentedControl(
                                options = listOf(
                                    SegmentedOption("Keyboard", Icons.Rounded.Keyboard),
                                    SegmentedOption("Handwriting", Icons.Rounded.Draw)
                                ),
                                selectedIndex = if (inputMode == PracticeInputMode.Keyboard) 0 else 1,
                                onSelectIndex = { idx ->
                                    inputMode = if (idx == 0) {
                                        PracticeInputMode.Keyboard
                                    } else {
                                        PracticeInputMode.Handwriting
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(Modifier.height(8.dp))

                            val current = state.words.getOrNull(state.currentIndex)
                            if (current != null) {
                                val cur = current.correctCount.coerceIn(0, required)

                                Text(
                                    text = "$cur of $required correct",
                                    color = Color(0xFF0F172A).copy(alpha = 0.50f),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )

                                Spacer(Modifier.height(8.dp))
                            }

                            AnimatedVisibility(
                                visible = showWrongAnswerCard,
                                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
                                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 })
                            ) {
                                WrongAnswerCard(
                                    spacedCorrectWord = spacedCorrectWord,
                                    onTryAgain = {
                                        viewModel.onInputChange("")
                                        viewModel.clearFeedback()
                                        showWrongAnswerCard = false
                                        viewModel.listen()
                                    }
                                )
                            }

                            AnimatedVisibility(
                                visible = !showWrongAnswerCard,
                                enter = fadeIn() + slideInVertically(initialOffsetY = { it / 6 }),
                                exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 6 })
                            ) {
                                AnimatedContent(
                                    targetState = inputMode,
                                    transitionSpec = {
                                        (fadeIn() + slideInVertically(initialOffsetY = { it / 8 }))
                                            .togetherWith(
                                                fadeOut() + slideOutVertically(targetOffsetY = { it / 8 })
                                            )
                                            .using(SizeTransform(clip = false))
                                    },
                                    label = "input_mode_switch"
                                ) { mode ->
                                    when (mode) {
                                        PracticeInputMode.Keyboard -> {
                                            Column {
                                                Box(modifier = Modifier.fillMaxWidth()) {
                                                    GlassTextField(
                                                        value = state.input,
                                                        onValueChange = viewModel::onInputChange,
                                                        placeholder = "Type here",
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .focusRequester(focusRequester),

                                                        keyboardOptions = KeyboardOptions(
                                                            imeAction = ImeAction.Done,
                                                            autoCorrectEnabled = false,
                                                            capitalization = KeyboardCapitalization.None,
                                                            keyboardType = KeyboardType.Password
                                                        ),

                                                        visualTransformation = VisualTransformation.None,

                                                        keyboardActions = KeyboardActions(
                                                            onDone = {
                                                                showWrongAnswerCard = false
                                                                viewModel.checkWord()
                                                            }
                                                        )
                                                    )

                                                    Image(
                                                        painter = painterResource(id = R.drawable.fox_neutral),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .align(Alignment.BottomEnd)
                                                            .padding(end = 10.dp, bottom = 8.dp)
                                                            .size(42.dp)
                                                            .offset(y = 4.dp)
                                                            .graphicsLayer { alpha = 0.7f }
                                                    )
                                                }

                                                Spacer(Modifier.height(10.dp))

                                                GlassButton(
                                                    text = "Check word",
                                                    onClick = {
                                                        showWrongAnswerCard = false
                                                        viewModel.checkWord()
                                                    },
                                                    leadingIcon = Icons.AutoMirrored.Filled.ArrowForward,
                                                    gradient = Brush.linearGradient(
                                                        colors = listOf(
                                                            Color(0xFF0B6B8C),
                                                            Color(0xFF22D3EE)
                                                        )
                                                    )
                                                )
                                            }
                                        }

                                        PracticeInputMode.Handwriting -> {
                                            HandwritingInputPanel(
                                                modifier = Modifier.fillMaxSize(),
                                                recognizer = recognizer,
                                                onRecognized = { recognized ->
                                                    viewModel.onInputChange(recognized)
                                                    viewModel.checkWord()
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    if (
                        inputMode == PracticeInputMode.Keyboard &&
                        state.hintsEnabled &&
                        state.input.isNotBlank()
                    ) {
                        HintsSection(
                            showHints = state.showHints,
                            nudgeHints = state.feedbackCorrect == false && !state.showHints,
                            letters = state.letters,
                            onShowHints = viewModel::showHints,
                            onAppendLetter = viewModel::appendLetter
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WrongAnswerCard(
    spacedCorrectWord: String,
    onTryAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(Color.White.copy(alpha = 0.66f))
            .border(
                width = 1.dp,
                color = Color(0xFFFCA5A5).copy(alpha = 0.26f),
                shape = RoundedCornerShape(26.dp)
            )
            .padding(horizontal = 18.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.fox_supportive),
            contentDescription = null,
            modifier = Modifier.size(80.dp)
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Keep trying!",
            color = Color(0xFFE11D48),
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "The correct spelling is:",
            color = Color(0xFF7F1D1D).copy(alpha = 0.82f),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = spacedCorrectWord,
            color = Color(0xFFE11D48),
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            letterSpacing = 6.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "You’re doing great. Try it once more.",
            color = Color(0xFF991B1B).copy(alpha = 0.78f),
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(16.dp))

        GlassButton(
            text = "Try again",
            onClick = onTryAgain,
            leadingIcon = Icons.Filled.Refresh,
            gradient = Brush.linearGradient(
                colors = listOf(
                    Color(0xFFFF6B81),
                    Color(0xFFFFB38A)
                )
            )
        )
    }
}

@Composable
private fun HintsSection(
    showHints: Boolean,
    nudgeHints: Boolean,
    letters: List<String>,
    onShowHints: () -> Unit,
    onAppendLetter: (String) -> Unit
) {
    OutlinedButton(
        onClick = onShowHints,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp,
            if (nudgeHints) {
                Color(0xFFF59E0B)
            } else {
                PrimaryBlueStrong.copy(alpha = 0.32f)
            }
        )
    ) {
        Text(
            text = "Letter hints shown",
            color = PrimaryBlueStrong.copy(alpha = 0.65f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 6.dp)
        )
    }

    if (nudgeHints) {
        Spacer(Modifier.height(6.dp))

        TextButton(
            onClick = onShowHints,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Tip: try a hint to get unstuck",
                color = Color(0xFFB45309),
                fontWeight = FontWeight.Medium,
                fontSize = 12.sp
            )
        }
    }

    AnimatedVisibility(
        visible = showHints,
        enter = fadeIn() + slideInVertically(initialOffsetY = { it / 4 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { it / 4 })
    ) {
        Column {
            Spacer(Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 6.dp)
            ) {
                Text(
                    text = "Tip:",
                    color = Color(0xFF0B6B8C).copy(alpha = 0.62f),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )

                Spacer(Modifier.width(6.dp))

                Text(
                    text = "Tap letters to help spell the word",
                    color = Color(0xFF0F172A).copy(alpha = 0.46f),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(10.dp))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(letters) { ch ->
                    LetterChip(
                        letter = ch,
                        onClick = { onAppendLetter(ch) }
                    )
                }
            }
        }
    }
}

@Composable
private fun HandwritingInputPanel(
    modifier: Modifier = Modifier,
    recognizer: DigitalInkRecognizer,
    onRecognized: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val strokes = remember { mutableStateListOf<HandwritingStroke>() }

    var isRecognizing by remember { mutableStateOf(false) }
    var detectedText by rememberSaveable { mutableStateOf("") }
    var recognizeTick by remember { mutableStateOf(0) }

    val cardShape = RoundedCornerShape(28.dp)
    val inkColor = Color(0xFF0F172A)

    LaunchedEffect(recognizeTick) {
        if (strokes.isEmpty()) {
            detectedText = ""
            return@LaunchedEffect
        }

        kotlinx.coroutines.delay(700)

        if (isRecognizing || strokes.isEmpty()) return@LaunchedEffect

        isRecognizing = true

        val recognized = runCatching {
            recognizeInkWord(
                recognizer = recognizer,
                strokes = strokes.toList()
            )
        }.getOrNull().orEmpty()

        detectedText = recognized
        isRecognizing = false
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(320.dp)
            .clip(cardShape)
            .background(Color(0xFFF8FBFD))
    ) {

        HandwritingCanvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            strokes = strokes,
            inkColor = inkColor,
            onStrokeFinished = { recognizeTick++ }
        )

        if (detectedText.isNotBlank()) {
            Text(
                text = "Detected: $detectedText",
                color = Color(0xFF0F172A).copy(alpha = 0.58f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 12.dp, end = 14.dp)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 14.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MinimalCircleOutlineIconButton(
                enabled = strokes.isNotEmpty() && !isRecognizing,
                onClick = {
                    strokes.clear()
                    recognizeTick++
                },
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = "Clear handwriting"
            )

            MinimalCircleOutlineIconButton(
                enabled = strokes.isNotEmpty() && !isRecognizing,
                onClick = {
                    if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex)
                    recognizeTick++
                },
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = "Undo"
            )
        }

        Button(
            onClick = {
                if (strokes.isEmpty()) return@Button

                scope.launch {
                    val already = detectedText.trim()

                    if (already.isNotBlank()) {
                        onRecognized(already)
                        return@launch
                    }

                    isRecognizing = true

                    val recognized = runCatching {
                        recognizeInkWord(
                            recognizer = recognizer,
                            strokes = strokes.toList()
                        )
                    }.getOrNull().orEmpty()

                    detectedText = recognized
                    isRecognizing = false

                    val toSubmit = recognized.trim()
                    if (toSubmit.isNotBlank()) onRecognized(toSubmit)
                }
            },
            enabled = strokes.isNotEmpty() && !isRecognizing,
            shape = RoundedCornerShape(999.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF0B6B8C).copy(alpha = 0.72f),
                contentColor = Color.White,
                disabledContainerColor = Color(0xFF0B6B8C).copy(alpha = 0.32f),
                disabledContentColor = Color.White.copy(alpha = 0.78f)
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 14.dp)
                .height(44.dp)
                .width(120.dp)
        ) {
            Text(
                text = if (isRecognizing) "Reading…" else "Submit",
                fontWeight = FontWeight.SemiBold,
                color = Color.White,
                fontSize = 15.sp
            )

            Spacer(Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Submit",
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun MinimalCircleOutlineIconButton(
    enabled: Boolean,
    onClick: () -> Unit,
    imageVector: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String
) {
    val shape = CircleShape

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(46.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.52f))
            .border(
                width = 1.dp,
                color = Color(0xFF0F172A).copy(alpha = if (enabled) 0.20f else 0.10f),
                shape = shape
            )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = Color(0xFF0F172A).copy(alpha = if (enabled) 0.76f else 0.30f),
            modifier = Modifier.size(19.dp)
        )
    }
}

@Composable
private fun HandwritingCanvas(
    modifier: Modifier = Modifier,
    strokes: MutableList<HandwritingStroke>,
    inkColor: Color,
    strokeWidth: Float = 14f,
    onStrokeFinished: () -> Unit
) {
    var currentStroke by remember { mutableStateOf<HandwritingStroke?>(null) }
    var redrawTick by remember { mutableStateOf(0) }

    androidx.compose.foundation.Canvas(
        modifier = modifier.pointerInput(Unit) {
            detectDragGestures(
                onDragStart = { start ->
                    val now = SystemClock.uptimeMillis()
                    currentStroke = HandwritingStroke(
                        points = mutableListOf(HandwritingPoint(start, now)),
                        path = Path().apply { moveTo(start.x, start.y) }
                    )
                    redrawTick++
                },
                onDrag = { change, _ ->
                    change.consume()

                    val p = change.position
                    val now = SystemClock.uptimeMillis()

                    currentStroke?.let { stroke ->
                        stroke.points.add(HandwritingPoint(p, now))
                        stroke.path.lineTo(p.x, p.y)
                        redrawTick++
                    }
                },
                onDragEnd = {
                    currentStroke?.let { finished ->
                        if (finished.points.size >= 2) {
                            strokes.add(finished)
                            onStrokeFinished()
                        }
                    }
                    currentStroke = null
                    redrawTick++
                },
                onDragCancel = {
                    currentStroke = null
                    redrawTick++
                }
            )
        }
    ) {
        redrawTick

        val paint = Stroke(
            width = strokeWidth,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )

        strokes.forEach { stroke ->
            drawPath(
                path = stroke.path,
                color = inkColor,
                style = paint
            )
        }

        currentStroke?.let { stroke ->
            drawPath(
                path = stroke.path,
                color = inkColor,
                style = paint
            )
        }
    }
}

private data class HandwritingPoint(
    val offset: Offset,
    val timeMs: Long
)

private data class HandwritingStroke(
    val points: MutableList<HandwritingPoint>,
    val path: Path
)

private suspend fun recognizeInkWord(
    recognizer: DigitalInkRecognizer,
    strokes: List<HandwritingStroke>
): String = withContext(Dispatchers.Default) {
    val id = requireNotNull(DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US"))
    val model = DigitalInkRecognitionModel.builder(id).build()
    val conditions = DownloadConditions.Builder().build()

    RemoteModelManager.getInstance()
        .download(model, conditions)
        .await()

    val inkBuilder = Ink.builder()

    for (stroke in strokes) {
        val strokeBuilder = Ink.Stroke.builder()

        stroke.points.forEach { p ->
            strokeBuilder.addPoint(
                Ink.Point.create(
                    p.offset.x,
                    p.offset.y,
                    p.timeMs
                )
            )
        }

        inkBuilder.addStroke(strokeBuilder.build())
    }

    val result = recognizer.recognize(inkBuilder.build()).await()

    result.candidates
        .firstOrNull()
        ?.text
        ?.trim()
        .orEmpty()
}

@Composable
private fun CorrectAnswerSuccessCard(
    completed: Int,
    total: Int,
    showWordMastered: Boolean,
    wordProgressText: String,
    onNextWord: () -> Unit
) {
    val progress = if (total <= 0) {
        0f
    } else {
        (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "correct_progress"
    )

    val cardShape = RoundedCornerShape(28.dp)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 10.dp,
                shape = cardShape,
                clip = false
            ),
        shape = cardShape,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            Box(
//                modifier = Modifier
//                    .size(64.dp)
//                    .clip(CircleShape)
//                    .background(Color(0xFFDDF7E7)),
//                contentAlignment = Alignment.Center
//            ) {
                Image(
                    painter = painterResource(R.drawable.fox_happy),
                    contentDescription = null,
                    modifier = Modifier.size(95.dp)
                )
//            }

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Correct!",
                color = Color(0xFF15803D),
                fontWeight = FontWeight.SemiBold,
                fontSize = 28.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Great job, you're a spelling star!",
                color = Color(0xFF6B7280),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            if (showWordMastered) {
                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Word mastered!",
                    color = Color(0xFF15803D),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            if (wordProgressText.isNotBlank()) {
                Spacer(Modifier.height(10.dp))

                Text(
                    text = wordProgressText,
                    color = Color(0xFF374151),
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    color = Color(0xFF374151),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = "$completed / $total words",
                    color = Color(0xFF15803D),
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
                    .background(Color(0xFFE5E7EB))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(animatedProgress)
                        .height(10.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF34D399))
                )
            }

            Spacer(Modifier.height(26.dp))

            Button(
                onClick = onNextWord,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF0B6B8C),
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Next word",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
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

@Composable
private fun LetterChip(
    letter: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 52.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White.copy(alpha = 0.84f))
            .border(
                width = 1.dp,
                color = Color.White.copy(alpha = 0.72f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.uppercase(),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF0F172A)
        )
    }
}