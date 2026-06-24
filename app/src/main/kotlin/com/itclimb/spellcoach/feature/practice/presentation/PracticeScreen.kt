package com.itclimb.spellcoach.feature.practice.presentation

import android.annotation.SuppressLint
import android.os.SystemClock
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognition
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModel
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognitionModelIdentifier
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizer
import com.google.mlkit.vision.digitalink.recognition.DigitalInkRecognizerOptions
import com.google.mlkit.vision.digitalink.recognition.Ink
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
import com.itclimb.spellcoach.core.designsystem.tokens.AppBorder
import com.itclimb.spellcoach.core.designsystem.tokens.AppDimensions
import com.itclimb.spellcoach.core.designsystem.tokens.AppElevation
import com.itclimb.spellcoach.core.designsystem.tokens.AppIconSize
import com.itclimb.spellcoach.core.designsystem.tokens.AppRadius
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing
import com.itclimb.spellcoach.domain.model.isLearnedAtThreshold
import com.itclimb.spellcoach.domain.practice.SpellingDisplayUnit
import com.itclimb.spellcoach.domain.practice.SpellingFeedback
import com.itclimb.spellcoach.domain.practice.SpellingLetterKind
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun PracticeScreen(
    onBack: () -> Unit,
    viewModel: PracticeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle(
        lifecycleOwner = LocalLifecycleOwner.current
    )

    if (!state.listIdValid) {
        SpellCoachScreenContainer {
            Column(modifier = Modifier.fillMaxSize()) {
                SpellCoachTopBar(
                    variant = SpellCoachTopBarVariant.Inner,
                    onBack = onBack,
                    innerTitle = stringResource(R.string.practice_title),
                    innerCaption = null,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(AppSpacing.xxl),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.manage_words_invalid_list),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        return
    }

    if (state.sessionWriteBlocked) {
        SpellCoachScreenContainer {
            Column(modifier = Modifier.fillMaxSize()) {
                SpellCoachTopBar(
                    variant = SpellCoachTopBarVariant.Inner,
                    onBack = onBack,
                    innerTitle = stringResource(R.string.practice_title),
                    innerCaption = null,
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .spellCoachScreenHorizontalPadding()
                        .padding(vertical = AppSpacing.xxl),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(AppSpacing.lg),
                    ) {
                        Text(
                            text = stringResource(R.string.practice_stale_session_message),
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        SpellCoachPrimaryButton(
                            text = stringResource(R.string.practice_back_to_lists),
                            onClick = onBack,
                        )
                    }
                }
            }
        }
        return
    }

    var showCorrectAnswerCard by rememberSaveable { mutableStateOf(false) }
    val showWrongAnswerCard = state.feedbackCorrect == false
    val focusRequester = remember { FocusRequester() }
    var inputMode by rememberSaveable { mutableStateOf(PracticeInputMode.Keyboard) }

    val recognizer = remember {
        val id = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US")
            ?: return@remember null

        val model = DigitalInkRecognitionModel.builder(id).build()
        DigitalInkRecognition.getClient(
            DigitalInkRecognizerOptions.builder(model).build()
        )
    }

    DisposableEffect(recognizer) {
        onDispose {
            recognizer?.close()
        }
    }

    val handwritingAvailable = recognizer != null
    val inkModelDownloader = remember(recognizer) { InkModelDownloader() }

    LaunchedEffect(handwritingAvailable, inputMode) {
        if (handwritingAvailable && inputMode == PracticeInputMode.Handwriting) {
            runCatching { inkModelDownloader.ensureInkModelDownloaded() }
        }
    }

    LaunchedEffect(handwritingAvailable) {
        if (!handwritingAvailable && inputMode == PracticeInputMode.Handwriting) {
            inputMode = PracticeInputMode.Keyboard
        }
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


    LaunchedEffect(state.currentIndex, state.feedbackCorrect) {
        when (state.feedbackCorrect) {
            true -> showCorrectAnswerCard = true
            false -> showCorrectAnswerCard = false
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

            val showAllMasteredExcludedEmpty =
                !state.loading &&
                    state.allWords.isNotEmpty() &&
                    state.words.isEmpty() &&
                    state.excludeMasteredWords &&
                    !state.includeMasteredInSession &&
                    !state.sessionComplete

            if (showAllMasteredExcludedEmpty) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .spellCoachScreenHorizontalPadding()
                        .padding(vertical = AppSpacing.lg),
                    contentAlignment = Alignment.Center
                ) {
                    PracticeAllMasteredExcludedCard(
                        onPracticeMastered = viewModel::practiceMasteredWords,
                        onBackToLists = onBack,
                        onResetProgress = viewModel::resetListProgress
                    )
                }
                return@Column
            }

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
                    if (listFullyMastered) {
                        PracticeCompletionListMasteredCard(
                            onReviewMastered = viewModel::practiceAgain,
                            onBackToLists = onBack,
                            onResetProgress = viewModel::resetListProgress
                        )
                    } else {
                        PracticeCompletionDailyDoneCard(
                            masteredWords = masteredWords,
                            stillLearningWords = stillLearning,
                            onContinueLearning = viewModel::practiceAgain,
                            onBackToLists = onBack
                        )
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
                                stringResource(
                                    R.string.practice_word_progress_label_format,
                                    cur,
                                    required
                                )
                            }
                        },
                        answerSoundsEnabled = state.answerSoundsEnabled,
                        onAnswerSound = viewModel::playAnswerSuccessSound,
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
                                if (
                                    inputMode == PracticeInputMode.Handwriting ||
                                    showWrongAnswerCard
                                ) {
                                    Modifier.weight(1f)
                                } else {
                                    Modifier
                                }
                            )
                    ) {
                        Column(
                            modifier = if (showWrongAnswerCard) {
                                Modifier.fillMaxSize()
                            } else {
                                Modifier.fillMaxWidth()
                            },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                                    contentDescription = stringResource(R.string.content_desc_listen),
                                    tint = scheme.primary,
                                    modifier = Modifier.size(AppDimensions.practiceSpeakerIcon)
                                )
                            }

                            Spacer(Modifier.height(AppSpacing.lg))

                            if (handwritingAvailable) {
                                SpellCoachSegmentedControl(
                                    options = listOf(
                                        SegmentedOption(
                                            stringResource(R.string.input_mode_keyboard),
                                            Icons.Rounded.Keyboard
                                        ),
                                        SegmentedOption(
                                            stringResource(R.string.input_mode_handwriting),
                                            Icons.Rounded.Draw
                                        )
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
                            }

                            Spacer(Modifier.height(AppSpacing.md + AppSpacing.xs))

                            val current = state.words.getOrNull(state.currentIndex)
                            if (current != null) {
                                val cur =
                                    if (current.isLearnedAtThreshold(required)) {
                                        required
                                    } else {
                                        current.correctCount.coerceIn(0, required)
                                    }
                                Text(
                                    text = stringResource(
                                        R.string.practice_word_progress_format,
                                        cur,
                                        required
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = scheme.onSurfaceVariant.copy(alpha = 0.9f)
                                )

                                Spacer(Modifier.height(AppSpacing.md))
                            }

                            AnimatedVisibility(
                                visible = showWrongAnswerCard,
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxWidth(),
                                enter = screenEnterSoft(),
                                exit = screenExitSoft()
                            ) {
                                WrongAnswerCard(
                                    spacedCorrectWord = spacedCorrectWord,
                                    spellingFeedback = state.spellingFeedback,
                                    answerSoundsEnabled = state.answerSoundsEnabled,
                                    onAnswerSound = viewModel::playAnswerRetrySound,
                                    onTryAgain = {
                                        viewModel.onInputChange("")
                                        viewModel.clearFeedback()
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
                                                BoxWithConstraints(
                                                    modifier = Modifier.fillMaxWidth()
                                                ) {
                                                    val inlineMascot =
                                                        (maxWidth * AppDimensions.mascotInlineWidthFraction).coerceIn(
                                                            AppDimensions.mascotInlineMin,
                                                            AppDimensions.mascotInlineMax
                                                        )
                                                    Box(modifier = Modifier.fillMaxWidth()) {

                                                        SpellCoachOutlinedTextField(
                                                            value = state.input,
                                                            onValueChange = viewModel::onInputChange,
                                                            keyboardOptions = KeyboardOptions(
                                                                keyboardType = KeyboardType.Password,
                                                                autoCorrectEnabled = false,
                                                                capitalization = KeyboardCapitalization.None
                                                            ),
                                                            placeholder = stringResource(R.string.practice_placeholder_type),
                                                            modifier = Modifier
                                                                .fillMaxWidth()
                                                                .padding(end = AppSpacing.sm + AppSpacing.xs)
                                                                .focusRequester(focusRequester),
                                                            height = AppDimensions.practiceKeyboardInputFieldHeight
                                                        )

                                                        Image(
                                                            painter = painterResource(id = R.drawable.fox_neutral),
                                                            contentDescription = stringResource(R.string.content_desc_fox_neutral),
                                                            modifier = Modifier
                                                                .align(Alignment.CenterEnd)
                                                                .padding(
                                                                    end = AppSpacing.sm + AppSpacing.xxs,
                                                                    bottom = AppSpacing.md + AppSpacing.xs
                                                                )
                                                                .size(inlineMascot)
                                                                .graphicsLayer {
                                                                    alpha = 0.92f
                                                                }
                                                        )
                                                    }
                                                }
                                                Spacer(Modifier.height(AppSpacing.md))

                                                SpellCoachPrimaryButton(
                                                    text = stringResource(R.string.practice_check_word),
                                                    onClick = viewModel::checkWord,
                                                    enabled = !state.isCheckingWord &&
                                                        state.feedbackCorrect == null,
                                                    leadingIcon = Icons.AutoMirrored.Filled.ArrowForward
                                                )
                                            }
                                        }

                                        PracticeInputMode.Handwriting -> {
                                            val inkRecognizer = recognizer
                                            if (inkRecognizer != null) {
                                                HandwritingInputPanel(
                                                    modifier = Modifier.fillMaxSize(),
                                                    recognizer = inkRecognizer,
                                                    inkModelDownloader = inkModelDownloader,
                                                    onRecognized = { recognized ->
                                                        viewModel.onInputChange(recognized)
                                                        viewModel.checkWord()
                                                    }
                                                )
                                            } else {
                                                HandwritingUnavailablePanel(
                                                    modifier = Modifier.fillMaxSize()
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(AppSpacing.md))

                    if (
                        inputMode == PracticeInputMode.Keyboard &&
                        state.hintsEnabled
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

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun PracticeCompletionDailyDoneCard(
    masteredWords: Int,
    stillLearningWords: Int,
    onContinueLearning: () -> Unit,
    onBackToLists: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current

    val mascotAlpha = remember { Animatable(0f) }
    val mascotScale = remember { Animatable(0.96f) }

    LaunchedEffect(Unit) {
        launch {
            mascotAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 320)
            )
        }
        launch {
            mascotScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            )
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppRadius.glassCard),
        colors = CardDefaults.cardColors(
            containerColor = scheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.level0)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            extras.success.copy(alpha = 0.045f),
                            scheme.surface,
                            scheme.surface
                        )
                    )
                )
                .border(
                    width = AppBorder.hairline,
                    color = scheme.outlineVariant.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(AppRadius.glassCard)
                )
                .padding(AppSpacing.lg)
        ) {
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val ring =
                    (maxWidth * AppDimensions.mascotCompletionDailyRingWidthFraction).coerceIn(
                        AppDimensions.mascotCompletionDailyRingMin,
                        AppDimensions.mascotCompletionDailyRingMax
                    )
                val image = ring * AppDimensions.mascotCompletionDailyImageOfRing
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(ring),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.fox_practice_completed_pose),
                            contentDescription = null,
                            modifier = Modifier
                                .size(image)
                                .graphicsLayer {
                                    alpha = mascotAlpha.value
                                    scaleX = mascotScale.value
                                    scaleY = mascotScale.value
                                }
                        )
                    }

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
                    color = scheme.onSurfaceVariant.copy(alpha = 0.82f),
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(AppSpacing.lg))

                SpellCoachSecondaryButton(
                    text = stringResource(R.string.practice_complete_daily_primary),
                    onClick = onContinueLearning
                )

                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

                TextButton(
                    onClick = onBackToLists,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = stringResource(R.string.practice_back_to_lists),
                        color = scheme.onSurfaceVariant.copy(alpha = 0.86f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            }
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun PracticeAllMasteredExcludedCard(
    onPracticeMastered: () -> Unit,
    onBackToLists: () -> Unit,
    onResetProgress: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current

    SpellCoachCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.lg, vertical = AppSpacing.lg),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(R.drawable.fox_practice_completed_all),
                contentDescription = null,
                modifier = Modifier.size(
                    AppDimensions.mascotCompletionMasteredRingMin *
                        AppDimensions.mascotCompletionMasteredImageOfRing
                )
            )

            Spacer(Modifier.height(AppSpacing.md))

            Text(
                text = stringResource(R.string.practice_all_mastered_empty_title),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.headlineSmall,
                color = extras.success,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(AppSpacing.lg))

            SpellCoachPrimaryButton(
                text = stringResource(R.string.practice_all_mastered_empty_primary),
                onClick = onPracticeMastered
            )

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            SpellCoachSecondaryButton(
                text = stringResource(R.string.practice_reset_progress),
                onClick = onResetProgress
            )

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            SpellCoachSecondaryButton(
                text = stringResource(R.string.practice_back_to_lists),
                onClick = onBackToLists
            )
        }
    }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun PracticeCompletionListMasteredCard(
    onReviewMastered: () -> Unit,
    onBackToLists: () -> Unit,
    onResetProgress: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current

    val mascotScale = remember { Animatable(0.86f) }
    val mascotAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            mascotAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 350)
            )
        }
        launch {
            mascotScale.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "mastered_completion_motion")

    val mascotBounce by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -7f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mascot_bounce"
    )

    val sparkleAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 950),
            repeatMode = RepeatMode.Reverse
        ),
        label = "sparkle_alpha"
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(AppRadius.glassCard),
        colors = CardDefaults.cardColors(
            containerColor = extras.success.copy(alpha = 0.34f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = AppElevation.level0)
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
            BoxWithConstraints(Modifier.fillMaxWidth()) {
                val ring =
                    (maxWidth * AppDimensions.mascotCompletionMasteredRingWidthFraction).coerceIn(
                        AppDimensions.mascotCompletionMasteredRingMin,
                        AppDimensions.mascotCompletionMasteredRingMax
                    )
                val image = ring * AppDimensions.mascotCompletionMasteredImageOfRing
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier.size(ring),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "✦",
                            color = extras.success.copy(alpha = sparkleAlpha * 0.55f),
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .graphicsLayer {
                                    translationY = -mascotBounce * 0.35f
                                }
                        )

                        Text(
                            text = "✦",
                            color = scheme.primary.copy(alpha = sparkleAlpha * 0.45f),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .graphicsLayer {
                                    translationY = mascotBounce * 0.45f
                                }
                        )

                        Text(
                            text = "•",
                            color = extras.success.copy(alpha = sparkleAlpha * 0.45f),
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = AppSpacing.sm)
                                .graphicsLayer {
                                    translationY = mascotBounce * 0.25f
                                }
                        )

                        Text(
                            text = "•",
                            color = scheme.primary.copy(alpha = sparkleAlpha * 0.35f),
                            style = MaterialTheme.typography.titleLarge,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = AppSpacing.xs + AppSpacing.xxs)
                                .graphicsLayer {
                                    translationY = -mascotBounce * 0.3f
                                }
                        )

                        Image(
                            painter = painterResource(R.drawable.fox_practice_completed_all),
                            contentDescription = null,
                            modifier = Modifier
                                .size(image)
                                .graphicsLayer {
                                    alpha = mascotAlpha.value
                                    scaleX = mascotScale.value
                                    scaleY = mascotScale.value
                                    translationY = mascotBounce
                                }
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
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun WrongAnswerCard(
    spacedCorrectWord: String,
    spellingFeedback: SpellingFeedback?,
    answerSoundsEnabled: Boolean,
    onAnswerSound: () -> Unit,
    onTryAgain: () -> Unit
) {
    LaunchedEffect(Unit) {
        if (answerSoundsEnabled) {
            onAnswerSound()
        }
    }
    val scheme = MaterialTheme.colorScheme
    val scrollState = rememberScrollState()
    val cardShape = RoundedCornerShape(AppRadius.sheet)
    val cardModifier = Modifier
        .fillMaxSize()
        .shadow(
            elevation = AppElevation.level2,
            shape = cardShape,
            ambientColor = scheme.error.copy(alpha = 0.08f),
            spotColor = scheme.error.copy(alpha = 0.12f)
        )
        .clip(cardShape)
        .background(scheme.errorContainer.copy(alpha = 0.38f))
        .border(
            width = AppBorder.hairline,
            color = scheme.error.copy(alpha = 0.22f),
            shape = cardShape
        )

    Column(modifier = Modifier.fillMaxSize()) {
        BoxWithConstraints(
            modifier = cardModifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            val wrongCardMascot =
                (maxWidth * AppDimensions.mascotWrongCardWidthFraction).coerceIn(
                    AppDimensions.mascotWrongCardMin,
                    AppDimensions.mascotWrongCardMax
                )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(
                        horizontal = AppSpacing.lg + AppSpacing.xs,
                        vertical = AppSpacing.lg + AppSpacing.xs
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.fox_supportive),
                    contentDescription = stringResource(R.string.content_desc_fox_supportive),
                    modifier = Modifier.size(wrongCardMascot)
                )

                Spacer(Modifier.height(AppSpacing.sm))

                Text(
                    text = stringResource(R.string.practice_wrong_title),
                    color = scheme.error,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(AppSpacing.sm))

                Text(
                    text = stringResource(R.string.practice_wrong_subtitle),
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

                if (spellingFeedback != null && !spellingFeedback.isCorrect) {
                    Spacer(Modifier.height(AppSpacing.md))

                    Text(
                        text = stringResource(R.string.practice_wrong_your_attempt),
                        color = scheme.onErrorContainer.copy(alpha = 0.82f),
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(AppSpacing.sm))

                    HighlightedAttemptText(
                        units = spellingFeedback.displayUnits,
                        modifier = Modifier.fillMaxWidth()
                    )

                    val missingSummary = formatLetterListSummary(
                        letters = spellingFeedback.missingLetters,
                        singularRes = R.string.practice_wrong_missing_letters,
                        pluralRes = R.string.practice_wrong_missing_letters_plural
                    )
                    val extraSummary = formatLetterListSummary(
                        letters = spellingFeedback.extraLetters,
                        singularRes = R.string.practice_wrong_extra_letters,
                        pluralRes = R.string.practice_wrong_extra_letters_plural
                    )

                    if (missingSummary != null) {
                        Spacer(Modifier.height(AppSpacing.sm))
                        Text(
                            text = missingSummary,
                            color = scheme.tertiary,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    if (extraSummary != null) {
                        Spacer(Modifier.height(AppSpacing.xs))
                        Text(
                            text = extraSummary,
                            color = scheme.error.copy(alpha = 0.9f),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

                Text(
                    text = stringResource(R.string.practice_wrong_encouragement),
                    color = scheme.onErrorContainer.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(Modifier.height(AppSpacing.md))

        SpellCoachPrimaryButton(
            text = stringResource(R.string.practice_try_again),
            onClick = onTryAgain,
            leadingIcon = Icons.Filled.Refresh,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HighlightedAttemptText(
    units: List<SpellingDisplayUnit>,
    modifier: Modifier = Modifier
) {
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current

    Text(
        text = buildAnnotatedString {
            units.forEach { unit ->
                when (unit) {
                    is SpellingDisplayUnit.Letter -> {
                        val style = when (unit.kind) {
                            SpellingLetterKind.Correct -> SpanStyle(color = scheme.onErrorContainer)
                            SpellingLetterKind.WrongSubstitution -> SpanStyle(
                                color = scheme.error,
                                background = scheme.error.copy(alpha = 0.14f),
                                fontWeight = FontWeight.SemiBold
                            )
                            SpellingLetterKind.Extra -> SpanStyle(
                                color = scheme.error,
                                background = scheme.error.copy(alpha = 0.22f),
                                fontWeight = FontWeight.SemiBold,
                                textDecoration = TextDecoration.LineThrough
                            )
                        }
                        withStyle(style) { append(unit.char) }
                    }
                    is SpellingDisplayUnit.Missing -> {
                        withStyle(
                            SpanStyle(
                                color = extras.success.copy(alpha = 0.95f),
                                background = extras.success.copy(alpha = 0.18f),
                                fontWeight = FontWeight.SemiBold
                            )
                        ) {
                            append(
                                stringResource(
                                    R.string.practice_wrong_missing_placeholder,
                                    unit.expected
                                )
                            )
                        }
                    }
                }
            }
        },
        fontSize = 20.sp,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = modifier
    )
}

@Composable
private fun formatLetterListSummary(
    letters: List<Char>,
    singularRes: Int,
    pluralRes: Int
): String? {
    if (letters.isEmpty()) return null
    val formatted = letters.joinToString(", ") { "'$it'" }
    return stringResource(
        if (letters.size == 1) singularRes else pluralRes,
        formatted
    )
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
            AppBorder.hairline,
            if (nudgeHints) {
                scheme.tertiary.copy(alpha = 0.26f)
            } else {
                scheme.outlineVariant.copy(alpha = 0.32f)
            }
        )
    ) {
        Text(
            text = stringResource(R.string.practice_hints_shown),
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
                text = stringResource(R.string.practice_hints_nudge),
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
                    text = stringResource(R.string.practice_hints_tip_label),
                    color = scheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(Modifier.width(AppSpacing.sm + AppSpacing.xs))

                Text(
                    text = stringResource(R.string.practice_hints_tip_body),
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
private fun HandwritingUnavailablePanel(modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AppDimensions.handwritingPanelMinHeight)
            .clip(RoundedCornerShape(AppRadius.glassCard))
            .background(scheme.surfaceVariant.copy(alpha = 0.34f)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.practice_handwriting_unavailable),
            color = scheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = AppSpacing.lg)
        )
    }
}

@Composable
private fun HandwritingInputPanel(
    modifier: Modifier = Modifier,
    recognizer: DigitalInkRecognizer,
    inkModelDownloader: InkModelDownloader,
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
            inkModelDownloader.ensureInkModelDownloaded()
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
            .fillMaxHeight()
            .heightIn(min = AppDimensions.handwritingPanelMinHeight)
            .clip(cardShape)
            .background(scheme.surfaceVariant.copy(alpha = 0.34f))
    ) {

        HandwritingCanvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = AppSpacing.sm + AppSpacing.xxs,
                    vertical = AppSpacing.sm + AppSpacing.xxs
                ),
            strokes = strokes,
            inkColor = inkColor,
            onStrokeFinished = { recognizeTick++ }
        )

        if (detectedText.isNotBlank()) {
            Text(
                text = stringResource(R.string.practice_handwriting_detected_format, detectedText),
                color = scheme.onSurfaceVariant,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = AppSpacing.md, end = AppSpacing.md + AppSpacing.xxs)
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(
                    start = AppSpacing.md + AppSpacing.xxs,
                    bottom = AppSpacing.md + AppSpacing.xxs
                ),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm + AppSpacing.xxs),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MinimalCircleOutlineIconButton(
                enabled = strokes.isNotEmpty() && !isRecognizing,
                onClick = {
                    strokes.clear()
                    recognizeTick++
                },
                imageVector = Icons.Filled.DeleteOutline,
                contentDescription = stringResource(R.string.content_desc_clear_handwriting)
            )

            MinimalCircleOutlineIconButton(
                enabled = strokes.isNotEmpty() && !isRecognizing,
                onClick = {
                    if (strokes.isNotEmpty()) strokes.removeAt(strokes.lastIndex)
                    recognizeTick++
                },
                imageVector = Icons.AutoMirrored.Filled.Undo,
                contentDescription = stringResource(R.string.content_desc_undo_stroke)
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
                        inkModelDownloader.ensureInkModelDownloaded()
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
                text = if (isRecognizing) {
                    stringResource(R.string.practice_handwriting_reading)
                } else {
                    stringResource(R.string.practice_handwriting_submit)
                },
                fontWeight = FontWeight.SemiBold,
                color = scheme.onPrimary,
                style = MaterialTheme.typography.labelLarge
            )

            Spacer(Modifier.width(AppSpacing.sm))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.Send,
                contentDescription = stringResource(R.string.content_desc_submit_handwriting),
                tint = scheme.onPrimary,
                modifier = Modifier.size(AppIconSize.sm)
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
            .size(dimensionResource(R.dimen.touch_target_min))
            .shadow(
                elevation = AppElevation.level1,
                shape = shape,
                ambientColor = scheme.primary.copy(alpha = 0.06f),
                spotColor = scheme.primary.copy(alpha = 0.08f)
            )
            .clip(shape)
            .background(scheme.surface.copy(alpha = 0.94f))
            .border(
                width = AppBorder.hairline,
                color = scheme.outlineVariant.copy(alpha = if (enabled) 0.18f else 0.12f),
                shape = shape
            )
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = scheme.onSurfaceVariant.copy(alpha = if (enabled) 1f else 0.45f),
            modifier = Modifier.size(AppIconSize.md)
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

private class InkModelDownloader {
    @Volatile
    private var modelReady = false
    private val mutex = Mutex()

    private val model: DigitalInkRecognitionModel? by lazy {
        val id = DigitalInkRecognitionModelIdentifier.fromLanguageTag("en-US")
            ?: return@lazy null
        DigitalInkRecognitionModel.builder(id).build()
    }

    suspend fun ensureInkModelDownloaded() {
        if (modelReady) return
        val inkModel = model ?: return

        mutex.withLock {
            if (modelReady) return@withLock

            val downloaded = runCatching {
                withContext(Dispatchers.Default) {
                    val conditions = DownloadConditions.Builder().build()
                    RemoteModelManager.getInstance()
                        .download(inkModel, conditions)
                        .await()
                }
            }.isSuccess

            if (downloaded) {
                modelReady = true
            }
        }
    }
}

private suspend fun recognizeInkWord(
    recognizer: DigitalInkRecognizer,
    strokes: List<HandwritingStroke>
): String = withContext(Dispatchers.Default) {
    if (strokes.isEmpty()) return@withContext ""

    runCatching {
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
    }.getOrElse { "" }
}

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
private fun CorrectAnswerSuccessCard(
    completed: Int,
    total: Int,
    showWordMastered: Boolean,
    wordProgressText: String,
    answerSoundsEnabled: Boolean,
    onAnswerSound: () -> Unit,
    onNextWord: () -> Unit
) {
    LaunchedEffect(Unit) {
        if (answerSoundsEnabled) {
            onAnswerSound()
        }
    }
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current
    val progress = if (total <= 0) {
        0f
    } else {
        (completed.toFloat() / total.toFloat()).coerceIn(0f, 1f)
    }

    SpellCoachCard(modifier = Modifier.fillMaxWidth()) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.sm, vertical = AppSpacing.sm + AppSpacing.xs)
        ) {
            val mascot =
                (maxWidth * AppDimensions.mascotCorrectCardWidthFraction).coerceIn(
                    AppDimensions.mascotCorrectCardMin,
                    AppDimensions.mascotCorrectCardMax
                )
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(R.drawable.fox_happy),
                    contentDescription = stringResource(R.string.content_desc_fox_happy),
                    modifier = Modifier.size(mascot)
                )

                Spacer(Modifier.height(AppSpacing.xs + AppSpacing.xxs + AppSpacing.xxs))

                Text(
                text = stringResource(R.string.practice_correct_title),
                color = extras.success,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            Text(
                text = stringResource(R.string.practice_correct_subtitle),
                color = scheme.onSurfaceVariant.copy(alpha = 0.82f),
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            if (showWordMastered) {
                Spacer(Modifier.height(AppSpacing.md))

                Text(
                    text = stringResource(R.string.practice_word_mastered),
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
                    text = stringResource(R.string.practice_progress_label),
                    color = scheme.onSurfaceVariant.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.labelMedium
                )

                Spacer(Modifier.weight(1f))

                Text(
                    text = stringResource(R.string.practice_progress_words_format, completed, total),
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
                text = stringResource(R.string.practice_next_word),
                onClick = onNextWord,
                leadingIcon = Icons.AutoMirrored.Filled.ArrowForward
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
                width = AppBorder.hairline,
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