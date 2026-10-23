package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.ui.theme.LocalMediFlowDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.MediFlowAquaAccent
import com.example.ui.theme.MediFlowBorderSubtle
import com.example.ui.theme.MediFlowDeepTeal
import com.example.ui.theme.MediFlowTealPrimary
import com.example.ui.theme.MediFlowTextMain
import com.example.ui.theme.MediFlowTextSecondary
import com.example.ui.theme.ShowcaseActiveGreen
import com.example.ui.theme.ShowcaseActiveGreenBg
import com.example.ui.theme.ShowcaseCardDark
import com.example.ui.theme.ShowcasePausedGrey
import com.example.ui.theme.ShowcasePausedGreyBg
import com.example.ui.theme.ShowcaseTealPrimary
import java.text.NumberFormat
import java.util.Locale

/**
 * Serene MediFlow atmospheric background with soft sky-to-aqua gradient
 * and gentle misty mountain/lake horizon at the bottom.
 */
@Composable
fun MediFlowBackground(
    isDark: Boolean = LocalMediFlowDarkTheme.current,
    scenicIntensity: Float = 0.45f,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val skyBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF06161C),
                Color(0xFF0A2129),
                Color(0xFF071B22),
                Color(0xFF041015)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFFDCEFF5),
                Color(0xFFEAF7F8),
                Color(0xFFE2F3F5),
                Color(0xFFCDE7EC)
            )
        )
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(skyBrush)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val alphaScale = scenicIntensity.coerceIn(0.15f, 1f)

            // Distant misty mountain ridge
            val farMountain = Path().apply {
                moveTo(0f, h * 0.72f)
                cubicTo(w * 0.22f, h * 0.65f, w * 0.48f, h * 0.74f, w * 0.72f, h * 0.67f)
                cubicTo(w * 0.86f, h * 0.63f, w * 0.94f, h * 0.68f, w, h * 0.65f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = farMountain,
                brush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color(0xFF0E333E).copy(alpha = 0.45f * alphaScale),
                            Color(0xFF072028).copy(alpha = 0.65f * alphaScale)
                        )
                    } else {
                        listOf(
                            Color(0xFF9AC6D0).copy(alpha = 0.35f * alphaScale),
                            Color(0xFF7CB2BE).copy(alpha = 0.45f * alphaScale)
                        )
                    }
                )
            )

            // Closer serene lake horizon wave
            val lakeWave = Path().apply {
                moveTo(0f, h * 0.83f)
                cubicTo(w * 0.32f, h * 0.79f, w * 0.68f, h * 0.87f, w, h * 0.81f)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
            drawPath(
                path = lakeWave,
                brush = Brush.verticalGradient(
                    colors = if (isDark) {
                        listOf(
                            Color(0xFF124652).copy(alpha = 0.4f * alphaScale),
                            Color(0xFF05181F).copy(alpha = 0.75f * alphaScale)
                        )
                    } else {
                        listOf(
                            Color(0xFF88BFC8).copy(alpha = 0.32f * alphaScale),
                            Color(0xFF4F939D).copy(alpha = 0.48f * alphaScale)
                        )
                    }
                )
            )
        }
        content()
    }
}

/**
 * ShowcaseGlassCard: Soft frosted white/aqua translucent glass card matching the 10-screen showcase.
 */
