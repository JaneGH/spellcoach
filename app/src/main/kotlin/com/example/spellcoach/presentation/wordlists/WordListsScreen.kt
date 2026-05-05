package com.example.spellcoach.presentation.wordlists

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.spellcoach.domain.model.WordList
import com.example.spellcoach.presentation.components.DesignProgressBar
import com.example.spellcoach.presentation.components.LearningCard
import com.example.spellcoach.presentation.components.SpellCoachTopBar
import com.example.spellcoach.presentation.theme.DashedBorder
import com.example.spellcoach.presentation.theme.PrimaryBlue
import com.example.spellcoach.presentation.theme.ProgressFillStrong
import com.example.spellcoach.presentation.theme.ScreenBackgroundCool

@Composable
fun WordListsScreen(
    onCreateNewList: () -> Unit,
    onPracticeList: (Long) -> Unit,
    viewModel: WordListsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackgroundCool)
    ) {
        Column(Modifier.fillMaxSize()) {
            SpellCoachTopBar(
                showBack = false,
                onBack = {},
                brandTitle = "SpellCoach",
                brandAccent = "My Lists",
                screenTitle = null,
                heroTitle = "My Lists",
                subtitleBelowBrand = "Keep track of your spelling progress across different groups."
            )
            LazyColumn(
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 10.dp,
                    bottom = 90.dp
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(state.lists) { list ->
                    WordListCard(
                        list = list,
                        onClick = {
                            viewModel.rememberPracticeList(list.id)
                            onPracticeList(list.id)
                        }
                    )
                }
                item {
                    CreateNewListCard(onClick = onCreateNewList)
                }
            }
        }
        FloatingActionButton(
            onClick = onCreateNewList,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 84.dp),
            containerColor = PrimaryBlue,
            contentColor = Color.White,
            shape = CircleShape
        ) {
            Icon(Icons.Filled.Add, contentDescription = "Add")
        }
    }
}

@Composable
private fun WordListCard(
    list: WordList,
    onClick: () -> Unit
) {
    LearningCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = list.name,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = "${list.learnedWords}/${list.totalWords} words learned",
                    fontSize = 14.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Medium
                )
            }
            if (list.isMastered) {
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE7F6EC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Mastered",
                        tint = ProgressFillStrong,
                        modifier = Modifier.size(16.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Filled.ChevronRight,
                    contentDescription = "Open",
                    tint = PrimaryBlue,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        DesignProgressBar(
            progress = list.progress,
            fullMastered = list.isMastered
        )
        Spacer(Modifier.height(12.dp))
        if (list.isMastered) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE7F6EC)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = ProgressFillStrong,
                        modifier = Modifier.size(12.dp)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Mastered!",
                    color = ProgressFillStrong,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        } else if (list.chips.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                list.chips.take(2).forEachIndexed { idx, chip ->
                    val bg = if (idx == 0) Color(0xFFE0F2FE) else Color(0xFFF3E8FF)
                    val fg = if (idx == 0) Color(0xFF0369A1) else Color(0xFF7E22CE)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(bg)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = chip, color = fg, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateNewListCard(onClick: () -> Unit) {
    val dash = PathEffect.dashPathEffect(floatArrayOf(12f, 10f), 0f)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp)
            .clip(RoundedCornerShape(20.dp))
            .drawBehind {
                val stroke = Stroke(width = 3.dp.toPx(), pathEffect = dash)
                drawRoundRect(
                    color = DashedBorder,
                    size = size,
                    style = stroke,
                    cornerRadius = CornerRadius(20.dp.toPx(), 20.dp.toPx())
                )
            }
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD7ECFF)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.Add, contentDescription = null, tint = PrimaryBlue)
            }
            Spacer(Modifier.height(10.dp))
            Text(
                text = "Create New List",
                color = PrimaryBlue,
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp
            )
        }
    }
}

