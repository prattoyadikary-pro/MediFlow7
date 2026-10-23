package com.example.ui.screens.hydration

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiFoodBeverage
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.model.BeverageType
import com.example.ui.components.ShowcaseCircularGauge
import com.example.ui.components.ShowcaseGlassCard
import com.example.ui.components.ShowcasePillButton
import com.example.ui.theme.MediFlowBorderSubtle
import com.example.ui.theme.ShowcaseTealPrimary
import com.example.ui.viewmodel.MediFlowViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun HydrationScreen(
    viewModel: MediFlowViewModel,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val hydrationSummary by viewModel.todayHydration.collectAsStateWithLifecycle()
    val remainingMl = (hydrationSummary.goalMl - hydrationSummary.totalAmountMl).coerceAtLeast(0)

    var showCustomLogDialog by remember { mutableStateOf(false) }
    var customAmountText by remember { mutableStateOf("250") }
    var showGoalDialog by remember { mutableStateOf(false) }
    var goalInputText by remember(hydrationSummary.goalMl) { mutableStateOf(hydrationSummary.goalMl.toString()) }

    val numberFormat = remember { NumberFormat.getNumberInstance(Locale.US) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hydration",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(
                    onClick = { showGoalDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isDark) Color(0x3327B9C0) else Color(0xCCFFFFFF))
                        .border(
                            1.dp,
                            if (isDark) Color(0x4D27B9C0) else MediFlowBorderSubtle,
                            CircleShape
                        )
                        .testTag("btn_hydration_goal_settings")
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "Hydration Goal",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                        modifier = Modifier.size(19.dp)
                    )
                }
            }
        }

        // Hero: Large Circular Liquid Gauge matching Screen 5
        item {
            Spacer(modifier = Modifier.height(6.dp))
            ShowcaseCircularGauge(
                percentage = hydrationSummary.percentage,
                consumedMl = hydrationSummary.totalAmountMl,
                goalMl = hydrationSummary.goalMl
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        // 3 Quick Add Glass Cards in a Row (+150 ml, +250 ml, +500 ml) matching Screen 5
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                QuickServingCard(
                    icon = Icons.Default.EmojiFoodBeverage,
                    amountText = "+150 ml",
                    onClick = { viewModel.logWater(150, BeverageType.WATER) },
                    testTag = "btn_water_150",
                    modifier = Modifier.weight(1f)
                )

                QuickServingCard(
                    icon = Icons.Default.LocalDrink,
                    amountText = "+250 ml",
                    onClick = { viewModel.logWater(250, BeverageType.WATER) },
                    testTag = "btn_water_250",
                    modifier = Modifier.weight(1f)
                )

                QuickServingCard(
                    icon = Icons.Default.WaterDrop,
                    amountText = "+500 ml",
                    onClick = { viewModel.logWater(500, BeverageType.WATER) },
                    testTag = "btn_water_500",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Big Action Button: "💧 Log Water" matching Screen 5
        item {
            Spacer(modifier = Modifier.height(2.dp))
            ShowcasePillButton(
                text = "💧 Log Water",
                onClick = { showCustomLogDialog = true },
                testTag = "btn_log_water_main"
            )
        }

        // Bottom 2 Stat Cards: "Today's Intake" & "Remaining" matching Screen 5
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                ShowcaseGlassCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Today's Intake",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${numberFormat.format(hydrationSummary.totalAmountMl)} ml",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                ShowcaseGlassCard(modifier = Modifier.weight(1f)) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Remaining",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${numberFormat.format(remainingMl)} ml",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(96.dp))
        }
    }

    if (showCustomLogDialog) {
        AlertDialog(
            onDismissRequest = { showCustomLogDialog = false },
            title = { Text("Log Water Intake", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Enter amount consumed in ml:",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customAmountText,
                        onValueChange = { customAmountText = it },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val amount = customAmountText.toIntOrNull() ?: 250
                        viewModel.logWater(amount, BeverageType.WATER)
                        showCustomLogDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShowcaseTealPrimary)
                ) {
                    Text("Add Intake", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomLogDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showGoalDialog) {
        AlertDialog(
            onDismissRequest = { showGoalDialog = false },
            title = { Text("Daily Hydration Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Set your daily water target in ml:",
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
                        showGoalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ShowcaseTealPrimary)
                ) {
                    Text("Save Goal", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun QuickServingCard(
    icon: ImageVector,
    amountText: String,
    onClick: () -> Unit,
    testTag: String,
    modifier: Modifier = Modifier
) {
    ShowcaseGlassCard(
        modifier = modifier
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = Color(0xFF1E96C8),
                modifier = Modifier.size(26.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = amountText,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
