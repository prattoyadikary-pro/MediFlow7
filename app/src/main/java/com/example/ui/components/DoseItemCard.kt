package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DoseItem
import com.example.data.model.DoseStatus
import com.example.data.model.MealInstruction
import com.example.ui.theme.StatusMissed
import com.example.ui.theme.StatusPending
import com.example.ui.theme.StatusSkipped
import com.example.ui.theme.StatusSnoozed
import com.example.ui.theme.StatusTaken
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DoseItemCard(
    doseItem: DoseItem,
    onMarkTaken: (Long) -> Unit,
    onSnooze: (Long) -> Unit,
    onSkip: (Long) -> Unit,
    modifier: Modifier = Modifier,
    isHeroCard: Boolean = false
) {
    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val formattedTime = timeFormat.format(Date(doseItem.instance.scheduledEpochMs))

    val statusColor = when (doseItem.instance.status) {
        DoseStatus.TAKEN -> StatusTaken
        DoseStatus.SNOOZED -> StatusSnoozed
        DoseStatus.SKIPPED -> StatusSkipped
        DoseStatus.MISSED -> StatusMissed
        DoseStatus.PENDING -> StatusPending
    }

    val statusLabel = when (doseItem.instance.status) {
        DoseStatus.TAKEN -> "Taken"
        DoseStatus.SNOOZED -> "Snoozed"
        DoseStatus.SKIPPED -> "Skipped"
        DoseStatus.MISSED -> "Missed"
        DoseStatus.PENDING -> "Due"
    }

    IosGlassCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("dose_card_${doseItem.instance.id}"),
        elevation = if (isHeroCard) 8.dp else 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row: Category Badge + Status Badge + Time
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
                            .background(statusColor.copy(alpha = 0.15f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Medication,
                            contentDescription = null,
                            tint = statusColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = doseItem.medicineCategory.displayName,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Text(
                            text = formattedTime,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                GlassBadge(
                    text = statusLabel,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Medicine Name and Dosage
            Text(
                text = doseItem.medicineName,
                fontSize = if (isHeroCard) 20.sp else 18.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            val detailsText = "${doseItem.doseAmount} ${doseItem.doseUnit}" +
                    if (doseItem.strength.isNotBlank()) " • ${doseItem.strength}" else ""
            Text(
                text = detailsText,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
            )

            // Meal Instruction & Refill warning
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (doseItem.mealInstruction != MealInstruction.NO_INSTRUCTION) {
                    GlassBadge(
                        text = doseItem.mealInstruction.displayName,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (doseItem.isRefillNeeded) {
                    GlassBadge(
                        text = "Refill soon (${doseItem.remainingQuantity} left)",
                        color = StatusSnoozed,
                        icon = Icons.Default.Warning
                    )
                }
            }

            // Action Buttons
            if (doseItem.instance.status == DoseStatus.PENDING || doseItem.instance.status == DoseStatus.SNOOZED) {
                Spacer(modifier = Modifier.height(14.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Mark as Taken (Prominent)
                    Button(
                        onClick = { onMarkTaken(doseItem.instance.id) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = StatusTaken,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_mark_taken_${doseItem.instance.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Take Dose",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    // Snooze Button
                    OutlinedButton(
                        onClick = { onSnooze(doseItem.instance.id) },
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .height(44.dp)
                            .testTag("btn_snooze_${doseItem.instance.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Alarm,
                            contentDescription = "Snooze",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Snooze", fontSize = 13.sp)
                    }

                    // Skip Button
                    IconButton(
                        onClick = { onSkip(doseItem.instance.id) },
                        modifier = Modifier
                            .size(44.dp)
                            .testTag("btn_skip_${doseItem.instance.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Skip Dose",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            } else if (doseItem.instance.status == DoseStatus.TAKEN && doseItem.instance.actualActionEpochMs != null) {
                Spacer(modifier = Modifier.height(8.dp))
                val takenAt = timeFormat.format(Date(doseItem.instance.actualActionEpochMs))
                Text(
                    text = "✓ Taken at $takenAt",
                    fontSize = 12.sp,
                    color = StatusTaken,
                    fontWeight = FontWeight.Medium
                )
            } else if (doseItem.instance.status == DoseStatus.SKIPPED) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "↷ Skipped: ${doseItem.instance.skipReason ?: "No reason given"}",
                    fontSize = 12.sp,
                    color = StatusSkipped,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
