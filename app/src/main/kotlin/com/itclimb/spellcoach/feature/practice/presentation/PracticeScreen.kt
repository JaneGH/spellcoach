package com.itclimb.spellcoach.feature.practice.presentation

import android.os.SystemClock
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.rounded.Draw
import androidx.compose.material.icons.rounded.Keyboard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.Ink
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.core.designsystem.components.SegmentedOption
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachCard
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachOutlinedTextField
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachPrimaryButton
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachProgressBar
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachScreenContainer
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachSecondaryButton
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachSegmentedControl
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachTopBar
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachTopBarVariant
import com.itclimb.spellcoach.core.designsystem.components.spellCoachScreenHorizontalPadding
import com.itclimb.spellcoach.core.designsystem.motion.SpellCoachMotion
import com.itclimb.spellcoach.core.designsystem.motion.screenEnterSoft
import com.itclimb.spellcoach.core.designsystem.motion.screenExitSoft
import com.itclimb.spellcoach.core.designsystem.theme.SpellCoachThemeExtras
import com.itclimb.spellcoach.core.designsystem.tokens.AppDimensions
import com.itclimb.spellcoach.core.designsystem.tokens.AppElevation
import com.itclimb.spellcoach.core.designsystem.tokens.AppRadius
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing
import com.itclimb.spellcoach.domain.model.isLearnedAtThreshold
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


    val orbInteraction = remember { MutableInteractionSource() }
    val orbScaleAnim = remember { Animatable(1f) }
    val orbOffsetXAnim = remember { Animatable(0f) }

    LaunchedEffect(state.animationHint) {
        when (state.animationHint) {
            PracticeAnimHint.None -> return@LaunchedEffect
            PracticeAnimHint.BounceOk -> {
                orbScaleAnim.snapTo(1f)
                orbScaleAnim.animateTo(
                    1.11f,
                    spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
                orbScaleAnim.animateTo(
                    1f,
                    spring(
                        dampingRatio = Spring.DampingRatioNoBouncy,
                        stiffness = Spring.StiffnessMedium
                    )
                )
            }

            PracticeAnimHint.ShakeWrong -> {
                orbOffsetXAnim.snapTo(0f)
                repeat(4) {
                    orbOffsetXAnim.animateTo(5.5f, tween(44))
                    orbOffsetXAnim.animateTo(-5.5f, tween(44))
                }
                orbOffsetXAnim.animateTo(0f, tween(90))
            }
        }
        kotlinx.coroutines.delay(380)
        viewModel.clearAnimationHint()
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

    SpellCoachScreenContainer {
        Column(modifier = Modifier.fillMaxSize()) {
            val totalWords = state.allWords.size
            val required = state.requiredCorrectAnswers.coerceAtLeast(1)
            val masteredWords = state.allWords.count { it.isLearnedAtThreshold(required) }

            SpellCoachTopBar(
                variant = SpellCoachTopBarVariant.Inner,
                onBack = onBack,
                innerTitle = stringResource(R.string.practice_title),
                innerCaption = stringResource(
                    R.string.practice_progress_format,
                    masteredWords,
                    totalWords
                )
            )

            if (!state.loading && state.sessionComplete && !showCorrectAnswerCard) {
                val totalWords = state.allWords.size
                val masteredWords = state.masteredWordsCount.coerceIn(0, totalWords)
                val stillLearning = (totalWords - masteredWords).coerceAtLeast(0)
                val listFullyMastered = totalWords > 0 && masteredWords >= totalWords

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .spellCoachScreenHorizontalPadding()
                        .padding(vertical = AppSpacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        totalWords <= 0 -> {
                            SpellCoachCard(modifier = Modifier.fillMaxWidth()) {
                                PracticeCompletionEmptyBody(
                                    onPracticeAgain = viewModel::practiceAgain,
                                    onBackToLists = onBack,
                                    onResetProgress = viewModel::resetListProgress
                                )
                            }
                        }

                        listFullyMastered -> {
                            PracticeCompletionListMasteredCard(
                                onReviewMastered = viewModel::practiceAgain,
                                onBackToLists = onBack,
                                onResetProgress = viewModel::resetListProgress
                            )
                        }

                        else -> {
                            PracticeCompletionDailyDoneCard(
                                masteredWords = masteredWords,
                                stillLearningWords = stillLearning,
                                onContinueLearning = viewModel::practiceAgain,
                                onBackToLists = onBack
                            )
                        }
                    }
                }
                return@Column
            }

            AnimatedVisibility(
                visible = showCorrectAnswerCard,
                enter = screenEnterSoft(),
                exit = screenExitSoft()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .spellCoachScreenHorizontalPadding(),
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
                                val cur =
                                    if (curWord.isLearnedAtThreshold(required)) {
                                        required
                                    } else {
                                        curWord.correctCount.coerceIn(0, required)
                                    }
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                enter = screenEnterSoft(),
                exit = screenExitSoft()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .spellCoachScreenHorizontalPadding()
                ) {
                    SpellCoachCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (inputMode == PracticeInputMode.Handwriting) {
                                    Modifier.weight(1f)
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val scheme = MaterialTheme.colorScheme
                            val correctWord = state.words.getOrNull(state.currentIndex)?.text.orEmpty()
                            val spacedCorrectWord = remember(correctWord) {
                                correctWord
                                    .trim()
                                    .uppercase()
                                    .toCharArray()
                                    .joinToString(" ")
                            }

                            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

                            val orbPressed by orbInteraction.collectIsPressedAsState()
                            val orbPressMul by animateFloatAsState(
                                targetValue = if (orbPressed) 0.93f else 1f,
                                animationSpec = SpellCoachMotion.gentleSpring(),
                                label = "orb_press"
                            )
                            Box(
                                modifier = Modifier
                                    .graphicsLayer {
                                        scaleX = orbScaleAnim.value * orbPressMul
                                        scaleY = orbScaleAnim.value * orbPressMul
                                        translationX = orbOffsetXAnim.value
                                    }
                                    .size(AppDimensions.practiceSpeakerOrb)
                                    .shadow(
                                        elevation = AppElevation.level3,
                                        shape = CircleShape,
                                        ambientColor = scheme.primary.copy(
                                            alpha = 0.08f + (if (orbPressed) 0.05f else 0f)
                                        ),
                                        spotColor = scheme.primary.copy(
                                            alpha = 0.12f + (if (orbPressed) 0.05f else 0f)
                                        )
                                    )
                                    .clip(CircleShape)
                                    .background(lerp(scheme.primaryContainer, scheme.surface, 0.12f))
                                    .semantics { role = Role.Button }
                                    .clickable(
                                        interactionSource = orbInteraction,
                                        indication = null,
                                        onClick = viewModel::listen
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Listen",
                                    tint = scheme.primary,
                                    modifier = Modifier.size(AppDimensions.practiceSpeakerIcon)
                                )
                            }

                            Spacer(Modifier.height(AppSpacing.lg))

                            SpellCoachSegmentedControl(
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

                            Spacer(Modifier.height(AppSpacing.md + AppSpacing.xs))

                            val current = state.words.getOrNull(state.currentIndex)
                            if (current != null) {
                                val cur =
                                    if (current.isLearnedAtThreshold(required)) {
                                        required
                                    } else {
                                        current.correctCount.coerceIn(0, required)
                                    }
                                val progressLabel = buildAnnotatedString {
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            color = scheme.onSurfaceVariant.copy(alpha = 0.92f)
                                        )
                                    ) {
                                        append(cur.toString())
                                    }
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Medium,
                                            color = scheme.onSurfaceVariant.copy(alpha = 0.88f)
                                        )
                                    ) {
                                        append(" of ")
                                    }
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.SemiBold,
                                            color = scheme.onSurfaceVariant.copy(alpha = 0.92f)
                                        )
                                    ) {
                                        append(required.toString())
                                    }
                                    withStyle(
                                        SpanStyle(
                                            fontWeight = FontWeight.Medium,
                                            color = scheme.onSurfaceVariant.copy(alpha = 0.88f)
                                        )
                                    ) {
                                        append(" correct")
                                    }
                                }
                                Text(
                                    text = progressLabel,
                                    style = MaterialTheme.typography.bodyMedium
                                )

                                Spacer(Modifier.height(AppSpacing.md))
                            }

                            AnimatedVisibility(
                                visible = showWrongAnswerCard,
                                enter = screenEnterSoft(),
                                exit = screenExitSoft()
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
                                enter = screenEnterSoft(),
                                exit = screenExitSoft()
                            ) {
                                AnimatedContent(
                                    targetState = inputMode,
                                    transitionSpec = {
                                        (
                                            fadeIn(SpellCoachMotion.fadeMedium) + slideInVertically(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                                    stiffness = Spring.StiffnessMediumLow
                                                ),
                                                initialOffsetY = { it / 8 }
                                            )
                                            ).togetherWith(
                                            fadeOut(SpellCoachMotion.fadeTween) + slideOutVertically(
                                                animationSpec = spring(
                                                    dampingRatio = Spring.DampingRatioNoBouncy,
                                                    stiffness = Spring.StiffnessMediumLow
                                                ),
                                                targetOffsetY = { it / 8 }
                                            )
                                        ).using(SizeTransform(clip = false))
                                    },
                                    label = "input_mode_switch"
                                ) { mode ->
                                    when (mode) {
                                        PracticeInputMode.Keyboard -> {
                                            val flowScheme = MaterialTheme.colorScheme
                                            Column(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clip(RoundedCornerShape(AppRadius.xl))
                                                    .background(
                                                        lerp(
                                                            flowScheme.surfaceContainerLow,
                                                            flowScheme.surfaceVariant,
                                                            0.35f
                                                        ).copy(alpha = 0.55f)
                                                    )
                                                    .padding(
                                                        horizontal = AppSpacing.md + AppSpacing.xs,
                                                        vertical = AppSpacing.md
                                                    )
                                            ) {
                                                Box(
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {

                                                    SpellCoachOutlinedTextField(
                                                        value = state.input,
                                                        onValueChange = viewModel::onInputChange,
                                                        placeholder = "Type here",
                                                        modifier = Modifier
                                                            .fillMaxWidth()
                                                            .padding(end = 12.dp)
                                                            .focusRequester(focusRequester),
                                                        height = 82.dp
                                                    )

                                                    Image(
                                                        painter = painterResource(id = R.drawable.fox_neutral),
                                                        contentDescription = null,
                                                        modifier = Modifier
                                                            .align(Alignment.CenterEnd)
                                                            .padding(end = 10.dp, bottom = 15.dp)
                                                            .size(50.dp)
                                                            .graphicsLayer {
                                                                alpha = 0.92f
                                                            }
                                                    )
                                                }
                                                Spacer(Modifier.height(AppSpacing.md))

                                                SpellCoachPrimaryButton(
                                                    text = "Check word",
                                                    onClick = {
                                                        showWrongAnswerCard = false
                                                        viewModel.checkWord()
                                                    },
                                                    leadingIcon = Icons.AutoMirrored.Filled.ArrowForward
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

                    Spacer(Modifier.height(AppSpacing.md))

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
private fun PracticeCompletionDailyDoneCard(
    masteredWords: Int,
    stillLearningWords: Int,
    onContinueLearning: () -> Unit,
    onBackToLists: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    SpellCoachCard(modifier = Modifier.fillMaxWidth()) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Spacer(Modifier.height(AppSpacing.sm))

            Text(
                text = stringResource(R.string.practice_complete_daily_title),
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.headlineSmall,
                color = scheme.onSurface,
                textAlign = TextAlign.Center
            )


            Spacer(Modifier.height(AppSpacing.sm))

            Text(
                text = stringResource(
                    R.string.practice_complete_daily_progress_hint,
                    masteredWords,
                    stillLearningWords
                ),
                style = MaterialTheme.typography.bodyMedium,
                color = scheme.primary.copy(alpha = 0.92f),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            SpellCoachPrimaryButton(
                text = stringResource(R.string.practice_complete_daily_primary),
                onClick = onContinueLearning
            )

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            SpellCoachSecondaryButton(
                text = stringResource(R.string.practice_back_to_lists),
                onClick = onBackToLists
            )
        }
    }
}

@Composable
private fun PracticeCompletionListMasteredCard(
    onReviewMastered: () -> Unit,
    onBackToLists: () -> Unit,
    onResetProgress: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = extras.success.copy(alpha = 0.34f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            extras.success.copy(alpha = 0.14f),
                            extras.success.copy(alpha = 0.08f),
                            scheme.surface.copy(alpha = 0.98f)
                        )
                    )
                )
                .padding(AppSpacing.lg)
        ) {
            Text(
                text = "✦",
                color = extras.success.copy(alpha = 0.28f),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.TopStart)
            )

            Text(
                text = "✦",
                color = extras.success.copy(alpha = 0.22f),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.TopEnd)
            )

            Text(
                text = "•",
                color = extras.success.copy(alpha = 0.24f),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 44.dp, end = 28.dp)
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(extras.success.copy(alpha = 0.14f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🏆",
                        style = MaterialTheme.typography.displaySmall
                    )
                }

                Spacer(Modifier.height(AppSpacing.md))

                Text(
                    text = stringResource(R.string.practice_complete_list_mastered_title),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.headlineSmall,
                    color = extras.success,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

                Text(
                    text = stringResource(R.string.practice_complete_list_mastered_subtitle),
                    style = MaterialTheme.typography.bodyLarge,
                    color = scheme.onSurfaceVariant.copy(alpha = 0.88f),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(AppSpacing.xs))

                Text(
                    text = stringResource(R.string.practice_complete_list_mastered_supporting),
                    style = MaterialTheme.typography.bodyMedium,
                    color = extras.success.copy(alpha = 0.86f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(AppSpacing.lg))

                SpellCoachPrimaryButton(
                    text = stringResource(R.string.practice_complete_list_mastered_primary),
                    onClick = onReviewMastered
                )

                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

                SpellCoachSecondaryButton(
                    text = stringResource(R.string.practice_back_to_lists),
                    onClick = onBackToLists
                )

                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

                TextButton(
                    onClick = onResetProgress,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.practice_reset_progress),
                        color = scheme.error.copy(alpha = 0.82f),
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}
@Composable
private fun PracticeCompletionEmptyBody(
    onPracticeAgain: () -> Unit,
    onBackToLists: () -> Unit,
    onResetProgress: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = stringResource(R.string.practice_completed_title_empty),
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.headlineSmall,
            color = scheme.onSurface,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

        Text(
            text = stringResource(R.string.practice_completed_body_empty),
            style = MaterialTheme.typography.bodyMedium,
            color = scheme.onSurfaceVariant.copy(alpha = 0.86f),
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

        SpellCoachPrimaryButton(
            text = stringResource(R.string.practice_again),
            onClick = onPracticeAgain
        )

        Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

        SpellCoachSecondaryButton(
            text = stringResource(R.string.practice_back_to_lists),
            onClick = onBackToLists
        )

        Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

        TextButton(
            onClick = onResetProgress,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.practice_reset_progress),
                color = scheme.error,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PracticeCompletionCalmAccentDots(color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { i ->
            if (i > 0) Spacer(Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.35f + i * 0.12f))
            )
        }
    }
}

@Composable
private fun PracticeCompletionConfettiStrip(colors: List<Color>) {
    if (colors.isEmpty()) return
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val sizes = listOf(6.dp, 9.dp, 7.dp, 8.dp, 6.dp, 10.dp, 7.dp)
        sizes.forEachIndexed { i, size ->
            if (i > 0) Spacer(Modifier.width(8.dp))
            val c = colors[i % colors.size]
            val shape = if (i % 2 == 0) CircleShape else RoundedCornerShape(3.dp)
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(shape)
                    .background(c)
            )
        }
    }
}

