package com.hogargo.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hogargo.app.ui.theme.BrandOrange
import com.hogargo.app.ui.theme.BrandOrangeContainer

/** Round avatar with the person's initial; the colour is derived from the name so it stays stable. */
@Composable
fun MemberAvatar(
    name: String,
    size: Dp,
    modifier: Modifier = Modifier,
    borderColor: Color? = null,
    borderWidth: Dp = 2.dp,
) {
    val palette = listOf(
        BrandOrangeContainer,
        BrandOrange.copy(alpha = 0.55f),
        MaterialTheme.colorScheme.tertiaryContainer,
        MaterialTheme.colorScheme.secondaryContainer,
    )
    val background = palette[(name.trim().lowercase().hashCode() and Int.MAX_VALUE) % palette.size]
    val shape = CircleShape
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(background)
            .then(if (borderColor != null) Modifier.border(borderWidth, borderColor, shape) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = name.trim().take(1).uppercase().ifEmpty { "?" },
            style = MaterialTheme.typography.titleMedium,
            fontSize = (size.value * 0.42f).sp,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