@Composable
fun ShowcaseGlassCard(
    modifier: Modifier = Modifier,
    shape: RoundedCornerShape = RoundedCornerShape(24.dp),
    elevation: Dp = 4.dp,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = LocalMediFlowDarkTheme.current
    val bgBrush = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                ShowcaseCardDark,
                Color(0xD90A2028)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xE6FFFFFF),
                Color(0xCCF4FBFB)
            )
        )
    }

    val borderBrush = Brush.linearGradient(
        colors = if (isDark) listOf(
            Color(0x6627B9C0),
            Color(0x26087F82),
            Color(0x4027B9C0)
        ) else listOf(
            Color(0xFFFFFFFF),
            MediFlowBorderSubtle,
            Color(0xFFE2F3F4)
        ),
        start = Offset(0f, 0f),
        end = Offset(400f, 400f)
    )

    Box(
        modifier = modifier
            .shadow(
                elevation = elevation,
                shape = shape,
                ambientColor = if (isDark) Color(0x66000000) else Color(0x14075B63),
                spotColor = if (isDark) Color(0x3327B9C0) else Color(0x18087F82)
            )
            .clip(shape)
            .background(bgBrush)
            .border(width = 1.dp, brush = borderBrush, shape = shape)
    ) {
        // Subtle top specular highlight
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            if (isDark) Color(0x4D80DEEA) else Color(0xCCFFFFFF),
                            Color.Transparent
                        )
                    )
                )
        )
        content()
    }
}

/**
 * ShowcasePillButton: Prominent rounded gradient teal pill button matching the showcase design.
 */
@Composable
fun ShowcasePillButton(
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
    val primaryColor = containerColor ?: if (isDark) Color(0xFF0E949A) else MediFlowTealPrimary
    val btnBrush = Brush.horizontalGradient(
        colors = if (containerColor != null) {
            listOf(containerColor, containerColor)
        } else if (isDark) {
            listOf(Color(0xFF087F82), Color(0xFF15989C))
        } else {
            listOf(
                MediFlowTealPrimary,
                Color(0xFF15989C)
            )
        }
    )

    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            contentColor = contentColor,
            disabledContainerColor = Color(0x22888888),
            disabledContentColor = Color(0x66FFFFFF)
        ),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 12.dp),
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 50.dp)
            .shadow(6.dp, RoundedCornerShape(26.dp), spotColor = primaryColor.copy(alpha = 0.35f))
            .clip(RoundedCornerShape(26.dp))
            .background(btnBrush)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
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
 * PillGraphic: Renders realistic 3D two-tone capsules (blue/white or coral-red/white)
 * or 3D round golden-amber tablets inside a soft pale-aqua container.
 */