@Composable
private fun WrongAnswerCard(
    spacedCorrectWord: String,
    onTryAgain: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = AppElevation.level2,
                shape = RoundedCornerShape(AppRadius.sheet),
                ambientColor = scheme.error.copy(alpha = 0.08f),
                spotColor = scheme.error.copy(alpha = 0.12f)
            )
            .clip(RoundedCornerShape(AppRadius.sheet))
            .background(scheme.errorContainer.copy(alpha = 0.38f))
            .border(
                width = 1.dp,
                color = scheme.error.copy(alpha = 0.22f),
                shape = RoundedCornerShape(AppRadius.sheet)
            )
            .padding(horizontal = AppSpacing.lg + AppSpacing.xs, vertical = AppSpacing.lg + AppSpacing.xs),
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
            color = scheme.error,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(AppSpacing.sm))

        Text(
            text = "The correct spelling is:",
            color = scheme.onErrorContainer.copy(alpha = 0.82f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(AppSpacing.sm))

        Text(
            text = spacedCorrectWord,
            color = scheme.error,
            fontWeight = FontWeight.SemiBold,
            fontSize = 20.sp,
            letterSpacing = 6.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

        Text(
            text = "You’re doing great. Try it once more.",
            color = scheme.onErrorContainer.copy(alpha = 0.78f),
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(AppSpacing.lg))

        SpellCoachPrimaryButton(
            text = "Try again",
            onClick = onTryAgain,
            leadingIcon = Icons.Filled.Refresh
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
    val scheme = MaterialTheme.colorScheme
    OutlinedButton(
        onClick = onShowHints,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppRadius.lg),
        border = BorderStroke(
            1.dp,
            if (nudgeHints) {
                scheme.tertiary.copy(alpha = 0.26f)
            } else {
                scheme.outlineVariant.copy(alpha = 0.32f)
            }
        )
    ) {
        Text(
            text = "Letter hints shown",
            color = scheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = AppSpacing.sm + AppSpacing.xs)
        )
    }

    if (nudgeHints) {
        Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

        TextButton(
            onClick = onShowHints,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Tip: try a hint to get unstuck",
                color = scheme.tertiary,
                fontWeight = FontWeight.Medium,
                style = MaterialTheme.typography.labelMedium
            )
        }
    }

    AnimatedVisibility(
        visible = showHints,
        enter = screenEnterSoft(),
        exit = screenExitSoft()
    ) {
        Column {
            Spacer(Modifier.height(AppSpacing.sm))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = AppSpacing.sm + AppSpacing.xs)
            ) {
                Text(
                    text = "Tip:",
                    color = scheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(Modifier.width(AppSpacing.sm + AppSpacing.xs))

                Text(
                    text = "Tap letters to help spell the word",
                    color = scheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AppSpacing.md)
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

    val scheme = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(AppRadius.glassCard)
    val inkColor = scheme.onSurface

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
            .height(AppDimensions.handwritingPanelMinHeight)
            .clip(cardShape)
            .background(scheme.surfaceVariant.copy(alpha = 0.34f))
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
                color = scheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
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
            shape = RoundedCornerShape(AppRadius.lg),
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = AppElevation.level2,
                pressedElevation = AppElevation.level1,
                disabledElevation = AppElevation.level0
            ),
            colors = ButtonDefaults.buttonColors(
                containerColor = lerp(
                    scheme.primary,
                    scheme.primaryContainer,
                    0.22f
                ),
                contentColor = scheme.onPrimary,
                disabledContainerColor = scheme.primary.copy(alpha = 0.38f),
                disabledContentColor = scheme.onPrimary.copy(alpha = 0.65f)
            ),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = AppSpacing.sm + AppSpacing.md, bottom = AppSpacing.sm + AppSpacing.md)
                .height(AppDimensions.handwritingSubmitHeight)
                .width(AppDimensions.handwritingSubmitWidth)
        ) {
            Text(
                text = if (isRecognizing) "Reading…" else "Submit",
                fontWeight = FontWeight.SemiBold,
                color = scheme.onPrimary,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(Modifier.width(AppSpacing.sm))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = "Submit",
                tint = scheme.onPrimary,
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
    val scheme = MaterialTheme.colorScheme

    IconButton(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .size(46.dp)
            .shadow(
                elevation = AppElevation.level1,
                shape = shape,
                ambientColor = scheme.primary.copy(alpha = 0.06f),
                spotColor = scheme.primary.copy(alpha = 0.08f)
            )
            .clip(shape)
            .background(scheme.surface.copy(alpha = 0.94f))
            .border(
                width = 1.dp,
                color = scheme.outlineVariant.copy(alpha = if (enabled) 0.18f else 0.12f),
                shape = shape
            )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = scheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.45f),
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
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current
    val progress = if (total <= 0) {
        0f
    } else {
        (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }

    SpellCoachCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.sm + AppSpacing.xs),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Image(
                    painter = painterResource(R.drawable.fox_happy),
                    contentDescription = null,
                    modifier = Modifier.size(95.dp)
                )

            Spacer(Modifier.height(5.dp))

            Text(
                text = "Correct!",
                color = extras.success,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            Text(
                text = "Great job, you're a spelling star!",
                color = scheme.onSurfaceVariant.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            if (showWordMastered) {
                Spacer(Modifier.height(AppSpacing.md))

                Text(
                    text = "Word mastered!",
                    color = extras.success,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center
                )
            }

            if (wordProgressText.isNotBlank()) {
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

                Text(
                    text = wordProgressText,
                    color = scheme.onSurfaceVariant.copy(alpha = 0.88f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(Modifier.height(AppSpacing.xxl))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Progress",
                    color = scheme.onSurfaceVariant.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = "$completed / $total words",
                    color = extras.success,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            SpellCoachProgressBar(
                progress = progress,
                fillColor = extras.progressFill
            )

            Spacer(Modifier.height(AppSpacing.xl + AppSpacing.sm))

            SpellCoachPrimaryButton(
                text = "Next word",
                onClick = onNextWord,
                leadingIcon = Icons.AutoMirrored.Filled.ArrowForward
            )
        }
    }
}

@Composable
private fun LetterChip(
    letter: String,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .size(
                width = AppDimensions.letterChipMinSize,
                height = AppDimensions.letterChipMinSize
            )
            .shadow(
                elevation = AppElevation.level1,
                shape = RoundedCornerShape(AppRadius.lg),
                ambientColor = scheme.primary.copy(alpha = 0.04f),
                spotColor = scheme.primary.copy(alpha = 0.06f)
            )
            .clip(RoundedCornerShape(AppRadius.lg))
            .background(scheme.surface)
            .border(
                width = 1.dp,
                color = scheme.outlineVariant.copy(alpha = 0.18f),
                shape = RoundedCornerShape(AppRadius.lg)
            )
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = letter.uppercase(),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            color = scheme.onSurface
        )
    }
}