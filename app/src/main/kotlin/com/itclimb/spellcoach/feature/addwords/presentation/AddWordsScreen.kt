package com.itclimb.spellcoach.feature.addwords.presentation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.itclimb.spellcoach.R
import com.itclimb.spellcoach.core.designsystem.components.LearningCard
import com.itclimb.spellcoach.core.designsystem.components.SaveGreenButton
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachOutlinedTextField
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachPrimaryButton
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachScreenContainer
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachTopBar
import com.itclimb.spellcoach.core.designsystem.components.SpellCoachTopBarVariant
import com.itclimb.spellcoach.core.designsystem.components.spellCoachScreenHorizontalPadding
import com.itclimb.spellcoach.core.designsystem.tokens.AppDimensions
import com.itclimb.spellcoach.core.designsystem.tokens.AppRadius
import com.itclimb.spellcoach.core.designsystem.tokens.AppSpacing
import com.itclimb.spellcoach.core.designsystem.components.WordChip
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AddWordsScreen(
    onBack: () -> Unit,
    onSaved: () -> Unit,
    viewModel: AddWordsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val scheme = MaterialTheme.colorScheme

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            if (uri != null) {
                runCatching {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
            viewModel.importFromPdf(uri)
        }
    )

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> viewModel.importFromImage(uri) }
    )

    LaunchedEffect(Unit) {
        viewModel.events.collectLatest { e ->
            if (e is AddWordsEvent.Saved) onSaved()
        }
    }

    SpellCoachScreenContainer(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        SpellCoachTopBar(
            variant = SpellCoachTopBarVariant.Inner,
            onBack = onBack,
            innerTitle = stringResource(
                if (state.isEditMode) R.string.add_words_title_edit else R.string.add_words_title_new
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .spellCoachScreenHorizontalPadding()
        ) {
            Column {

                Text(
                    text = "List name",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium,
                    color = scheme.onSurfaceVariant.copy(alpha = 0.9f),
                    modifier = Modifier.padding(
                        start = AppSpacing.sm + AppSpacing.xs,
                        bottom = AppSpacing.sm
                    )
                )

                SpellCoachOutlinedTextField(
                    value = state.listName,
                    onValueChange = viewModel::setListName,
                    placeholder = "Lesson 1",
                    modifier = Modifier.fillMaxWidth(),
                    height = 56.dp
                )
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            LearningCard(modifier = Modifier.fillMaxWidth()) {
                Spacer(Modifier.height(AppSpacing.md))

                Text(
                    text = "TYPE OR PASTE WORDS",
                    style = MaterialTheme.typography.labelMedium,
                    color = scheme.onSurfaceVariant.copy(alpha = 0.78f),
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(AppSpacing.md))
                SpellCoachOutlinedTextField(
                    value = state.rawInput,
                    onValueChange = viewModel::setRawInput,
                    placeholder = "Type words here, separated by spaces or commas…",
                    modifier = Modifier.fillMaxWidth(),
                    height = AppDimensions.addWordsFieldHeight,
                    singleLine = false,
                    minLines = 5,
                    maxLines = 12
                )
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    SpellCoachPrimaryButton(
                        text = "Add",
                        onClick = viewModel::addParsedWordsFromInput,
                        modifier = Modifier.width(160.dp)
                    )
                }
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.md))

            ImportCard(
                iconBg = scheme.primaryContainer.copy(alpha = 0.65f),
                icon = Icons.Filled.PictureAsPdf,
                title = "Import from PDF",
                subtitle = "Upload worksheets",
                onClick = { pdfPicker.launch(arrayOf("application/pdf")) }
            )

            Spacer(Modifier.height(AppSpacing.md))

            ImportCard(
                iconBg = scheme.tertiaryContainer.copy(alpha = 0.55f),
                icon = Icons.Filled.CameraAlt,
                title = "Scan from Photo",
                subtitle = "Snap a picture",
                onClick = {
                    imagePicker.launch(
                        PickVisualMediaRequest.Builder()
                            .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            .build()
                    )
                }
            )

            Spacer(Modifier.height(AppSpacing.lg))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Word Preview",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = scheme.onSurface
                )
                Box(
                    modifier = Modifier
                        .background(scheme.surfaceVariant.copy(alpha = 0.42f), RoundedCornerShape(AppRadius.pill))
                        .padding(horizontal = AppSpacing.sm + AppSpacing.xs, vertical = AppSpacing.xs)
                ) {
                    Text(
                        text = "${state.previewWords.size} Words",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = scheme.onSurfaceVariant.copy(alpha = 0.82f)
                    )
                }
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            if (state.isImporting) {
                Text(
                    text = "Importing...",
                    color = scheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
            }

            val dash = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)
            val previewOutline = scheme.outlineVariant.copy(alpha = 0.22f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppRadius.lg))
                    .background(scheme.surface.copy(alpha = 0.92f))
                    .drawBehind {
                        val stroke = Stroke(width = 1.dp.toPx(), pathEffect = dash)
                        drawRoundRect(
                            color = previewOutline,
                            style = stroke,
                            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                        )
                    }
                    .padding(AppSpacing.sm + AppSpacing.md)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(AppSpacing.sm + AppSpacing.xs),
                    verticalArrangement = Arrangement.spacedBy(AppSpacing.sm + AppSpacing.xs)
                ) {
                    state.previewWords.forEach { w ->
                        WordChip(
                            word = w,
                            onRemove = { viewModel.removeWord(w) }
                        )
                    }
                }
            }

            if (state.errorMessage != null) {
                Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))
                Text(
                    text = state.errorMessage!!,
                    color = scheme.error,
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(AppSpacing.sm + AppSpacing.xs))

            SaveGreenButton(
                text = if (state.isEditMode) "Save Changes" else "Save to My Lists",
                onClick = viewModel::save,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = AppSpacing.sm + AppSpacing.md)
            )
        }
    }
}

@Composable
private fun ImportCard(
    iconBg: androidx.compose.ui.graphics.Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    LearningCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBg, RoundedCornerShape(AppRadius.sm)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = scheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(AppSpacing.sm + AppSpacing.md))
            Column {
                Text(
                    text = title,
                    fontWeight = FontWeight.SemiBold,
                    color = scheme.onSurface,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = subtitle,
                    color = scheme.onSurfaceVariant.copy(alpha = 0.78f),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}
