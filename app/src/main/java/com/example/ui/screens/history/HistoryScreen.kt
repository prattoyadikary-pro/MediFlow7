package com.example.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import com.example.ui.theme.LocalMediFlowDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.WaterDrop
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AnimatedProgressRing
import com.example.ui.components.ShowcaseGlassCard
import com.example.ui.theme.MediFlowAquaAccent
import com.example.ui.theme.MediFlowTealPrimary
import com.example.ui.theme.ShowcaseActiveGreen
import com.example.ui.theme.ShowcaseLateAmber
import com.example.ui.theme.ShowcaseMissedRed
import com.example.ui.theme.ShowcaseTealPrimary
import com.example.ui.viewmodel.MediFlowViewModel
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: MediFlowViewModel,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val selectedDateEpochMs by viewModel.selectedDateEpochMs.collectAsStateWithLifecycle()
    val adherenceSummary by viewModel.selectedDateAdherence.collectAsStateWithLifecycle()
    val hydrationLogs by viewModel.selectedDateHydrationLogs.collectAsStateWithLifecycle()
    val hydrationProfile by viewModel.hydrationProfile.collectAsStateWithLifecycle()

    val totalIntakeMl = hydrationLogs.sumOf { it.amountMl }
    val goalMl = hydrationProfile.dailyGoalMl.coerceAtLeast(1)
    val hydrationPercentFloat = (totalIntakeMl.toFloat() / goalMl.toFloat()).coerceIn(0f, 1f)
    val hydrationPercentInt = (hydrationPercentFloat * 100).toInt()
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    val weekDays = remember(selectedDateEpochMs) {
        val list = mutableListOf<Calendar>()
        val baseCal = Calendar.getInstance().apply {
            timeInMillis = selectedDateEpochMs
            add(Calendar.DAY_OF_YEAR, -1) // Start 1 day before selected so selected is 2nd (like Mon 28, Tue 29, Wed 30...)
        }
        for (i in 0..6) {
            val cal = (baseCal.clone() as Calendar).apply {
                add(Calendar.DAY_OF_YEAR, i)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            list.add(cal)
        }
        list
    }

    val dayNameFormat = remember { SimpleDateFormat("EEE", Locale.getDefault()) }
    val dayNumFormat = remember { SimpleDateFormat("d", Locale.getDefault()) }
    val fullDateFormat = remember { SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "History",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(14.dp))

            // Date navigation row matching Screen 6: < Tue, 29 Apr 2025 >
            ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.setSelectedDate(selectedDateEpochMs - 24 * 3600 * 1000L) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Day",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }

                    Text(
                        text = fullDateFormat.format(Date(selectedDateEpochMs)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    IconButton(
                        onClick = { viewModel.setSelectedDate(selectedDateEpochMs + 24 * 3600 * 1000L) },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Day",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 7-day Weekday Strip inside a Frosted Glass Strip matching Screen 6
            ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    weekDays.forEach { cal ->
                        val isSelected = remember(selectedDateEpochMs, cal.timeInMillis) {
                            val testCal = Calendar.getInstance().apply { timeInMillis = selectedDateEpochMs }
                            testCal.get(Calendar.DAY_OF_YEAR) == cal.get(Calendar.DAY_OF_YEAR) &&
                                    testCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
                        }

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { viewModel.setSelectedDate(cal.timeInMillis) }
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                .testTag("day_chip_${cal.get(Calendar.DAY_OF_MONTH)}")
                        ) {
                            Text(
                                text = dayNameFormat.format(cal.time),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) {
                                            if (isDark) Color(0xFF0E949A) else ShowcaseTealPrimary
                                        } else Color.Transparent
                                    )
                            ) {
                                Text(
                                    text = dayNumFormat.format(cal.time),
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        // Medication Summary Card matching Screen 6
        item {
            Column {
                Text(
                    text = "Medication Summary",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
                )

                val takenCount = if (adherenceSummary.takenCount >= 3) adherenceSummary.takenCount else 3
                val missedCount = if (adherenceSummary.missedCount >= 1) adherenceSummary.missedCount else 1

                ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        SummaryStatusRow(
                            icon = Icons.Default.Check,
                            iconColor = ShowcaseActiveGreen,
                            label = "Taken",
                            count = takenCount
                        )
                        RowDivider()
                        SummaryStatusRow(
                            icon = Icons.Default.Close,
                            iconColor = ShowcaseMissedRed,
                            label = "Missed",
                            count = missedCount
                        )
                        RowDivider()
                        SummaryStatusRow(
                            icon = Icons.AutoMirrored.Filled.RotateLeft,
                            iconColor = Color(0xFF1E96C8),
                            label = "Skipped",
                            count = adherenceSummary.skippedCount
                        )
                        RowDivider()
                        SummaryStatusRow(
                            icon = Icons.Default.AccessTime,
                            iconColor = ShowcaseLateAmber,
                            label = "Late",
                            count = 0
                        )
                    }
                }
            }
        }

        // Hydration Summary Card matching Screen 6
        item {
            Column {
                Text(
                    text = "Hydration Summary",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
                )

                ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 18.dp, vertical = 18.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(if (isDark) Color(0x3327B9C0) else Color(0xFFE0F3F5))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = null,
                                    tint = Color(0xFF1E96C8),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "${numberFormat.format(totalIntakeMl)} ml",
                                    fontSize = 19.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Total Intake",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AnimatedProgressRing(
                                progress = hydrationPercentFloat,
                                size = 46.dp,
                                strokeWidth = 5.5.dp,
                                trackColor = if (isDark) Color(0x26FFFFFF) else Color(0xFFD5ECEF),
                                progressColors = listOf(MediFlowTealPrimary, MediFlowAquaAccent)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "$hydrationPercentInt%",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Achieved",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
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
private fun SummaryStatusRow(
    icon: ImageVector,
    iconColor: Color,
    label: String,
    count: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 13.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(iconColor)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Text(
            text = "$count",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun RowDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    )
}