@Composable
fun PillGraphic(
    colorHex: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
    isSquareTile: Boolean = false
) {
    val isDark = LocalMediFlowDarkTheme.current
    val normalizedHex = colorHex.uppercase(Locale.ROOT)
    val isAmberTablet = normalizedHex.contains("E9A23B") || normalizedHex.contains("F59E0B")
    val isCoralCapsule = normalizedHex.contains("DF6666") || normalizedHex.contains("EF4444")

    val primaryHalfColor = when {
        isAmberTablet -> Color(0xFFE9A23B)
        isCoralCapsule -> Color(0xFFE05A5A)
        else -> Color(0xFF1E88E5) // Vibrant azure blue cap matching Screen 2, 4, 7
    }

    val tileShape = if (isSquareTile) RoundedCornerShape(22.dp) else CircleShape
    val tileBg = if (isDark) Color(0x3327B9C0) else Color(0xFFE1F3F5)
    val tileBorder = if (isDark) Color(0x4D27B9C0) else Color(0xFFCBE7EA)

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .clip(tileShape)
            .background(tileBg)
            .border(1.dp, tileBorder, tileShape)
    ) {
        Canvas(modifier = Modifier.size(size * 0.62f)) {
            val w = this.size.width
            val h = this.size.height

            if (isAmberTablet) {
                // 3D Round Golden-Amber Tablet with specular rim & score line
                drawCircle(
                    color = Color(0x22000000),
                    radius = w * 0.42f,
                    center = Offset(w * 0.53f, h * 0.55f)
                )
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFFFCD34D),
                            Color(0xFFE9A23B),
                            Color(0xFFD97706)
                        ),
                        center = Offset(w * 0.38f, h * 0.35f),
                        radius = w * 0.55f
                    ),
                    radius = w * 0.42f,
                    center = Offset(w * 0.5f, h * 0.5f)
                )
                // Score groove line
                drawLine(
                    color = Color(0x5592400E),
                    start = Offset(w * 0.25f, h * 0.72f),
                    end = Offset(w * 0.75f, h * 0.28f),
                    strokeWidth = 1.8.dp.toPx(),
                    cap = StrokeCap.Round
                )
                // Glossy highlight arc
                drawCircle(
                    color = Color.White.copy(alpha = 0.45f),
                    radius = w * 0.12f,
                    center = Offset(w * 0.35f, h * 0.34f)
                )
            } else {
                // 3D Two-Tone Capsule angled at -38 degrees
                rotate(degrees = -38f, pivot = Offset(w * 0.5f, h * 0.5f)) {
                    val capW = w * 0.44f
                    val capH = h * 0.88f
                    val left = (w - capW) / 2f
                    val top = (h - capH) / 2f
                    val radius = capW / 2f

                    // Soft drop shadow
                    drawRoundRect(
                        color = Color(0x1F000000),
                        topLeft = Offset(left + 2.dp.toPx(), top + 3.dp.toPx()),
                        size = Size(capW, capH),
                        cornerRadius = CornerRadius(radius, radius)
                    )

                    // Bottom White Half
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFFFFFFF),
                                Color(0xFFE6F0F2)
                            ),
                            startX = left,
                            endX = left + capW
                        ),
                        topLeft = Offset(left, top),
                        size = Size(capW, capH),
                        cornerRadius = CornerRadius(radius, radius)
                    )

                    // Top Colored Half (Blue or Coral-Red)
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(
                                primaryHalfColor,
                                primaryHalfColor.copy(alpha = 0.85f)
                            ),
                            startX = left,
                            endX = left + capW
                        ),
                        topLeft = Offset(left, top),
                        size = Size(capW, capH * 0.54f),
                        cornerRadius = CornerRadius(radius, radius)
                    )

                    // Center seam band
                    drawLine(
                        color = Color(0x26000000),
                        start = Offset(left, top + capH * 0.5f),
                        end = Offset(left + capW, top + capH * 0.5f),
                        strokeWidth = 1.5.dp.toPx()
                    )

                    // Specular vertical highlight streak
                    drawRoundRect(
                        color = Color.White.copy(alpha = 0.55f),
                        topLeft = Offset(left + capW * 0.22f, top + capH * 0.12f),
                        size = Size(capW * 0.16f, capH * 0.65f),
                        cornerRadius = CornerRadius(4.dp.toPx(), 4.dp.toPx())
                    )
                }
            }
        }
    }
}

/**
 * ShowcaseStatusPill: Active (Green) or Paused (Soft Gray with bullet) status tag
 * matching Screen 3 and Screen 7.
 */
@Composable
fun ShowcaseStatusPill(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val bg = when {
        isActive && isDark -> Color(0x2936A982)
        isActive -> ShowcaseActiveGreenBg
        isDark -> Color(0x334F6870)
        else -> ShowcasePausedGreyBg
    }
    val fg = when {
        isActive && isDark -> Color(0xFF8EE0B9)
        isActive -> ShowcaseActiveGreen
        isDark -> Color(0xFFB7C8CC)
        else -> ShowcasePausedGrey
    }
    val text = if (isActive) "Active" else "• Paused"

    Surface(
        color = bg,
        shape = RoundedCornerShape(14.dp),
        modifier = modifier
    ) {
        Text(
            text = text,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        )
    }
}

/**
 * ShowcaseProgressBar: Matches Screen 2 "Today's Progress" card:
 * Left side has title, "2 of 4 doses", and slim horizontal progress bar;
 * Right side has circular progress ring with "50%" inside.
 */
