package com.example.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.ui.theme.LocalMediFlowDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.DoseStatus
import com.example.ui.components.AnimatedProgressRing
import com.example.ui.components.PillGraphic
import com.example.ui.components.ShowcaseGlassCard
import com.example.ui.components.ShowcasePillButton
import com.example.ui.components.ShowcaseProgressBar
import com.example.ui.components.UserPortraitAvatar
import com.example.ui.theme.MediFlowAquaAccent
import com.example.ui.theme.MediFlowTealPrimary
import com.example.ui.theme.MediFlowWarningAmber
import com.example.ui.theme.ShowcaseTealPrimary
import com.example.ui.viewmodel.MediFlowViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    viewModel: MediFlowViewModel,
    onNavigateToMedicines: () -> Unit,
    onNavigateToHydration: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenReminders: () -> Unit = onOpenProfile,
    onSelectMedicine: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val nextDose by viewModel.nextDose.collectAsStateWithLifecycle()
    val todayDoses by viewModel.todayDoses.collectAsStateWithLifecycle()
    val hydrationSummary by viewModel.todayHydration.collectAsStateWithLifecycle()

    val totalDoses = todayDoses.size
    val takenDoses = todayDoses.count { it.instance.status == DoseStatus.TAKEN }
    val progress = if (totalDoses > 0) takenDoses.toFloat() / totalDoses.toFloat() else 0f

    val firstName = remember(userProfile.displayName) {
        userProfile.displayName.trim().substringBefore(" ").ifBlank { "Alex" }
    }

    val dateFormatted = remember {
        SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()).format(Date())
    }

    val timeFormat = remember { SimpleDateFormat("h:mm a", Locale.getDefault()) }
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(6.dp))
            // Top Header matching Screen 2: Greeting + Date on left, Bell + Portrait Avatar on right
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Good Morning,",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f)
                    )
                    Text(
                        text = "$firstName ☀️",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = dateFormatted,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Notification Bell in Frosted Circle with Unread Red Dot
                    Box(modifier = Modifier.size(44.dp)) {
                        IconButton(
                            onClick = onOpenReminders,
                            modifier = Modifier
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0x3327B9C0) else Color(0xCCFFFFFF))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0x4D27B9C0) else Color(0xFFD3E9EB),
                                    CircleShape
                                )
                                .testTag("btn_home_notifications")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Reminder Settings",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .align(Alignment.TopEnd)
                                .padding(end = 3.dp, top = 3.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFDF6666))
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Warm Portrait Avatar matching Screen 2 & Screen 9
                    UserPortraitAvatar(
                        displayName = userProfile.displayName,
                        avatarUrl = userProfile.avatarUrl,
                        size = 44.dp,
                        onClick = onOpenProfile,
                        modifier = Modifier.testTag("btn_profile_avatar")
                    )
                }
            }
        }

        // Card 1: Next Dose Hero Card matching Screen 2
        item {
            ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    if (nextDose != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelectMedicine(nextDose!!.instance.medicineId) }
                        ) {
                            PillGraphic(
                                colorHex = "#0284C7",
                                size = 58.dp,
                                isSquareTile = true
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "Next Dose",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                                Text(
                                    text = nextDose!!.medicineName,
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                val dosageText = "${nextDose!!.strength} • ${nextDose!!.medicineCategory.displayName}".trim(' ', '•')
                                Text(
                                    text = dosageText,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                )
                                Text(
                                    text = timeFormat.format(Date(nextDose!!.instance.scheduledEpochMs)),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Full-width gradient teal pill button: "✓ Mark as Taken"
                        ShowcasePillButton(
                            text = "✓ Mark as Taken",
                            onClick = { viewModel.markDoseTaken(nextDose!!.instance.id) },
                            testTag = "btn_home_mark_taken"
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            PillGraphic(
                                colorHex = "#0284C7",
                                size = 56.dp,
                                isSquareTile = true
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(
                                    text = "No upcoming doses",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "You're all caught up on today's schedule.",
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        ShowcasePillButton(
                            text = "View Medicines",
                            onClick = onNavigateToMedicines,
                            testTag = "btn_home_add_med"
                        )
                    }
                }
            }
        }

        // Card 2: Today's Progress Card matching Screen 2 (Bar on left + 50% ring on right)
        item {
            ShowcaseProgressBar(
                progress = progress,
                completedDoses = takenDoses,
                totalDoses = totalDoses
            )
        }

        // Card 3: Hydration Card matching Screen 2 (+ Add Water on left + 60% ring on right)
        item {
            val consumedFormatted = numberFormat.format(hydrationSummary.totalAmountMl)
            val goalFormatted = numberFormat.format(hydrationSummary.goalMl)
            val hydrationPercent = (hydrationSummary.percentage * 100).toInt()

            ShowcaseGlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onNavigateToHydration)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = Color(0xFF1E96C8),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Hydration",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "$consumedFormatted ml / $goalFormatted ml",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Compact "+ Add Water" teal pill button
                        Button(
                            onClick = { viewModel.logWater(250) },
                            shape = RoundedCornerShape(18.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0xFF0E949A) else ShowcaseTealPrimary,
                                contentColor = Color.White
                            ),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("btn_home_add_water")
                        ) {
                            Text("+ Add Water", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }

                    // Circular progress ring with "60%" inside
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(60.dp)
                    ) {
                        AnimatedProgressRing(
                            progress = hydrationSummary.percentage,
                            size = 60.dp,
                            strokeWidth = 6.dp,
                            trackColor = if (isDark) Color(0x26FFFFFF) else Color(0xFFD5ECEF),
                            progressColors = listOf(MediFlowTealPrimary, MediFlowAquaAccent)
                        )
                        Text(
                            text = "$hydrationPercent%",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        // Section 4: Upcoming Reminders matching Screen 2
        item {
            Column {
                Text(
                    text = "Upcoming Reminders",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
                )

                val pendingDoses = todayDoses
                    .filter { it.instance.status == DoseStatus.PENDING }
                    .sortedBy { it.instance.scheduledEpochMs }
                    .take(2)

                ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        if (pendingDoses.isEmpty()) {
                            Text(
                                text = "No upcoming reminders",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 18.dp)
                            )
                        } else {
                            pendingDoses.forEachIndexed { index, dose ->
                                if (index > 0) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
                                    )
                                }
                                UpcomingReminderRow(
                                    time = timeFormat.format(Date(dose.instance.scheduledEpochMs)),
                                    title = "${dose.medicineName} • ${dose.medicineCategory.displayName}",
                                    dotColor = if (index == 0) Color(0xFF1E96C8) else MediFlowWarningAmber,
                                    onClick = { onSelectMedicine(dose.instance.medicineId) }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }
}

@Composable
private fun UpcomingReminderRow(
    time: String,
    title: String,
    dotColor: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(dotColor)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = time,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = title,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
            modifier = Modifier.size(18.dp)
        )
    }
}
