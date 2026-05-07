package com.example.spellcoach.presentation.addwords

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
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.presentation.components.LearningCard
import com.example.spellcoach.presentation.components.PrimaryButton
import com.example.spellcoach.presentation.components.SaveGreenButton
import com.example.spellcoach.presentation.components.SpellCoachTopBar
import com.example.spellcoach.presentation.components.WordChip
import com.example.spellcoach.presentation.theme.DashedBorder
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

    val pdfPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
        onResult = { uri ->
            // persist read permission for future reads during process lifetime
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(horizontal = 16.dp)
            .background(Color(0xFFF8FAFF))
    ) {
        SpellCoachTopBar(
            showBack = true,
            onBack = onBack,
            brandTitle = if (state.isEditMode) "Edit List" else "Add Words",
            brandAccent = null,
            screenTitle = null,
            subtitleBelowBrand = null
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            LearningCard(modifier = Modifier.fillMaxWidth()) {

                OutlinedTextField(
                    value = state.listName,
                    onValueChange = viewModel::setListName,
                    placeholder = {
                        Text("Enter list name")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                )

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "TYPE OR PASTE WORDS",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = state.rawInput,
                    onValueChange = viewModel::setRawInput,
                    placeholder = {
                        Text(
                            text = "Type words here,\nseparated by spaces or\ncommas...",
                            color = Color(0xFFCBD5E1),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    shape = RoundedCornerShape(14.dp)
                )
                Spacer(Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    PrimaryButton(
                        text = "Add",
                        onClick = viewModel::addParsedWordsFromInput,
                        modifier = Modifier.width(160.dp),
                        containerColor = Color(0xFF0B6B8C)
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            ImportCard(
                iconBg = Color(0xFFCFFAE5),
                icon = Icons.Filled.PictureAsPdf,
                title = "Import from PDF",
                subtitle = "Upload worksheets",
                onClick = { pdfPicker.launch(arrayOf("application/pdf")) }
            )

            Spacer(Modifier.height(12.dp))

            ImportCard(
                iconBg = Color(0xFFE9D5FF),
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

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Word Preview",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Box(
                    modifier = Modifier
                        .background(Color(0xFFE2E8F0), RoundedCornerShape(50))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${state.previewWords.size} Words",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0F172A)
                    )
                }
            }

            Spacer(Modifier.height(10.dp))

            if (state.isImporting) {
                Text(
                    text = "Importing...",
                    color = Color(0xFF0B6B8C),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
                Spacer(Modifier.height(10.dp))
            }

            val dash = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFFEFF6FF))
                    .drawBehind {
                        val stroke = Stroke(width = 2.dp.toPx(), pathEffect = dash)
                        drawRoundRect(
                            color = DashedBorder,
                            style = stroke,
                            cornerRadius = CornerRadius(16.dp.toPx(), 16.dp.toPx())
                        )
                    }
                    .padding(14.dp)
            ) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
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
                Spacer(Modifier.height(10.dp))
                Text(
                    text = state.errorMessage!!,
                    color = Color(0xFFB3261E),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.weight(1f))
            SaveGreenButton(
                text = if (state.isEditMode) "Save Changes" else "Save to My Lists",
                onClick = viewModel::save,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp)
            )
        }
    }
}

@Composable
private fun ImportCard(
    iconBg: Color,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    LearningCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(iconBg, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = Color(0xFF0F172A))
            }
            Spacer(Modifier.width(12.dp))
            Column {
                Text(text = title, fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                Text(text = subtitle, color = Color(0xFF64748B), fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

