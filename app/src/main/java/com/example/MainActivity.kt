package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.local.entity.MedicineEntity
import com.example.data.local.entity.MedicineScheduleEntity
import com.example.ui.components.GlassBottomNavBar
import com.example.ui.components.MediFlowBackground
import com.example.ui.components.NavScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.auth.signOutUser
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.hydration.HydrationScreen
import com.example.ui.screens.medicines.AddMedicineScreen
import com.example.ui.screens.medicines.MedicineDetailsScreen
import com.example.ui.screens.medicines.MedicinesScreen
import com.example.ui.screens.settings.ProfileScreen
import com.example.ui.screens.settings.ReminderSettingsScreen
import com.example.ui.theme.MediFlowTheme
import com.example.ui.viewmodel.MediFlowViewModel
import com.google.firebase.auth.FirebaseAuth

sealed interface SubScreen {
    data object Profile : SubScreen
    data object ReminderSettings : SubScreen
    data class Details(val medicineId: Long) : SubScreen
    data class AddOrEdit(
        val medicine: MedicineEntity? = null,
        val schedule: MedicineScheduleEntity? = null
    ) : SubScreen
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val viewModel: MediFlowViewModel = viewModel()
            val themePreference by viewModel.themeMode.collectAsStateWithLifecycle()

            val isDarkTheme = when (themePreference) {
                "LIGHT" -> false
                "DARK" -> true
                else -> isSystemInDarkTheme()
            }

            // Notification permission request for Android 13+
            val notificationPermissionLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.RequestPermission(),
                onResult = { /* Handled */ }
            )

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    val hasPermission = ContextCompat.checkSelfPermission(
                        this@MainActivity,
                        Manifest.permission.POST_NOTIFICATIONS
                    ) == PackageManager.PERMISSION_GRANTED
                    if (!hasPermission) {
                        notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    }
                }
            }

            MediFlowTheme(darkTheme = isDarkTheme) {
                AuthGatedRoot(viewModel = viewModel, isDark = isDarkTheme)
            }
        }
    }
}

@Composable
fun AuthGatedRoot(
    viewModel: MediFlowViewModel,
    isDark: Boolean
) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    if (userProfile.displayName.isBlank()) {
        AuthScreen(
            viewModel = viewModel,
            onAuthSuccess = {
                // Profile saved and setup completed
            }
        )
    } else {
        MediFlowApp(
            viewModel = viewModel,
            isDark = isDark
        )
    }
}

@Composable
fun MediFlowApp(
    viewModel: MediFlowViewModel,
    isDark: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var currentTab by remember { mutableStateOf(NavScreen.HOME) }
    var activeSubScreen by remember { mutableStateOf<SubScreen?>(null) }
    val allMedicines by viewModel.allMedicines.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.syncAuthenticatedUser()
    }

    val scenicIntensity = if (activeSubScreen == null && currentTab == NavScreen.HYDRATION) 0.78f else 0.42f

    // Predictive / System Back gesture management
    BackHandler(enabled = activeSubScreen != null || currentTab != NavScreen.HOME) {
        when (val sub = activeSubScreen) {
            is SubScreen.ReminderSettings -> activeSubScreen = SubScreen.Profile
            is SubScreen.AddOrEdit -> {
                if (sub.medicine != null) {
                    activeSubScreen = SubScreen.Details(sub.medicine.id)
                } else {
                    activeSubScreen = null
                }
            }
            is SubScreen.Details, SubScreen.Profile -> activeSubScreen = null
            null -> currentTab = NavScreen.HOME
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        containerColor = Color.Transparent,
        bottomBar = {
            if (activeSubScreen == null) {
                GlassBottomNavBar(
                    currentScreen = currentTab,
                    onScreenSelected = { currentTab = it }
                )
            }
        },
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        MediFlowBackground(
            isDark = isDark,
            scenicIntensity = scenicIntensity,
            modifier = Modifier.fillMaxSize()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                AnimatedContent(
                    targetState = activeSubScreen to currentTab,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "showcase_screen_transition"
                ) { (subScreen, tab) ->
                    when (subScreen) {
                        is SubScreen.Profile -> {
                            ProfileScreen(
                                viewModel = viewModel,
                                onBack = { activeSubScreen = null },
                                onNavigateToReminders = { activeSubScreen = SubScreen.ReminderSettings },
                                onSignOut = {
                                    signOutUser(
                                        onSignOutComplete = {
                                            viewModel.clearAuthenticatedUserSession()
                                            activeSubScreen = null
                                            currentTab = NavScreen.HOME
                                        }
                                    )
                                }
                            )
                        }
                        is SubScreen.ReminderSettings -> {
                            ReminderSettingsScreen(
                                viewModel = viewModel,
                                onBack = { activeSubScreen = null }
                            )
                        }
                        is SubScreen.Details -> {
                            MedicineDetailsScreen(
                                medicineId = subScreen.medicineId,
                                viewModel = viewModel,
                                onBack = { activeSubScreen = null },
                                onEdit = {
                                    val match = allMedicines.find { it.medicine.id == subScreen.medicineId }
                                    activeSubScreen = SubScreen.AddOrEdit(
                                        medicine = match?.medicine,
                                        schedule = match?.schedule
                                    )
                                }
                            )
                        }
                        is SubScreen.AddOrEdit -> {
                            AddMedicineScreen(
                                initialMedicine = subScreen.medicine,
                                initialSchedule = subScreen.schedule,
                                onBack = {
                                    if (subScreen.medicine != null) {
                                        activeSubScreen = SubScreen.Details(subScreen.medicine.id)
                                    } else {
                                        activeSubScreen = null
                                    }
                                },
                                onSave = { med, sched ->
                                    if (subScreen.medicine != null) {
                                        viewModel.updateMedicine(med, sched)
                                        activeSubScreen = SubScreen.Details(med.id)
                                    } else {
                                        viewModel.addMedicine(med, sched)
                                        activeSubScreen = null
                                    }
                                }
                            )
                        }
                        null -> {
                            when (tab) {
                                NavScreen.HOME -> {
                                    HomeScreen(
                                        viewModel = viewModel,
                                        onNavigateToMedicines = { currentTab = NavScreen.MEDICINES },
                                        onNavigateToHydration = { currentTab = NavScreen.HYDRATION },
                                        onOpenProfile = { activeSubScreen = SubScreen.Profile },
                                        onOpenReminders = { activeSubScreen = SubScreen.ReminderSettings },
                                        onSelectMedicine = { medicineId ->
                                            activeSubScreen = SubScreen.Details(medicineId)
                                        }
                                    )
                                }
                                NavScreen.MEDICINES -> {
                                    MedicinesScreen(
                                        viewModel = viewModel,
                                        onSelectMedicine = { medicineId ->
                                            activeSubScreen = SubScreen.Details(medicineId)
                                        },
                                        onAddMedicine = {
                                            activeSubScreen = SubScreen.AddOrEdit()
                                        }
                                    )
                                }
                                NavScreen.HYDRATION -> {
                                    HydrationScreen(viewModel = viewModel)
                                }
                                NavScreen.HISTORY -> {
                                    HistoryScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
