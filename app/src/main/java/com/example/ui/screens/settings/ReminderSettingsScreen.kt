package com.example.ui.screens.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.ui.components.ShowcaseGlassCard
import com.example.ui.screens.auth.MediFlowDropletLeafLogo
import com.example.ui.theme.MediFlowDeepTeal
import com.example.ui.theme.ShowcaseTealPrimary
import com.example.ui.viewmodel.MediFlowViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderSettingsScreen(
    viewModel: MediFlowViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val snoozeMins by viewModel.snoozeMinutes.collectAsStateWithLifecycle()
    val hydrationProfile by viewModel.hydrationProfile.collectAsStateWithLifecycle()
    val nextDose by viewModel.nextDose.collectAsStateWithLifecycle()

    var medRemindersEnabled by remember { mutableStateOf(true) }
    var hydRemindersEnabled by remember { mutableStateOf(hydrationProfile.notificationsEnabled) }
    var medVibrationEnabled by remember { mutableStateOf(true) }
    var hydVibrationEnabled by remember { mutableStateOf(true) }
    var medSoundName by remember { mutableStateOf("Default") }
    var hydSoundName by remember { mutableStateOf("Default") }
    var bannerActionFeedback by remember { mutableStateOf<String?>(null) }

    var showSnoozePicker by remember { mutableStateOf(false) }
    var showIntervalPicker by remember { mutableStateOf(false) }

    val soundOptions = listOf("Default", "Gentle Chime", "Crystal Drop", "Calm Wave")

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Reminder Settings",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Card 1: Medicine Reminders matching Screen 8
        ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Medicine Reminders",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = medRemindersEnabled,
                        onCheckedChange = { medRemindersEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = if (isDark) Color(0xFF0E949A) else ShowcaseTealPrimary
                        )
                    )
                }
                DividerLine()
                SettingNavRow(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    label = "Sound",
                    value = medSoundName,
                    onClick = {
                        val nextIdx = (soundOptions.indexOf(medSoundName) + 1) % soundOptions.size
                        medSoundName = soundOptions[nextIdx]
                    }
                )
                DividerLine()
                SettingToggleRow(
                    icon = Icons.Default.NotificationsActive,
                    label = "Vibration",
                    checked = medVibrationEnabled,
                    onCheckedChange = { medVibrationEnabled = it }
                )
                DividerLine()
                SettingNavRow(
                    icon = Icons.Default.Alarm,
                    label = "Snooze Duration",
                    value = "$snoozeMins minutes",
                    onClick = { showSnoozePicker = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Card 2: Hydration Reminders matching Screen 8
        ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Hydration Reminders",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Switch(
                        checked = hydRemindersEnabled,
                        onCheckedChange = { hydRemindersEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = if (isDark) Color(0xFF0E949A) else ShowcaseTealPrimary
                        )
                    )
                }
                DividerLine()
                SettingNavRow(
                    icon = Icons.AutoMirrored.Filled.VolumeUp,
                    label = "Sound",
                    value = hydSoundName,
                    onClick = {
                        val nextIdx = (soundOptions.indexOf(hydSoundName) + 1) % soundOptions.size
                        hydSoundName = soundOptions[nextIdx]
                    }
                )
                DividerLine()
                SettingToggleRow(
                    icon = Icons.Default.NotificationsActive,
                    label = "Vibration",
                    checked = hydVibrationEnabled,
                    onCheckedChange = { hydVibrationEnabled = it }
                )
                DividerLine()
                val hours = hydrationProfile.reminderIntervalMinutes / 60
                val intervalLabel = if (hours > 0) "$hours hours" else "${hydrationProfile.reminderIntervalMinutes} minutes"
                SettingNavRow(
                    icon = Icons.Default.HourglassBottom,
                    label = "Reminder Interval",
                    value = intervalLabel,
                    onClick = { showIntervalPicker = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // Screen 10: Interactive Lock-Screen Notification Banner Preview matching the 10th screen in the showcase
        Text(
            text = "Lock Screen Notification Preview",
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = 2.dp, bottom = 8.dp)
        )

        ShowcaseGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("card_lockscreen_notification_preview")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header: App Logo + "MediFlow" + "now"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(26.dp)
                                .clip(RoundedCornerShape(7.dp))
                                .background(if (isDark) Color(0x3327B9C0) else Color(0xFFE0F3F5))
                        ) {
                            MediFlowDropletLeafLogo(size = 18.dp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "MediFlow",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f)
                        )
                    }
                    Text(
                        text = "now",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Time for your medicine",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (nextDose != null) {
                        "${nextDose!!.medicineName} ${nextDose!!.strength} • Take 1 ${nextDose!!.doseUnit}"
                    } else {
                        "Paracetamol 500 mg • Take 1 tablet"
                    },
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                if (bannerActionFeedback != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = bannerActionFeedback!!,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ShowcaseTealPrimary
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Action Row: [ Snooze ] and [ Mark as Taken ] matching Screen 10
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            nextDose?.let { viewModel.snoozeDose(it.instance.id) }
                            bannerActionFeedback = "Snoozed for $snoozeMins minutes"
                        },
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0x3327B9C0) else Color(0xFFDDF1F3),
                            contentColor = if (isDark) Color(0xFFBCEBEF) else MediFlowDeepTeal
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_preview_snooze")
                    ) {
                        Text(
                            text = "Snooze",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Button(
                        onClick = {
                            nextDose?.let { viewModel.markDoseTaken(it.instance.id) }
                            bannerActionFeedback = "Marked as taken ✓"
                        },
                        shape = RoundedCornerShape(22.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF0E949A) else ShowcaseTealPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("btn_preview_mark_taken")
                    ) {
                        Text(
                            text = "Mark as Taken",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    if (showSnoozePicker) {
        AlertDialog(
            onDismissRequest = { showSnoozePicker = false },
            title = { Text("Snooze Duration", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(5, 10, 15, 30).forEach { mins ->
                        Surface(
                            color = if (mins == snoozeMins) ShowcaseTealPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setSnoozeMinutes(mins)
                                    showSnoozePicker = false
                                }
                        ) {
                            Text(
                                text = "$mins minutes",
                                color = if (mins == snoozeMins) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showSnoozePicker = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showIntervalPicker) {
        AlertDialog(
            onDismissRequest = { showIntervalPicker = false },
            title = { Text("Hydration Interval", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf(60 to "1 hour", 90 to "1.5 hours", 120 to "2 hours", 180 to "3 hours").forEach { (mins, label) ->
                        Surface(
                            color = if (mins == hydrationProfile.reminderIntervalMinutes) ShowcaseTealPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.updateHydrationTimes(
                                        wakeMin = hydrationProfile.wakeTimeMinutes,
                                        bedMin = hydrationProfile.bedTimeMinutes,
                                        intervalMin = mins
                                    )
                                    showIntervalPicker = false
                                }
                        ) {
                            Text(
                                text = label,
                                color = if (mins == hydrationProfile.reminderIntervalMinutes) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showIntervalPicker = false }) {
                    Text("Close")
                }
            }
        )
    }
}

@Composable
private fun SettingNavRow(
    icon: ImageVector,
    label: String,
    value: String,
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
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = value,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.35f),
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
private fun SettingToggleRow(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    val isDark = LocalMediFlowDarkTheme.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = if (isDark) Color(0xFF0E949A) else ShowcaseTealPrimary
            )
        )
    }
}

@Composable
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    )
}
