package com.example.spellcoach.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spellcoach.presentation.theme.PrimaryBlue
import com.example.spellcoach.presentation.theme.PrimaryBlueAlt
import com.example.spellcoach.presentation.theme.TitleDark

@Composable
fun SpellCoachTopBar(
    showBack: Boolean,
    onBack: () -> Unit,
    brandTitle: String,
    brandAccent: String? = null,
    screenTitle: String? = null,
    heroTitle: String? = null,
    subtitleBelowBrand: String? = null,
    profileInitials: String = "SP",
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                if (showBack) {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = PrimaryBlueAlt
                        )
                    }
                }
                Column(
                    modifier = Modifier.padding(
                        start = if (showBack) 0.dp else 12.dp,
                        end = 8.dp
                    )
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = brandTitle,
                            color = PrimaryBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        if (brandAccent != null) {
                            Text(
                                text = "  $brandAccent",
                                color = PrimaryBlue.copy(alpha = 0.85f),
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }
                    }
                    if (screenTitle != null) {
                        Text(
                            text = screenTitle,
                            color = PrimaryBlueAlt,
                            fontWeight = FontWeight.Medium,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD1E4FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = profileInitials,
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
        if (heroTitle != null) {
            Text(
                text = heroTitle,
                color = TitleDark,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 4.dp)
            )
        }
        if (subtitleBelowBrand != null) {
            Text(
                text = subtitleBelowBrand,
                color = Color(0xFF64748B),
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 6.dp, bottom = 8.dp)
            )
        }
    }
}
