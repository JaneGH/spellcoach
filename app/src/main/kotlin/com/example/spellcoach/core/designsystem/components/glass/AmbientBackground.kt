package com.example.spellcoach.core.designsystem.components.glass

import android.graphics.RenderEffect as AndroidRenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import com.example.spellcoach.core.designsystem.theme.SpellCoachThemeExtras

@Composable
fun AmbientBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val scheme = MaterialTheme.colorScheme
    val extras = SpellCoachThemeExtras.current
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        scheme.background,
                        scheme.surfaceVariant.copy(alpha = 0.35f),
                        scheme.background
                    )
                )
            )
    ) {
        AmbientBlobs(modifier = Modifier.fillMaxSize())
        content()
    }
}

@Composable
private fun AmbientBlobs(modifier: Modifier = Modifier) {
    val extras = SpellCoachThemeExtras.current
    val blurModifier = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        Modifier.graphicsLayer {
            renderEffect = AndroidRenderEffect.createBlurEffect(
                50f,
                50f,
                Shader.TileMode.CLAMP
            ).asComposeRenderEffect()
        }
    } else {
        Modifier
    }

    Box(modifier = modifier.then(blurModifier)) {
        Blob(
            color = extras.glassBlobCyan,
            center = Offset(0.20f, 0.22f),
            sizeFraction = 0.72f,
            alpha = 0.22f
        )
        Blob(
            color = extras.glassBlobViolet,
            center = Offset(0.82f, 0.28f),
            sizeFraction = 0.62f,
            alpha = 0.18f
        )
        Blob(
            color = extras.glassBlobTeal,
            center = Offset(0.72f, 0.62f),
            sizeFraction = 0.62f,
            alpha = 0.14f
        )
    }
}

@Composable
private fun Blob(
    color: Color,
    center: Offset,
    sizeFraction: Float,
    alpha: Float
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                translationX = size.width * (center.x - 0.5f)
                translationY = size.height * (center.y - 0.5f)
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize(fraction = sizeFraction)
                .clip(CircleShape)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = alpha),
                            color.copy(alpha = 0f)
                        )
                    )
                )
        )
    }
}
