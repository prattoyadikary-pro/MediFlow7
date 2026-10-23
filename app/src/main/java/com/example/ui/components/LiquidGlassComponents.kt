package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.ui.theme.LocalMediFlowDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GlassBorderDark
import com.example.ui.theme.GlassBorderLight
import com.example.ui.theme.GlassCardDark
import com.example.ui.theme.GlassCardLight
import com.example.ui.theme.GlassHighlightWhite
import com.example.ui.theme.GlassWaterDepth
import com.example.ui.theme.GlassWaterWave
import com.example.ui.theme.OceanCyanDarkPrimary
import com.example.ui.theme.OceanCyanLightPrimary
import kotlin.math.sin

/**
 * LiquidGlassCard: Translucent glass card with specular edge highlight
 * and soft ambient drop shadow.
 */
@Composable
fun LiquidGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    elevation: Dp = 6.dp,
    borderBrush: Brush? = null,
    backgroundColor: Color? = null,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalMediFlowDarkTheme.current
    val bgColor = backgroundColor ?: if (isDark) GlassCardDark else GlassCardLight
    val specularBorder = borderBrush ?: Brush.linearGradient(
        colors = if (isDark) listOf(
            Color(0x6680DEEA),
            Color(0x1A00838F),
            Color(0x3300E5FF)
        ) else listOf(
            Color(0x99FFFFFF),
            Color(0x3300838F),
            Color(0x66B2EBF2)
        ),
        start = Offset(0f, 0f),
        end = Offset(400f, 400f)
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color(0x66000000) else Color(0x1A006874),
                spotColor = if (isDark) Color(0x4000E5FF) else Color(0x26006874)
            )
            .clip(shape)
            .background(bgColor)
            .border(width = 1.dp, brush = specularBorder, shape = shape)
    ) {
        // Specular top light reflex
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            if (isDark) Color(0x4D80DEEA) else GlassHighlightWhite,
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

/**
 * GlassButton: Vibrant tactile button with subtle specular highlight
 */
@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    testTag: String? = null,
    enabled: Boolean = true,
    containerColor: Color? = null,
    contentColor: Color = Color.White
) {
    val isDark = LocalMediFlowDarkTheme.current
    val primaryColor = containerColor ?: if (isDark) OceanCyanDarkPrimary else OceanCyanLightPrimary
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            primaryColor,
            primaryColor.copy(alpha = 0.85f)
        )
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Color(0x22888888),
            disabledContentColor = Color(0x66FFFFFF)
        ),
        contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = modifier
            .defaultMinSize(minHeight = 48.dp)
            .shadow(4.dp, RoundedCornerShape(16.dp), spotColor = primaryColor.copy(alpha = 0.5f))
            .background(gradientBrush, RoundedCornerShape(16.dp))
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        Color(0x80FFFFFF),
                        Color(0x1AFFFFFF)
                    )
                ),
                RoundedCornerShape(16.dp)
            )
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            Text(
                text = text,
                fontWeight = FontWeight.SemiBold,
                fontSize = 15.sp
            )
        }
    }
}

/**
 * GlassBadge: Translucent status tag
 */
