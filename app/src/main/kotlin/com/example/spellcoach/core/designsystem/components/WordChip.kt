package com.example.spellcoach.core.designsystem.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.example.spellcoach.R
import com.example.spellcoach.core.designsystem.tokens.AppRadius
import com.example.spellcoach.core.designsystem.tokens.AppSpacing

@Composable
fun WordChip(
    word: String,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(AppRadius.pill))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(horizontal = AppSpacing.md, vertical = AppSpacing.sm),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = word,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.width(AppSpacing.sm))
        Icon(
            imageVector = Icons.Filled.Close,
            contentDescription = stringResource(R.string.content_desc_remove_word, word),
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier
                .padding(AppSpacing.xs)
                .clickable(onClick = onRemove)
        )
    }
}
