package com.example.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import com.example.ui.theme.LocalMediFlowDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ShowcaseGlassCard
import com.example.ui.components.UserPortraitAvatar
import com.example.ui.theme.MediFlowErrorRed
import com.example.ui.theme.ShowcaseTealPrimary
import com.example.ui.viewmodel.MediFlowViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProfileScreen(
    viewModel: MediFlowViewModel,
    onBack: () -> Unit,
    onNavigateToReminders: () -> Unit,
    onSignOut: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val themeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val hydrationProfile by viewModel.hydrationProfile.collectAsStateWithLifecycle()
    val profileStatusMessage by viewModel.profileStatusMessage.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val clipboardManager = LocalClipboardManager.current
    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var showHydrationGoalDialog by remember { mutableStateOf(false) }
    var goalInputText by remember(hydrationProfile.dailyGoalMl) {
        mutableStateOf(hydrationProfile.dailyGoalMl.toString())
    }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var showLogoutConfirmDialog by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var exportJsonText by remember { mutableStateOf("") }
    var showAboutDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Profile",
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

        Spacer(modifier = Modifier.height(6.dp))

        // Top Centered Portrait Avatar, Name & Gmail matching Screen 9
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showEditProfileDialog = true }
                .testTag("card_user_profile")
        ) {
            Box(contentAlignment = Alignment.BottomEnd) {
                UserPortraitAvatar(
                    displayName = userProfile.displayName,
                    avatarUrl = userProfile.avatarUrl,
                    size = 86.dp,
                    onClick = { showEditProfileDialog = true }
                )

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(ShowcaseTealPrimary)
                        .border(2.dp, Color.White, CircleShape)
                        .clickable { showEditProfileDialog = true }
                        .testTag("btn_edit_personal_details")
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Personal Details",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = userProfile.displayName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = userProfile.email,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                textAlign = TextAlign.Center
            )

            // Personal details chips if populated
            val detailsList = buildList {
                if (userProfile.age.isNotBlank()) add("Age: ${userProfile.age}")
                if (userProfile.weightKg.isNotBlank()) add("Weight: ${userProfile.weightKg}")
                if (userProfile.gender.isNotBlank()) add("Gender: ${userProfile.gender}")
                if (userProfile.bloodGroup.isNotBlank()) add("Blood: ${userProfile.bloodGroup}")
                if (userProfile.phone.isNotBlank()) add(userProfile.phone)
                if (userProfile.emergencyContact.isNotBlank()) add("SOS: ${userProfile.emergencyContact}")
            }

            if (detailsList.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    detailsList.forEach { badgeText ->
                        Surface(
                            color = if (isDark) Color(0x3327B9C0) else Color(0xFFDDF1F3),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = badgeText,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            if (profileStatusMessage != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = profileStatusMessage!!,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Cohesive Frosted Glass Options List matching Screen 9
        ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileOptionRow(
                    icon = Icons.Default.Person,
                    title = "Personal Information",
                    subtitle = "Edit details",
                    onClick = { showEditProfileDialog = true },
                    testTag = "row_personal_information"
                )
                DividerLine()
                ProfileOptionRow(
                    icon = Icons.Default.WaterDrop,
                    title = "Hydration Goal",
                    subtitle = "${numberFormat.format(hydrationProfile.dailyGoalMl)} ml",
                    onClick = { showHydrationGoalDialog = true },
                    testTag = "row_hydration_goal"
                )
                DividerLine()
                ProfileOptionRow(
                    icon = Icons.Default.Notifications,
                    title = "Reminder Settings",
                    subtitle = null,
                    onClick = onNavigateToReminders,
                    testTag = "row_reminder_settings"
                )
                DividerLine()

                // Appearance with interactive Light / Dark Mode toggle
                val isDarkModeActive = themeMode == "DARK"
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DarkMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = "Appearance",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = if (isDarkModeActive) "Dark Mode" else "Light Mode",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        }
                    }

                    Switch(
                        checked = isDarkModeActive,
                        onCheckedChange = { checked ->
                            viewModel.setThemeMode(if (checked) "DARK" else "LIGHT")
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = if (isDark) Color(0xFF0E949A) else ShowcaseTealPrimary
                        ),
                        modifier = Modifier.testTag("switch_dark_mode")
                    )
                }

                DividerLine()
                ProfileOptionRow(
                    icon = Icons.Default.Lock,
                    title = "Privacy & Security",
                    subtitle = null,
                    onClick = { showPrivacyDialog = true }
                )
                DividerLine()
                ProfileOptionRow(
                    icon = Icons.Default.FileDownload,
                    title = "Data & Export",
                    subtitle = null,
                    onClick = {
                        scope.launch {
                            exportJsonText = viewModel.exportDataJson()
                            showExportDialog = true
                        }
                    }
                )
                DividerLine()
                ProfileOptionRow(
                    icon = Icons.Default.Info,
                    title = "About",
                    subtitle = null,
                    onClick = { showAboutDialog = true }
                )
            }
        }

        Spacer(modifier = Modifier.height(22.dp))

        // "Log Out" Button matching Screen 9 (Soft Rose Pill with Coral Red text)
        Surface(
            color = if (isDark) Color(0x33DF6666) else Color(0xFFF9DEDE),
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .clickable { showLogoutConfirmDialog = true }
                .testTag("btn_logout")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Log Out",
                    color = MediFlowErrorRed,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { showClearDialog = true },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .testTag("btn_logout_reset")
        ) {
            Text(
                text = "Reset Local Schedule Data",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

    // Personal Information Editor Modal (Name, Email, Phone, Age, Weight, Height, Blood Group, Emergency Contact)
    if (showEditProfileDialog) {
        val initialWeightPart = remember(userProfile.weightKg) {
            userProfile.weightKg.substringBefore("•").trim()
        }
        val initialHeightPart = remember(userProfile.weightKg) {
            if (userProfile.weightKg.contains("•")) {
                userProfile.weightKg.substringAfter("•").trim()
            } else ""
        }

        var nameInput by remember(userProfile) { mutableStateOf(userProfile.displayName) }
        var emailInput by remember(userProfile) { mutableStateOf(userProfile.email) }
        var phoneInput by remember(userProfile) { mutableStateOf(userProfile.phone) }
        var ageInput by remember(userProfile) { mutableStateOf(userProfile.age) }
        var weightInput by remember(userProfile) { mutableStateOf(initialWeightPart) }
        var heightInput by remember(userProfile) { mutableStateOf(initialHeightPart) }
        var genderInput by remember(userProfile) { mutableStateOf(userProfile.gender) }
        var bloodGroupInput by remember(userProfile) { mutableStateOf(userProfile.bloodGroup) }
        var emergencyInput by remember(userProfile) { mutableStateOf(userProfile.emergencyContact) }

        val bloodGroups = listOf("O+", "O-", "A+", "A-", "B+", "B-", "AB+", "AB-")
        val genderOptions = listOf("Male", "Female", "Non-binary", "Prefer not to say")

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = {
                Text(
                    text = "Personal Information",
                    fontWeight = FontWeight.Bold,
                    fontSize = 19.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Edit your personal & health details.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )

                    OutlinedTextField(
                        value = nameInput,
                        onValueChange = { nameInput = it },
                        label = { Text("Username / Full Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_name")
                    )

                    OutlinedTextField(
                        value = emailInput,
                        onValueChange = { emailInput = it },
                        label = { Text("Email (Optional)") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_email")
                    )

                    OutlinedTextField(
                        value = phoneInput,
                        onValueChange = { phoneInput = it },
                        label = { Text("Phone Number") },
                        placeholder = { Text("e.g. +1 555-0199") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_phone")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = ageInput,
                            onValueChange = { ageInput = it },
                            label = { Text("Age") },
                            placeholder = { Text("28") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_profile_age")
                        )

                        OutlinedTextField(
                            value = weightInput,
                            onValueChange = { weightInput = it },
                            label = { Text("Weight") },
                            placeholder = { Text("70 kg") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_profile_weight")
                        )

                        OutlinedTextField(
                            value = heightInput,
                            onValueChange = { heightInput = it },
                            label = { Text("Height") },
                            placeholder = { Text("175 cm") },
                            singleLine = true,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("input_profile_height")
                        )
                    }

                    Text(
                        text = "Gender",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        genderOptions.forEach { option ->
                            val isSelected = genderInput.equals(option, ignoreCase = true)
                            Surface(
                                color = if (isSelected) ShowcaseTealPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { genderInput = option }
                            ) {
                                Text(
                                    text = option,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    Text(
                        text = "Blood Group",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        bloodGroups.forEach { bg ->
                            val isSelected = bloodGroupInput == bg
                            Surface(
                                color = if (isSelected) ShowcaseTealPrimary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { bloodGroupInput = bg }
                            ) {
                                Text(
                                    text = bg,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = emergencyInput,
                        onValueChange = { emergencyInput = it },
                        label = { Text("Emergency Contact") },
                        placeholder = { Text("Name & Phone") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_profile_emergency")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val combinedWeightHeight = when {
                            weightInput.isNotBlank() && heightInput.isNotBlank() ->
                                "${weightInput.trim()} • ${heightInput.trim()}"
                            weightInput.isNotBlank() -> weightInput.trim()
                            heightInput.isNotBlank() -> heightInput.trim()
                            else -> ""
                        }
                        viewModel.saveUserProfile(
                            displayName = nameInput,
                            age = ageInput,
                            weightKg = combinedWeightHeight,
                            gender = genderInput,
                            email = emailInput,
                            phone = phoneInput,
                            bloodGroup = bloodGroupInput,
                            emergencyContact = emergencyInput
                        )
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ShowcaseTealPrimary,
                        contentColor = Color.White
                    ),
                    modifier = Modifier.testTag("btn_save_personal_details")
                ) {
                    Text("Save Details")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showHydrationGoalDialog) {
        AlertDialog(
            onDismissRequest = { showHydrationGoalDialog = false },
            title = { Text("Daily Hydration Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Set your daily water target (ml):",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = goalInputText,
                        onValueChange = { goalInputText = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val newGoal = (goalInputText.toIntOrNull() ?: 2000).coerceIn(500, 6000)
                        viewModel.updateHydrationGoal(newGoal)
                        showHydrationGoalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShowcaseTealPrimary)
                ) {
                    Text("Save Goal", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showHydrationGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showLogoutConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutConfirmDialog = false },
            title = { Text("Log Out?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to log out of your MediFlow account?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutConfirmDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MediFlowErrorRed)
                ) {
                    Text("Log Out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutConfirmDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showPrivacyDialog) {
        AlertDialog(
            onDismissRequest = { showPrivacyDialog = false },
            title = { Text("Privacy & Security", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "Your personal profile is protected with zero-trust Firestore rules isolated strictly to your authenticated Gmail account, and your medication schedules are stored locally on your device."
                )
            },
            confirmButton = {
                TextButton(onClick = { showPrivacyDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            title = { Text("About MediFlow", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    text = "MediFlow — Take your health, one step at a time.\n\nVersion 1.0 • Serene Liquid Glass Edition"
                )
            },
            confirmButton = {
                TextButton(onClick = { showAboutDialog = false }) {
                    Text("OK")
                }
            }
        )
    }

    if (showExportDialog) {
        AlertDialog(
            onDismissRequest = { showExportDialog = false },
            title = { Text("Data Export", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text("Complete JSON ledger:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                    ) {
                        Text(
                            text = exportJsonText,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(8.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        clipboardManager.setText(AnnotatedString(exportJsonText))
                        showExportDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShowcaseTealPrimary)
                ) {
                    Text("Copy", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showExportDialog = false }) {
                    Text("Close")
                }
            }
        )
    }

    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Reset Data?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to clear all local schedule data and restore defaults?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.clearAllData()
                        showClearDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MediFlowErrorRed)
                ) {
                    Text("Reset", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun ProfileOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String?,
    onClick: () -> Unit,
    testTag: String? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp)
            .then(if (testTag != null) Modifier.testTag(testTag) else Modifier),
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
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
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
private fun DividerLine() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    )
}
