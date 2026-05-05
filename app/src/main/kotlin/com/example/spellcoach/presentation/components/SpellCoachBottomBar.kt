package com.example.spellcoach.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.EditNote
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spellcoach.presentation.theme.PrimaryBlue
import com.example.spellcoach.presentation.theme.PillSelectedBg

enum class MainTab { Lists, Practice, Settings }

@Composable
fun SpellCoachBottomBar(
    selected: MainTab,
    onSelect: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 10.dp, horizontal = 12.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SpellCoachNavItem(
            label = "Lists",
            selected = selected == MainTab.Lists,
            onClick = { onSelect(MainTab.Lists) },
            icon = Icons.Filled.List
        )
        SpellCoachNavItem(
            label = "Practice",
            selected = selected == MainTab.Practice,
            onClick = { onSelect(MainTab.Practice) },
            icon = Icons.Outlined.EditNote
        )
        SpellCoachNavItem(
            label = "Settings",
            selected = selected == MainTab.Settings,
            onClick = { onSelect(MainTab.Settings) },
            icon = Icons.Filled.Settings
        )
    }
}

@Composable
private fun SpellCoachNavItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    icon: ImageVector
) {
    val bg = if (selected) PillSelectedBg else Color.Transparent
    val tint = if (selected) PrimaryBlue else Color(0xFF9CA3AF)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 8.dp)
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = tint)
        Text(
            text = label,
            color = tint,
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