@Composable
fun GlassBadge(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    Surface(
        color = color.copy(alpha = 0.15f),
        contentColor = color,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, color.copy(alpha = 0.35f)),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = text,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * WaterOrbVisualization: Dynamic fluid liquid glass orb
 * Displays animated sinusoidal waves responding smoothly to hydration levels.
 */
@Composable
fun WaterOrbVisualization(
    percentage: Float, // 0.0f to 1.0f+
    consumedMl: Int,
    goalMl: Int,
    modifier: Modifier = Modifier,
    onQuickAdd: () -> Unit = {}
) {
    val isDark = LocalMediFlowDarkTheme.current
    val animatedPercent = remember { Animatable(0f) }

    LaunchedEffect(percentage) {
        animatedPercent.animateTo(
            targetValue = percentage.coerceIn(0f, 1.5f),
            animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing)
        )
    }

    val infiniteTransition = rememberInfiniteTransition(label = "wave_anim")
    val waveOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * Math.PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2400, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wave_offset"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(190.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = Color(0x3300B4D8),
                spotColor = Color(0x6600E5FF)
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = if (isDark) listOf(
                        Color(0xFF0F2B33),
                        Color(0xFF07171C)
                    ) else listOf(
                        Color(0xFFE0F7FA),
                        Color(0xFFB2EBF2)
                    )
                )
            )
            .border(
                width = 2.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color(0x9980DEEA),
                        Color(0x3300838F),
                        Color(0x6600E5FF)
                    )
                ),
                shape = CircleShape
            )
            .clickable(onClick = onQuickAdd)
    ) {
        // Fluid Wave Canvas
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            val currentFill = animatedPercent.value.coerceIn(0.02f, 1.0f)
            val baseWaterY = height * (1f - currentFill)

            // Back wave (Darker / Deeper)
            val backPath = Path()
            backPath.moveTo(0f, height)
            backPath.lineTo(0f, baseWaterY)
            val waveLength = width
            val waveHeight = 9.dp.toPx()

            var x = 0f
            while (x <= width) {
                val y = baseWaterY + waveHeight * sin((x / waveLength * 2 * Math.PI + waveOffset + 1.2f).toDouble()).toFloat()
                backPath.lineTo(x, y)
                x += 5f
            }
            backPath.lineTo(width, height)
            backPath.close()

            drawPath(
                path = backPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassWaterDepth.copy(alpha = 0.55f),
                        GlassWaterDepth.copy(alpha = 0.85f)
                    ),
                    startY = baseWaterY,
                    endY = height
                )
            )

            // Front wave (Vibrant Aqua)
            val frontPath = Path()
            frontPath.moveTo(0f, height)
            frontPath.lineTo(0f, baseWaterY)

            x = 0f
            while (x <= width) {
                val y = baseWaterY + waveHeight * sin((x / waveLength * 2 * Math.PI + waveOffset).toDouble()).toFloat()
                frontPath.lineTo(x, y)
                x += 5f
            }
            frontPath.lineTo(width, height)
            frontPath.close()

            drawPath(
                path = frontPath,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF26C6DA).copy(alpha = 0.75f),
                        GlassWaterWave.copy(alpha = 0.90f),
                        GlassWaterDepth.copy(alpha = 0.95f)
                    ),
                    startY = baseWaterY,
                    endY = height
                )
            )

            // Top specular highlight curved crest
            drawArc(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0x66FFFFFF),
                        Color.Transparent
                    )
                ),
                startAngle = 200f,
                sweepAngle = 140f,
                useCenter = false,
                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round),
                topLeft = Offset(10.dp.toPx(), 10.dp.toPx()),
                size = Size(width - 20.dp.toPx(), height - 20.dp.toPx())
            )
        }

        // Content overlay: Center text
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {
            val percentText = (percentage * 100).toInt()
            Text(
                text = "$percentText%",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = if (animatedPercent.value > 0.45f) Color.White else MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "$consumedMl / $goalMl ml",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = if (animatedPercent.value > 0.45f) Color(0xCCFFFFFF) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * AnimatedProgressRing: Circular progress ring with glowing gradient
 */
@Composable
fun AnimatedProgressRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp = 80.dp,
    strokeWidth: Dp = 8.dp,
    trackColor: Color = Color(0x1F00838F),
    progressColors: List<Color> = listOf(Color(0xFF00E5FF), Color(0xFF10B981))
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(durationMillis = 800, easing = FastOutSlowInEasing)
        )
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            val diameter = size.toPx() - strokePx
            val topLeft = Offset(strokePx / 2, strokePx / 2)
            val ringSize = Size(diameter, diameter)

            // Background Track
            drawArc(
                color = trackColor,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round),
                topLeft = topLeft,
                size = ringSize
            )

            // Progress Arc
            if (animatedProgress.value > 0.01f) {
                drawArc(
                    brush = Brush.sweepGradient(progressColors),
                    startAngle = -90f,
                    sweepAngle = animatedProgress.value * 360f,
                    useCenter = false,
                    style = Stroke(width = strokePx, cap = StrokeCap.Round),
                    topLeft = topLeft,
                    size = ringSize
                )
            }
        }
    }
}