@Composable
fun ShowcaseProgressBar(
    progress: Float, // 0f to 1f
    completedDoses: Int,
    totalDoses: Int,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(progress) {
        animatedProgress.animateTo(
            targetValue = progress.coerceIn(0f, 1f),
            animationSpec = tween(700, easing = FastOutSlowInEasing)
        )
    }

    ShowcaseGlassCard(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Today's Progress",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (totalDoses > 0) "$completedDoses of $totalDoses doses" else "No doses scheduled today",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Slim horizontal progress bar on the left side matching Screen 2
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.88f)
                        .height(7.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isDark) Color(0x26FFFFFF) else Color(0xFFD5ECEF))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress.value)
                            .height(7.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MediFlowTealPrimary,
                                        MediFlowAquaAccent
                                    )
                                )
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right circular progress ring with percentage inside
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.size(56.dp)
            ) {
                AnimatedProgressRing(
                    progress = progress,
                    size = 56.dp,
                    strokeWidth = 6.dp,
                    trackColor = if (isDark) Color(0x26FFFFFF) else Color(0xFFD5ECEF),
                    progressColors = listOf(MediFlowTealPrimary, MediFlowAquaAccent)
                )
                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

/**
 * ShowcaseCircularGauge: Large hydration dial matching Screen 5 of the showcase.
 */
@Composable
fun ShowcaseCircularGauge(
    percentage: Float,
    consumedMl: Int,
    goalMl: Int,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val animatedPercent = remember { Animatable(0f) }

    LaunchedEffect(percentage) {
        animatedPercent.animateTo(
            targetValue = percentage.coerceIn(0f, 1.5f),
            animationSpec = tween(900, easing = FastOutSlowInEasing)
        )
    }

    val formattedConsumed = remember(consumedMl) {
        NumberFormat.getNumberInstance(Locale.US).format(consumedMl)
    }
    val formattedGoal = remember(goalMl) {
        NumberFormat.getNumberInstance(Locale.US).format(goalMl)
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(236.dp)
            .shadow(
                elevation = 10.dp,
                shape = CircleShape,
                ambientColor = Color(0x26087F82),
                spotColor = Color(0x3327B9C0)
            )
            .clip(CircleShape)
            .background(
                Brush.radialGradient(
                    colors = if (isDark) listOf(
                        Color(0xFF0E2A34),
                        Color(0xFF081B22)
                    ) else listOf(
                        Color(0xFFF6FCFC),
                        Color(0xFFDCEFF2)
                    )
                )
            )
            .border(
                width = 1.5.dp,
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.White,
                        Color(0xFFBFE4E8),
                        Color(0xFF8FD4DC)
                    )
                ),
                shape = CircleShape
            )
    ) {
        // Circular progress arc
        Canvas(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            val strokeWidth = 12.dp.toPx()
            val diameter = size.width - strokeWidth
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(strokeWidth / 2, strokeWidth / 2)

            // Background Track
            drawArc(
                color = if (isDark) Color(0x26FFFFFF) else Color(0xFFCDE6EA),
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                topLeft = topLeft,
                size = arcSize
            )

            // Progress Arc
            if (animatedPercent.value > 0.01f) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            MediFlowTealPrimary,
                            MediFlowAquaAccent,
                            MediFlowTealPrimary
                        )
                    ),
                    startAngle = -90f,
                    sweepAngle = (animatedPercent.value.coerceAtMost(1f) * 360f),
                    useCenter = false,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round),
                    topLeft = topLeft,
                    size = arcSize
                )
            }
        }

        // Center Content
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            // Glossy Water Droplet Icon
            Canvas(modifier = Modifier.size(22.dp)) {
                val path = Path().apply {
                    moveTo(size.width / 2f, 0f)
                    cubicTo(size.width * 0.15f, size.height * 0.45f, 0f, size.height * 0.65f, size.width / 2f, size.height)
                    cubicTo(size.width, size.height * 0.65f, size.width * 0.85f, size.height * 0.45f, size.width / 2f, 0f)
                    close()
                }
                drawPath(
                    path = path,
                    brush = Brush.verticalGradient(
                        colors = listOf(MediFlowAquaAccent, MediFlowTealPrimary)
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "$formattedConsumed ml",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = "of $formattedGoal ml",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Percentage Pill Badge
            Surface(
                color = if (isDark) Color(0xFF0E949A) else Color(0xFFD5F0F2),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = "${(percentage * 100).toInt()}%",
                    color = if (isDark) Color.White else MediFlowDeepTeal,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                )
            }
        }
    }
}

