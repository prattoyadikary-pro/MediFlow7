package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import com.example.ui.theme.LocalMediFlowDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassHighlightWhite

/**
 * IosGlassCard: Ultra-thin frosted glass card with Cupertino grouped inset styling,
 * 0.5dp specular border and realistic glass lighting.
 */
@Composable
fun IosGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(22.dp),
    elevation: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalMediFlowDarkTheme.current
    val cardBackground = if (isDark) Color(0xD90D1E26) else Color(0xEBFFFFFF)

    val specularBorder = Brush.linearGradient(
        colors = if (isDark) listOf(
            Color(0x6680DEEA),
            Color(0x1F00838F),
            Color(0x4000E5FF)
        ) else listOf(
            Color(0xB3FFFFFF),
            Color(0x2600838F),
            Color(0x80E0F7FA)
        ),
        start = Offset(0f, 0f),
        end = Offset(350f, 350f)
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color(0x66000000) else Color(0x14006874),
                spotColor = if (isDark) Color(0x3300E5FF) else Color(0x1F006874)
            )
            .clip(shape)
            .background(cardBackground)
            .border(width = 1.dp, brush = specularBorder, shape = shape)
    ) {
        // Specular top highlight hairline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            if (isDark) Color(0x6680DEEA) else GlassHighlightWhite,
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

/**
 * IosSegmentedControl: Apple Cupertino-style sliding segmented selector
 */
@Composable
fun <T> IosSegmentedControl(
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    labelProvider: (T) -> String,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val controlBg = if (isDark) Color(0x800A181F) else Color(0x33B2EBF2)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(controlBg)
            .border(1.dp, if (isDark) Color(0x3380DEEA) else Color(0x4D00838F), RoundedCornerShape(14.dp))
            .padding(3.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items.forEach { item ->
            val isSelected = item == selectedItem
            val targetBg = if (isSelected) {
                if (isDark) Color(0xFF00838F) else Color(0xFF006874)
            } else Color.Transparent

            val bgAnimation by animateColorAsState(
                targetValue = targetBg,
                animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                label = "seg_bg"
            )

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .weight(1f)
                    .height(34.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(bgAnimation)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { onItemSelected(item) }
                    )
            ) {
                Text(
                    text = labelProvider(item),
                    fontSize = 12.sp,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

/**
 * CupertinoDragHandle: Standard iOS modal sheet grabber handle
 */
@Composable
fun CupertinoDragHandle(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .width(36.dp)
                .height(4.5.dp)
                .clip(CircleShape)
                .background(Color(0x4D888888))
        )
    }
}
