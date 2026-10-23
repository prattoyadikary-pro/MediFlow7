package com.example.ui.screens.medicines

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MedicineEntity
import com.example.data.local.entity.MedicineScheduleEntity
import com.example.data.model.FrequencyType
import com.example.data.model.MealInstruction
import com.example.data.model.MedicineCategory
import com.example.ui.components.CupertinoDragHandle
import com.example.ui.components.IosGlassCard

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AddEditMedicineDialog(
    initialMedicine: MedicineEntity? = null,
    initialSchedule: MedicineScheduleEntity? = null,
    onDismiss: () -> Unit,
    onSave: (MedicineEntity, MedicineScheduleEntity) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(initialMedicine?.name ?: "") }
    var category by remember { mutableStateOf(initialMedicine?.category ?: MedicineCategory.TABLET) }
    var strength by remember { mutableStateOf(initialMedicine?.strength ?: "500 mg") }
    var doseAmountStr by remember { mutableStateOf((initialMedicine?.doseAmount ?: 1.0).toString()) }
    var doseUnit by remember { mutableStateOf(initialMedicine?.doseUnit ?: "tablet") }
    var mealInstruction by remember { mutableStateOf(initialMedicine?.mealInstruction ?: MealInstruction.NO_INSTRUCTION) }
    var notes by remember { mutableStateOf(initialMedicine?.notes ?: "") }
    var remainingQtyStr by remember { mutableStateOf((initialMedicine?.remainingQuantity ?: 30).toString()) }
    var refillThresholdStr by remember { mutableStateOf((initialMedicine?.refillThreshold ?: 5).toString()) }

    var frequencyType by remember { mutableStateOf(initialSchedule?.frequencyType ?: FrequencyType.ONCE_DAILY) }
    val scheduledTimes = remember {
        mutableStateListOf<String>().apply {
            addAll(initialSchedule?.scheduledTimes ?: listOf("09:00"))
        }
    }
    val selectedDays = remember {
        mutableStateListOf<Int>().apply {
            addAll(initialSchedule?.daysOfWeek ?: listOf(1, 2, 3, 4, 5, 6, 7))
        }
    }
    var intervalHoursStr by remember { mutableStateOf((initialSchedule?.intervalHours ?: 8).toString()) }
    var vibrate by remember { mutableStateOf(initialSchedule?.vibrate ?: true) }

    var nameError by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        dragHandle = { CupertinoDragHandle() },
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp),
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // iOS Modal Header: Cancel | Title | Done
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Cancel",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable(onClick = onDismiss)
                )

                Text(
                    text = if (initialMedicine == null) "New Medicine" else "Edit Medicine",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = "Done",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable {
                            if (name.isBlank()) {
                                nameError = true
                                return@clickable
                            }
                            val doseAmount = doseAmountStr.toDoubleOrNull() ?: 1.0
                            val remaining = remainingQtyStr.toIntOrNull() ?: 30
                            val threshold = refillThresholdStr.toIntOrNull() ?: 5
                            val interval = intervalHoursStr.toIntOrNull() ?: 8

                            val updatedMed = initialMedicine?.copy(
                                name = name.trim(),
                                category = category,
                                strength = strength.trim(),
                                doseAmount = doseAmount,
                                doseUnit = doseUnit.trim(),
                                mealInstruction = mealInstruction,
                                notes = notes.trim(),
                                remainingQuantity = remaining,
                                refillThreshold = threshold
                            ) ?: MedicineEntity(
                                name = name.trim(),
                                category = category,
                                strength = strength.trim(),
                                doseAmount = doseAmount,
                                doseUnit = doseUnit.trim(),
                                mealInstruction = mealInstruction,
                                notes = notes.trim(),
                                remainingQuantity = remaining,
                                refillThreshold = threshold
                            )

                            val updatedSched = initialSchedule?.copy(
                                frequencyType = frequencyType,
                                scheduledTimes = scheduledTimes.toList(),
                                daysOfWeek = selectedDays.toList(),
                                intervalHours = interval,
                                vibrate = vibrate
                            ) ?: MedicineScheduleEntity(
                                medicineId = 0,
                                frequencyType = frequencyType,
                                scheduledTimes = scheduledTimes.toList(),
                                daysOfWeek = selectedDays.toList(),
                                intervalHours = interval,
                                vibrate = vibrate
                            )

                            onSave(updatedMed, updatedSched)
                        }
                        .testTag("btn_save_medicine")
                )
            }

            // Inset Group 1: Name & Category
            IosGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = it.isBlank()
                        },
                        label = { Text("Medicine Name *") },
                        placeholder = { Text("e.g. Amoxicillin, Vitamin C") },
                        isError = nameError,
                        supportingText = { if (nameError) Text("Name is required") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_medicine_name")
                    )

                    Text(
                        text = "Form Category",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MedicineCategory.values().forEach { cat ->
                            val isSelected = cat == category
                            Surface(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable {
                                    category = cat
                                    if (doseUnit.isBlank() || doseUnit in listOf("tablet", "capsule", "ml", "drops", "puffs")) {
                                        doseUnit = when (cat) {
                                            MedicineCategory.TABLET -> "tablet"
                                            MedicineCategory.CAPSULE -> "capsule"
                                            MedicineCategory.SYRUP -> "ml"
                                            MedicineCategory.DROPS -> "drops"
                                            MedicineCategory.INHALER -> "puffs"
                                            MedicineCategory.INJECTION -> "units"
                                            MedicineCategory.OTHER -> "dose"
                                        }
                                    }
                                }
                            ) {
                                Text(
                                    text = cat.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Inset Group 2: Dosage & Food Instructions
            IosGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = strength,
                            onValueChange = { strength = it },
                            label = { Text("Strength") },
                            placeholder = { Text("500 mg") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = doseAmountStr,
                            onValueChange = { doseAmountStr = it },
                            label = { Text("Dose") },
                            placeholder = { Text("1") },
                            singleLine = true,
                            modifier = Modifier.weight(0.7f)
                        )
                        OutlinedTextField(
                            value = doseUnit,
                            onValueChange = { doseUnit = it },
                            label = { Text("Unit") },
                            placeholder = { Text("tablet") },
                            singleLine = true,
                            modifier = Modifier.weight(0.9f)
                        )
                    }

                    Text(
                        text = "Meal Instruction",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        MealInstruction.values().forEach { instr ->
                            val isSelected = instr == mealInstruction
                            Surface(
                                color = if (isSelected) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable { mealInstruction = instr }
                            ) {
                                Text(
                                    text = instr.displayName,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }

            // Inset Group 3: Schedule & Times
            IosGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Frequency",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FrequencyType.values().forEach { freq ->
                            val isSelected = freq == frequencyType
                            Surface(
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.clickable {
                                    frequencyType = freq
                                    when (freq) {
                                        FrequencyType.ONCE_DAILY -> {
                                            scheduledTimes.clear()
                                            scheduledTimes.add("09:00")
                                        }
                                        FrequencyType.TWICE_DAILY -> {
                                            scheduledTimes.clear()
                                            scheduledTimes.addAll(listOf("09:00", "21:00"))
                                        }
                                        FrequencyType.THREE_TIMES_DAILY -> {
                                            scheduledTimes.clear()
                                            scheduledTimes.addAll(listOf("08:00", "14:00", "20:00"))
                                        }
                                        else -> {}
                                    }
                                }
                            ) {
                                Text(
                                    text = freq.displayName,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }

                    if (frequencyType != FrequencyType.EVERY_N_HOURS) {
                        Text(
                            text = "Reminder Times (24h format)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            scheduledTimes.forEachIndexed { index, time ->
                                Surface(
                                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(text = time, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        if (scheduledTimes.size > 1) {
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Remove",
                                                modifier = Modifier
                                                    .size(14.dp)
                                                    .clickable { scheduledTimes.removeAt(index) }
                                            )
                                        }
                                    }
                                }
                            }

                            if (scheduledTimes.size < 6) {
                                IconButton(
                                    onClick = {
                                        val nextHour = (scheduledTimes.size * 4 + 8).coerceAtMost(23)
                                        val formatted = String.format("%02d:00", nextHour)
                                        scheduledTimes.add(formatted)
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = "Add Time",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    } else {
                        OutlinedTextField(
                            value = intervalHoursStr,
                            onValueChange = { intervalHoursStr = it },
                            label = { Text("Interval (Hours)") },
                            placeholder = { Text("6") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            // Inset Group 4: Inventory & Vibration
            IosGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = remainingQtyStr,
                            onValueChange = { remainingQtyStr = it },
                            label = { Text("Remaining Quantity") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = refillThresholdStr,
                            onValueChange = { refillThresholdStr = it },
                            label = { Text("Refill Alert At") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Notes / Instructions") },
                        placeholder = { Text("Take with plenty of water") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Vibration Alert", fontSize = 14.sp)
                        Switch(
                            checked = vibrate,
                            onCheckedChange = { vibrate = it }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