/**
 * UserPortraitAvatar: Warm illustrated portrait avatar matching Screen 2 and Screen 9 of the showcase.
 */
@Composable
fun UserPortraitAvatar(
    displayName: String,
    avatarUrl: String? = null,
    size: Dp = 44.dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .size(size)
            .shadow(3.dp, CircleShape, spotColor = Color(0x26075B63))
            .clip(CircleShape)
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB5E0E6),
                        Color(0xFF82C2CC)
                    )
                )
            )
            .border(2.dp, Color.White.copy(alpha = 0.9f), CircleShape)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = this.size.width
            val h = this.size.height

            // Shoulders / Teal Crewneck Shirt
            val shirtPath = Path().apply {
                moveTo(w * 0.12f, h)
                cubicTo(w * 0.18f, h * 0.72f, w * 0.82f, h * 0.72f, w * 0.88f, h)
                close()
            }
            drawPath(shirtPath, color = Color(0xFF1F4E5B))

            // Neck
            drawRoundRect(
                color = Color(0xFFE0A98B),
                topLeft = Offset(w * 0.41f, h * 0.56f),
                size = Size(w * 0.18f, h * 0.2f),
                cornerRadius = CornerRadius(6f, 6f)
            )

            // Head / Warm Skin Tone
            drawCircle(
                color = Color(0xFFF0BE9F),
                radius = w * 0.23f,
                center = Offset(w * 0.5f, h * 0.43f)
            )

            // Soft Beard & Smile
            val beardPath = Path().apply {
                moveTo(w * 0.28f, h * 0.44f)
                cubicTo(w * 0.30f, h * 0.67f, w * 0.70f, h * 0.67f, w * 0.72f, h * 0.44f)
                cubicTo(w * 0.66f, h * 0.56f, w * 0.34f, h * 0.56f, w * 0.28f, h * 0.44f)
                close()
            }
            drawPath(beardPath, color = Color(0xFF4A3228))

            // Warm Smile
            drawArc(
                color = Color.White,
                startAngle = 15f,
                sweepAngle = 150f,
                useCenter = false,
                style = Stroke(width = (w * 0.032f).coerceAtLeast(1.5f), cap = StrokeCap.Round),
                topLeft = Offset(w * 0.41f, h * 0.44f),
                size = Size(w * 0.18f, h * 0.09f)
            )

            // Eyes
            drawCircle(
                color = Color(0xFF2D1E18),
                radius = w * 0.024f,
                center = Offset(w * 0.42f, h * 0.40f)
            )
            drawCircle(
                color = Color(0xFF2D1E18),
                radius = w * 0.024f,
                center = Offset(w * 0.58f, h * 0.40f)
            )

            // Styled Brown Hair
            val hairPath = Path().apply {
                moveTo(w * 0.26f, h * 0.42f)
                cubicTo(w * 0.24f, h * 0.18f, w * 0.76f, h * 0.16f, w * 0.74f, h * 0.42f)
                cubicTo(w * 0.66f, h * 0.27f, w * 0.35f, h * 0.27f, w * 0.26f, h * 0.42f)
                close()
            }
            drawPath(hairPath, color = Color(0xFF3E2723))
        }
        if (!avatarUrl.isNullOrBlank()) {
            AsyncImage(
                model = avatarUrl,
                contentDescription = "$displayName profile photo",
                contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
            )
        }
    }
}
