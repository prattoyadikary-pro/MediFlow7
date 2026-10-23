package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyHydrationSummary
import com.example.data.model.DoseItem
import com.example.ui.theme.StatusTaken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * DynamicIslandPill: Signature iOS Dynamic Island component.
 * Sits at the top of the interface, dynamically animating between
 * a compact status pill and an expanded interactive command center.
 */
@Composable
fun DynamicIslandPill(
    nextDose: DoseItem?,
    hydrationSummary: DailyHydrationSummary,
    onMarkTaken: (Long) -> Unit,
    onSnooze: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }

    val islandShape = RoundedCornerShape(if (isExpanded) 28.dp else 22.dp)
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

    val borderBrush = Brush.linearGradient(
        colors = listOf(
            Color(0x6680DEEA),
            Color(0x1A00E5FF),
            Color(0x3300838F)
        )
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = if (isExpanded) 16.dp else 36.dp, vertical = 6.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        Box(
            modifier = Modifier
                .animateContentSize(
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessMediumLow
                    )
                )
                .shadow(
                    elevation = 16.dp,
                    shape = islandShape,
                    ambientColor = Color(0x80000000),
                    spotColor = Color(0x4D00E5FF)
                )
                .clip(islandShape)
                .background(Color(0xF00A151B))
                .border(width = 1.dp, brush = borderBrush, shape = islandShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = { isExpanded = !isExpanded }
                )
                .padding(horizontal = if (isExpanded) 18.dp else 16.dp, vertical = if (isExpanded) 16.dp else 8.dp)
                .testTag("dynamic_island_pill")
        ) {
            if (!isExpanded) {
                // Compact Island View
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0x3300E5FF))
                        ) {
                            Icon(
                                imageVector = if (nextDose != null) Icons.Default.Medication else Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = Color(0xFF00E5FF),
                                modifier = Modifier.size(14.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (nextDose != null) nextDose.medicineName else "Hydration",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (nextDose != null) {
                                timeFormat.format(Date(nextDose.instance.scheduledEpochMs))
                            } else {
                                "${(hydrationSummary.percentage * 100).toInt()}%"
                            },
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF80DEEA)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        // Glowing cyan pulse indicator
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF00E5FF))
                        )
                    }
                }
            } else {
                // Expanded Island View
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x3300E5FF))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Medication,
                                    contentDescription = null,
                                    tint = Color(0xFF00E5FF),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "UPCOMING MEDICATION",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = Color(0xFF80DEEA)
                                )
                                Text(
                                    text = nextDose?.medicineName ?: "No pending medicines",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        IconButton(
                            onClick = { isExpanded = false },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Collapse",
                                tint = Color(0x99FFFFFF),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    if (nextDose != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val scheduledTime = timeFormat.format(Date(nextDose.instance.scheduledEpochMs))
                        val doseInfo = "${nextDose.doseAmount} ${nextDose.doseUnit} • ${nextDose.strength}"

                        Text(
                            text = "Scheduled for $scheduledTime ($doseInfo)",
                            fontSize = 13.sp,
                            color = Color(0xCCFFFFFF)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = {
                                    onMarkTaken(nextDose.instance.id)
                                    isExpanded = false
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = StatusTaken,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Take Dose", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            }

                            OutlinedButton(
                                onClick = {
                                    onSnooze(nextDose.instance.id)
                                    isExpanded = false
                                },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Alarm,
                                    contentDescription = "Snooze",
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Snooze", color = Color.White, fontSize = 13.sp)
                            }
                        }
                    } else {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Daily hydration: ${hydrationSummary.totalAmountMl} / ${hydrationSummary.goalMl} ml (${(hydrationSummary.percentage * 100).toInt()}%)",
                            fontSize = 13.sp,
                            color = Color(0xCCFFFFFF)
                        )
                    }
                }
            }
        }
    }
}
