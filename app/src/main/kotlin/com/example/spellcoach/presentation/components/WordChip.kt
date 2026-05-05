package com.example.spellcoach.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.spellcoach.presentation.theme.TitleDark

@Composable
fun WordChip(
    word: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(Color.White)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = word,
            color = TitleDark,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp
        )
        Spacer(Modifier.width(6.dp))
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = "Remove",
            tint = Color(0xFF9CA3AF),
            modifier = Modifier
                .padding(2.dp)
                .clickable(onClick = onRemove)
        )
    }
}
