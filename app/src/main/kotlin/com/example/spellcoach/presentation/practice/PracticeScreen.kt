package com.example.spellcoach.presentation.practice

import android.os.SystemClock
import android.os.Build
import android.graphics.Shader
import android.graphics.RenderEffect as AndroidRenderEffect
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
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
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
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.presentation.components.LearningCard
import com.example.spellcoach.presentation.components.PrimaryButton
import com.example.spellcoach.presentation.components.SpellCoachTopBar
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
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    val recognizer = remember {
        val id = requireNotNull(DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US")) {
            "Digital ink model identifier missing for en-US"
        }
        val model = DigitalInkRecognitionModel.builder(id).build()
        DigitalInkRecognition.getClient(
            DigitalInkRecognizerOptions.builder(model).build()
        )
    }

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
            inputMode == PracticeInputMode.Keyboard &&
            !showWrongAnswerCard &&
            !showCorrectAnswerCard
        ) {
            kotlinx.coroutines.delay(60)

            runCatching {
                focusRequester.requestFocus()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8FAFF))
    ) {
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

        if (!state.loading && state.words.isEmpty() && !showCorrectAnswerCard) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "All words mastered!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp,
                        color = Color(0xFF0F172A),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Great job. You can reset progress or add more words.",
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp,
                        color = Color(0xFF64748B),
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(22.dp))
                    PrimaryButton(
                        text = "Reset Progress",
                        onClick = viewModel::resetListProgress,
                        containerColor = Color(0xFF0B6B8C)
                    )
                    Spacer(Modifier.height(10.dp))
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(text = "Back to Lists", fontWeight = FontWeight.Bold)
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
                val total = totalWords.coerceAtLeast(0)
                val completed = masteredWords.coerceIn(0, total)
                CorrectAnswerSuccessCard(
                    completed = completed,
                    total = total,
                    showWordMastered = state.wordJustMastered,
                    wordProgressText = run {
                        val curWord = state.words.getOrNull(state.currentIndex)
                        if (curWord == null) "" else {
                            val cur = curWord.correctCount.coerceIn(0, required)
                            "Progress: $cur / $required"
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
                                .size(70.dp)
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
                        Spacer(Modifier.height(5.dp))
//                        Text(
//                            text = "LISTEN",
//                            color = Color(0xFF0B6B8C),
//                            fontWeight = FontWeight.Bold,
//                            fontSize = 12.sp
//                        )
//                        Spacer(Modifier.height(10.dp))

                        SingleChoiceSegmentedButtonRow(
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            SegmentedButton(
                                selected = inputMode == PracticeInputMode.Keyboard,
                                onClick = { inputMode = PracticeInputMode.Keyboard },
                                shape = RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp)
                            ) {
                                Text(text = "Keyboard", fontWeight = FontWeight.SemiBold)
                            }
                            SegmentedButton(
                                selected = inputMode == PracticeInputMode.Handwriting,
                                onClick = { inputMode = PracticeInputMode.Handwriting },
                                shape = RoundedCornerShape(topEnd = 12.dp, bottomEnd = 12.dp)
                            ) {
                                Text(text = "Handwriting", fontWeight = FontWeight.SemiBold)
                            }
                        }

                        Spacer(Modifier.height(5.dp))

                        val current = state.words.getOrNull(state.currentIndex)
                        val required = state.requiredCorrectAnswers.coerceAtLeast(1)
                        if (current != null) {
                            val cur = current.correctCount.coerceIn(0, required)
                            Text(
                                text = "Word Progress: $cur / $required",
                                color = Color(0xFF475569),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                            Spacer(Modifier.height(5.dp))
                        }

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
                            AnimatedContent(
                                targetState = inputMode,
                                transitionSpec = {
                                    (fadeIn() + slideInVertically(initialOffsetY = { it / 8 }))
                                        .togetherWith(fadeOut() + slideOutVertically(targetOffsetY = { it / 8 }))
                                        .using(SizeTransform(clip = false))
                                },
                                label = "input_mode_switch"
                            ) { mode ->
                                when (mode) {
                                    PracticeInputMode.Keyboard -> {
                                        Column {
                                            OutlinedTextField(
                                                value = state.input,
                                                onValueChange = viewModel::onInputChange,

                                                keyboardOptions = KeyboardOptions(
                                                    imeAction = ImeAction.Next
                                                ),

                                                keyboardActions = KeyboardActions(
                                                    onNext = {
                                                        showWrongAnswerCard = false
                                                        viewModel.checkWord()
                                                    }
                                                ),

                                                placeholder = {
                                                    Text(
                                                        text = "Type here",
                                                        color = Color(0xFFCBD5E1),
                                                        fontSize = 25.sp,
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
                                                onClick = {
                                                    showWrongAnswerCard = false
                                                    viewModel.checkWord()
                                                },
                                                containerColor = Color(0xFF0B6B8C)
                                            )
                                        }
                                    }

                                    PracticeInputMode.Handwriting -> {
                                        HandwritingInputPanel(
                                            modifier = Modifier.fillMaxWidth(),
                                            recognizer = recognizer,
                                            onRecognized = { recognized ->
                                                // Reuse existing ViewModel flow.
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

    val cardShape = RoundedCornerShape(32.dp)
    val border = Color(0xFF0F172A).copy(alpha = 0.08f)
    val inkColor = Color(0xFF0F172A)
    val uiBlack = Color(0xFF0F172A)
    val subtleText = Color(0xFF111827).copy(alpha = 0.68f)

    LaunchedEffect(recognizeTick) {
        if (strokes.isEmpty()) {
            detectedText = ""
            return@LaunchedEffect
        }
        // Debounce after the last finished stroke / edit.
        kotlinx.coroutines.delay(700)
        if (isRecognizing || strokes.isEmpty()) return@LaunchedEffect

        isRecognizing = true
        val recognized = runCatching {
            recognizeInkWord(recognizer = recognizer, strokes = strokes.toList())
        }.getOrNull().orEmpty()
        detectedText = recognized
        isRecognizing = false
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(380.dp)
            .shadow(elevation = 16.dp, shape = cardShape, clip = false)
            .clip(cardShape)
            .background(Color.White)
            .border(width = 1.dp, color = border, shape = cardShape)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        renderEffect = AndroidRenderEffect.createBlurEffect(
                            18f,
                            18f,
                            Shader.TileMode.CLAMP
                        ).asComposeRenderEffect()
                    }
                }
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            Color(0xFFBFD6FF).copy(alpha = 0.20f),
                            Color(0xFFFFFFFF).copy(alpha = 0.60f),
                            Color(0xFFF2F7FF).copy(alpha = 0.35f)
                        )
                    )
                )
        )

        // максимально wide/tall writing surface with minimal padding
        HandwritingCanvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            strokes = strokes,
            inkColor = inkColor,
            onStrokeFinished = { recognizeTick++ }
        )

        Column(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 12.dp, end = 14.dp),
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = "Detected text:",
                color = subtleText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.2.sp
            )
            if (detectedText.isNotBlank()) {
                Text(
                    text = detectedText,
                    color = uiBlack.copy(alpha = 0.82f),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1
                )
            }
        }

        // Bottom-left: circular outline buttons (Apple HIG minimal)
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

        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 14.dp, bottom = 14.dp)
        ) {
            val pillShape = RoundedCornerShape(999.dp)
            val submitColor = Color(0xFF0B6B8C)

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
                            recognizeInkWord(recognizer = recognizer, strokes = strokes.toList())
                        }.getOrNull().orEmpty()
                        detectedText = recognized
                        isRecognizing = false
                        val toSubmit = recognized.trim()
                        if (toSubmit.isNotBlank()) onRecognized(toSubmit)
                    }
                },
                enabled = strokes.isNotEmpty() && !isRecognizing,
                shape = pillShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = submitColor,
                    contentColor = Color.White,
                    disabledContainerColor = submitColor.copy(alpha = 0.45f),
                    disabledContentColor = Color.White
                ),
                contentPadding = ButtonDefaults.ContentPadding,
                modifier = Modifier
                    .height(44.dp)
                    .width(120.dp)
                    .clip(pillShape)
            ) {
                Text(
                    text = if (isRecognizing) "Reading…" else "Submit",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 15.sp,
                    letterSpacing = 0.1.sp
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
            .size(48.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.60f))
            .border(1.dp, Color(0xFF0F172A).copy(alpha = 0.28f), shape)
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = Color(0xFF0F172A).copy(alpha = if (enabled) 0.84f else 0.35f),
            modifier = Modifier.size(20.dp)
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
        modifier = modifier
            .pointerInput(Unit) {
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
    RemoteModelManager.getInstance().download(model, conditions).await()

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
    val ink = inkBuilder.build()

    val result = recognizer.recognize(ink).await()
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

            if (showWordMastered) {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = "Word mastered!",
                    color = titleGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center
                )
            }

            if (wordProgressText.isNotBlank()) {
                Spacer(Modifier.height(10.dp))
                Text(
                    text = wordProgressText,
                    color = Color(0xFF374151),
                    fontWeight = FontWeight.SemiBold,
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

