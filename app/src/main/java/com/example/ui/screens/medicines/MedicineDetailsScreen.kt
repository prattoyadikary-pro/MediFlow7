package com.example.ui.screens.medicines

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.MealInstruction
import com.example.ui.components.PillGraphic
import com.example.ui.components.ShowcaseGlassCard
import com.example.ui.components.ShowcaseStatusPill
import com.example.ui.theme.MediFlowBorderSubtle
import com.example.ui.theme.MediFlowDeepTeal
import com.example.ui.theme.ShowcaseTealPrimary
import com.example.ui.viewmodel.MediFlowViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MedicineDetailsScreen(
    medicineId: Long,
    viewModel: MediFlowViewModel,
    onBack: () -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val allMeds by viewModel.allMedicines.collectAsStateWithLifecycle()
    val medItem = remember(allMeds, medicineId) {
        allMeds.find { it.medicine.id == medicineId }
    }

    var showOverflowMenu by remember { mutableStateOf(false) }

    if (medItem == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Medicine not found")
        }
        return
    }

    val med = medItem.medicine
    val sched = medItem.schedule
    val dateFormat = remember { SimpleDateFormat("d MMM yyyy", Locale.getDefault()) }

    val scheduleSummary = remember(sched) {
        when {
            sched == null -> "No schedule set"
            sched.scheduledTimes.size >= 2 -> "Daily • ${sched.scheduledTimes.size} times a day"
            else -> "Daily • ${sched.frequencyType.displayName}"
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = {},
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            actions = {
                Box {
                    IconButton(onClick = { showOverflowMenu = true }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Options"
                        )
                    }
                    DropdownMenu(
                        expanded = showOverflowMenu,
                        onDismissRequest = { showOverflowMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Refill (+30 pills)") },
                            onClick = {
                                showOverflowMenu = false
                                viewModel.refillMedicine(med.id, 30)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Delete Medicine", color = Color(0xFFDF6666)) },
                            onClick = {
                                showOverflowMenu = false
                                viewModel.deleteMedicine(med.id)
                                onBack()
                            }
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent
            )
        )

        // Cohesive Frosted Glass Details Container matching Screen 7
        ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Top Hero: 3D Blue Capsule Tile in center + "Active" badge at top-right
                Box(modifier = Modifier.fillMaxWidth()) {
                    ShowcaseStatusPill(
                        isActive = med.isActive,
                        modifier = Modifier.align(Alignment.TopEnd)
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PillGraphic(
                            colorHex = "#0284C7",
                            size = 96.dp,
                            isSquareTile = true
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = med.name,
                            fontSize = 23.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${med.strength} • ${med.category.displayName}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Inner Frosted Next Dose Card matching Screen 7
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isDark) Color(0x88081D25) else Color(0xCCF0F9FA))
                        .border(
                            1.dp,
                            if (isDark) Color(0x4D27B9C0) else MediFlowBorderSubtle,
                            RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Next Dose",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = sched?.scheduledTimes?.firstOrNull() ?: "08:00 AM",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x3327B9C0) else Color(0xFFD9F0F2))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Details Section (Schedule, Instructions, Start Date, End Date) matching Screen 7
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DetailRow(
                        label = "Schedule",
                        value = scheduleSummary
                    )
                    DetailDivider()
                    DetailRow(
                        label = "Instructions",
                        value = if (med.mealInstruction != MealInstruction.NO_INSTRUCTION) med.mealInstruction.displayName else "After food"
                    )
                    DetailDivider()
                    DetailRow(
                        label = "Start Date",
                        value = sched?.let { dateFormat.format(Date(it.startDateEpochMs)) } ?: "Not set"
                    )
                    DetailDivider()
                    DetailRow(
                        label = "End Date",
                        value = if (sched?.endDateEpochMs != null) dateFormat.format(Date(sched.endDateEpochMs)) else "Ongoing"
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Bottom Action Buttons: [ Pause ] and [ Edit ] matching Screen 7
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { viewModel.toggleMedicineActive(med.id, !med.isActive) },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0x3327B9C0) else Color(0xFFDDF1F3),
                            contentColor = if (isDark) Color(0xFFBCEBEF) else MediFlowDeepTeal
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_details_pause")
                    ) {
                        Text(
                            text = if (med.isActive) "Pause" else "Resume",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Button(
                        onClick = onEdit,
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF0E949A) else ShowcaseTealPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("btn_details_edit")
                    ) {
                        Text(
                            text = "Edit",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun DetailDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f))
    )
}
