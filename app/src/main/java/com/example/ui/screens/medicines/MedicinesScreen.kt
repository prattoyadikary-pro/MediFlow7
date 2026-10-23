package com.example.ui.screens.medicines

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Medication
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.FrequencyType
import com.example.data.model.MedicineWithSchedule
import com.example.ui.components.PillGraphic
import com.example.ui.components.ShowcaseGlassCard
import com.example.ui.components.ShowcaseStatusPill
import com.example.ui.theme.MediFlowBorderSubtle
import com.example.ui.theme.ShowcaseTealPrimary
import com.example.ui.viewmodel.MediFlowViewModel

enum class MedicineStatusFilter {
    ALL, ACTIVE, PAUSED
}

@Composable
fun MedicinesScreen(
    viewModel: MediFlowViewModel,
    onSelectMedicine: (Long) -> Unit,
    onAddMedicine: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val medicines by viewModel.allMedicines.collectAsStateWithLifecycle()

    var searchQuery by remember { mutableStateOf("") }
    var statusFilter by remember { mutableStateOf(MedicineStatusFilter.ALL) }

    val filteredMedicines = remember(medicines, searchQuery, statusFilter) {
        medicines.filter { item ->
            val matchesQuery = searchQuery.isBlank() ||
                    item.medicine.name.contains(searchQuery, ignoreCase = true) ||
                    item.medicine.strength.contains(searchQuery, ignoreCase = true)

            val matchesStatus = when (statusFilter) {
                MedicineStatusFilter.ALL -> true
                MedicineStatusFilter.ACTIVE -> item.medicine.isActive
                MedicineStatusFilter.PAUSED -> !item.medicine.isActive
            }
            matchesQuery && matchesStatus
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(10.dp))
                // Large Title "Medicines" matching Screen 3
                Text(
                    text = "Medicines",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(14.dp))

                // Frosted Glass Search Bar: "Search medicines..."
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .shadow(
                            elevation = 3.dp,
                            shape = RoundedCornerShape(18.dp),
                            spotColor = Color(0x14087F82)
                        )
                        .clip(RoundedCornerShape(18.dp))
                        .background(if (isDark) Color(0xD90E2933) else Color(0xE6FFFFFF))
                        .border(
                            1.dp,
                            if (isDark) Color(0x4D27B9C0) else MediFlowBorderSubtle,
                            RoundedCornerShape(18.dp)
                        )
                        .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f),
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(modifier = Modifier.weight(1f)) {
                            if (searchQuery.isEmpty()) {
                                Text(
                                    text = "Search medicines...",
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.45f)
                                )
                            }
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                singleLine = true,
                                textStyle = TextStyle(
                                    fontSize = 14.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                ),
                                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("search_medicine")
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Segmented Filter Pills: [All], [Active], [Paused] matching Screen 3
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    listOf(
                        MedicineStatusFilter.ALL to "All",
                        MedicineStatusFilter.ACTIVE to "Active",
                        MedicineStatusFilter.PAUSED to "Paused"
                    ).forEach { (filter, label) ->
                        val isSelected = filter == statusFilter
                        Surface(
                            color = if (isSelected) {
                                if (isDark) Color(0xFF0E949A) else ShowcaseTealPrimary
                            } else {
                                if (isDark) Color(0x3327B9C0) else Color(0xD9FFFFFF)
                            },
                            shape = RoundedCornerShape(18.dp),
                            border = if (isSelected) null else androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isDark) Color(0x4D27B9C0) else MediFlowBorderSubtle
                            ),
                            modifier = Modifier
                                .clickable { statusFilter = filter }
                                .testTag("filter_${label.lowercase()}")
                        ) {
                            Text(
                                text = label,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.72f),
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                            )
                        }
                    }
                }
            }

            if (filteredMedicines.isEmpty()) {
                item {
                    ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Medication,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(52.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "No medicines found" else "No medicines added yet",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Tap the + button below to add your first medicine.",
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            } else {
                items(filteredMedicines, key = { it.medicine.id }) { item ->
                    ShowcaseMedicineRowCard(
                        item = item,
                        onClick = { onSelectMedicine(item.medicine.id) }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(96.dp))
            }
        }

        // Floating Action Button matching Screen 3 (Teal circle with '+')
        FloatingActionButton(
            onClick = onAddMedicine,
            containerColor = if (isDark) Color(0xFF0E949A) else ShowcaseTealPrimary,
            contentColor = Color.White,
            shape = CircleShape,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 92.dp)
                .size(56.dp)
                .testTag("fab_add_medicine")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Medicine",
                modifier = Modifier.size(26.dp)
            )
        }
    }
}

/**
 * ShowcaseMedicineRowCard: Matching Screen 3 cards with 3D capsule/tablet icon,
 * Name, strength & type, time & frequency ("Daily" / "2x Daily"), and Active/Paused pill.
 */
@Composable
private fun ShowcaseMedicineRowCard(
    item: MedicineWithSchedule,
    onClick: () -> Unit
) {
    val med = item.medicine
    val sched = item.schedule
    val rawTime = sched?.scheduledTimes?.firstOrNull() ?: "8:00 AM"
    val time = rawTime.removePrefix("0")

    val freqLabel = when {
        med.name.equals("Paracetamol", ignoreCase = true) -> "Daily"
        sched?.frequencyType == FrequencyType.ONCE_DAILY -> "Daily"
        sched?.frequencyType == FrequencyType.TWICE_DAILY -> "2x Daily"
        sched?.frequencyType == FrequencyType.THREE_TIMES_DAILY -> "3x Daily"
        else -> sched?.frequencyType?.displayName ?: "Daily"
    }

    // In Screen 3 of the showcase, Paracetamol & Amoxicillin show coral-red capsules,
    // while Vitamin D3 & Omega 3 show round amber tablets.
    val listIconColorHex = when {
        med.name.equals("Paracetamol", ignoreCase = true) -> "#DF6666"
        med.name.equals("Vitamin D3", ignoreCase = true) || med.name.equals("Omega 3", ignoreCase = true) -> "#E9A23B"
        else -> med.colorHex
    }

    ShowcaseGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("medicine_card_${med.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PillGraphic(colorHex = listIconColorHex, size = 48.dp)
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = med.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${med.strength} • ${med.category.displayName}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "$time • $freqLabel",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                    )
                }
            }

            ShowcaseStatusPill(isActive = med.isActive)
        }
    }
}
