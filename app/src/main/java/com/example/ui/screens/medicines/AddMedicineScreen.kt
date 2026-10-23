package com.example.ui.screens.medicines

import android.app.DatePickerDialog
import android.app.TimePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.MedicineEntity
import com.example.data.local.entity.MedicineScheduleEntity
import com.example.data.model.FrequencyType
import com.example.data.model.MealInstruction
import com.example.data.model.MedicineCategory
import com.example.ui.components.PillGraphic
import com.example.ui.components.ShowcaseGlassCard
import com.example.ui.components.ShowcasePillButton
import com.example.ui.theme.MediFlowBorderSubtle
import com.example.ui.theme.ShowcaseTealPrimary
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMedicineScreen(
    initialMedicine: MedicineEntity? = null,
    initialSchedule: MedicineScheduleEntity? = null,
    onBack: () -> Unit,
    onSave: (MedicineEntity, MedicineScheduleEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalMediFlowDarkTheme.current
    val context = LocalContext.current
    var name by remember { mutableStateOf(initialMedicine?.name ?: "") }
    var dosage by remember { mutableStateOf(initialMedicine?.strength ?: "") }
    var category by remember { mutableStateOf(initialMedicine?.category ?: MedicineCategory.TABLET) }
    var frequencyType by remember { mutableStateOf(initialSchedule?.frequencyType ?: FrequencyType.ONCE_DAILY) }
    var selectedTime by remember(initialSchedule?.scheduledTimes) {
        mutableStateOf(formatTimeForDisplay(initialSchedule?.scheduledTimes?.firstOrNull() ?: "10:00 AM"))
    }
    var startDateEpochMs by remember(initialSchedule?.startDateEpochMs) {
        mutableLongStateOf(initialSchedule?.startDateEpochMs ?: System.currentTimeMillis())
    }
    val startDateText = remember(startDateEpochMs) {
        SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(startDateEpochMs))
    }
    var selectedColorHex by remember { mutableStateOf(initialMedicine?.colorHex ?: "#0284C7") }
    var nameError by remember { mutableStateOf(false) }
    var dosageError by remember { mutableStateOf(false) }

    var showTypeMenu by remember { mutableStateOf(false) }
    var showFreqMenu by remember { mutableStateOf(false) }

    val fieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = if (isDark) Color(0x99081D25) else Color(0xCCFFFFFF),
        unfocusedContainerColor = if (isDark) Color(0x66081D25) else Color(0xB3F8FCFC),
        focusedBorderColor = ShowcaseTealPrimary,
        unfocusedBorderColor = if (isDark) Color(0x4D27B9C0) else MediFlowBorderSubtle
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = if (initialMedicine == null) "Add Medicine" else "Edit Medicine",
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

        // Single Cohesive Frosted Glass Form Card matching Screen 4
        ShowcaseGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Hero 3D Pill Preview Box with Camera Icon at top-right
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(36.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(if (isDark) Color(0x3327B9C0) else Color(0xFFE0F2F4))
                            .clickable {
                                // Cycle pill visual style on tap
                                selectedColorHex = when (selectedColorHex) {
                                    "#0284C7" -> "#DF6666"
                                    "#DF6666" -> "#E9A23B"
                                    else -> "#0284C7"
                                }
                            }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = "Change Pill Style",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        PillGraphic(
                            colorHex = selectedColorHex,
                            size = 96.dp,
                            isSquareTile = true
                        )
                    }
                }

                // 1. Medicine Name
                Column {
                    Text(
                        text = "Medicine Name",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = it.isBlank()
                        },
                        placeholder = { Text("e.g. Paracetamol", fontSize = 14.sp) },
                        isError = nameError,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_medicine_name")
                    )
                    if (nameError) {
                        Text(
                            text = "Enter a medicine name.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                // 2. Dosage
                Column {
                    Text(
                        text = "Dosage",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = dosage,
                        onValueChange = {
                            dosage = it
                            dosageError = it.isBlank()
                        },
                        placeholder = { Text("e.g. 500 mg", fontSize = 14.sp) },
                        isError = dosageError,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = fieldColors,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (dosageError) {
                        Text(
                            text = "Enter the prescribed dosage.",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }
                }

                // 3. Medicine Type (Dropdown field matching Screen 4)
                Column {
                    Text(
                        text = "Medicine Type",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDark) Color(0x66081D25) else Color(0xB3F8FCFC))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0x4D27B9C0) else MediFlowBorderSubtle,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { showTypeMenu = true }
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = category.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        }
                        DropdownMenu(
                            expanded = showTypeMenu,
                            onDismissRequest = { showTypeMenu = false }
                        ) {
                            MedicineCategory.entries.forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.displayName) },
                                    onClick = {
                                        category = cat
                                        showTypeMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 4. Frequency (Dropdown field matching Screen 4)
                Column {
                    Text(
                        text = "Frequency",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(if (isDark) Color(0x66081D25) else Color(0xB3F8FCFC))
                                .border(
                                    1.dp,
                                    if (isDark) Color(0x4D27B9C0) else MediFlowBorderSubtle,
                                    RoundedCornerShape(14.dp)
                                )
                                .clickable { showFreqMenu = true }
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = frequencyType.displayName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f)
                            )
                        }
                        DropdownMenu(
                            expanded = showFreqMenu,
                            onDismissRequest = { showFreqMenu = false }
                        ) {
                            FrequencyType.entries.take(4).forEach { freq ->
                                DropdownMenuItem(
                                    text = { Text(freq.displayName) },
                                    onClick = {
                                        frequencyType = freq
                                        showFreqMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                // 5. Time Field with Clock Icon matching Screen 4
                Column {
                    Text(
                        text = "Time",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0x66081D25) else Color(0xB3F8FCFC))
                            .border(
                                1.dp,
                                if (isDark) Color(0x4D27B9C0) else MediFlowBorderSubtle,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                val (hour, minute) = parseTimeForPicker(selectedTime)
                                TimePickerDialog(
                                    context,
                                    { _, selectedHour, selectedMinute ->
                                        selectedTime = formatTimeForPicker(selectedHour, selectedMinute)
                                    },
                                    hour,
                                    minute,
                                    false
                                ).show()
                            }
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedTime,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.AccessTime,
                            contentDescription = "Select Time",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // 6. Start Date Field with Calendar Icon matching Screen 4
                Column {
                    Text(
                        text = "Start Date",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0x66081D25) else Color(0xB3F8FCFC))
                            .border(
                                1.dp,
                                if (isDark) Color(0x4D27B9C0) else MediFlowBorderSubtle,
                                RoundedCornerShape(14.dp)
                            )
                            .clickable {
                                val selectedDate = Calendar.getInstance().apply { timeInMillis = startDateEpochMs }
                                DatePickerDialog(
                                    context,
                                    { _, year, month, day ->
                                        startDateEpochMs = Calendar.getInstance().apply {
                                            timeInMillis = startDateEpochMs
                                            set(Calendar.YEAR, year)
                                            set(Calendar.MONTH, month)
                                            set(Calendar.DAY_OF_MONTH, day)
                                            set(Calendar.HOUR_OF_DAY, 0)
                                            set(Calendar.MINUTE, 0)
                                            set(Calendar.SECOND, 0)
                                            set(Calendar.MILLISECOND, 0)
                                        }.timeInMillis
                                    },
                                    selectedDate.get(Calendar.YEAR),
                                    selectedDate.get(Calendar.MONTH),
                                    selectedDate.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = startDateText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Select Start Date",
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.55f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // "Save Medicine" Prominent Button inside the glass card matching Screen 4
                ShowcasePillButton(
                    text = "Save Medicine",
                    onClick = {
                        if (name.isBlank()) {
                            nameError = true
                            dosageError = dosage.isBlank()
                            return@ShowcasePillButton
                        }
                        if (dosage.isBlank()) {
                            dosageError = true
                            return@ShowcasePillButton
                        }

                        val med = initialMedicine?.copy(
                            name = name.trim(),
                            category = category,
                            strength = dosage.trim(),
                            doseAmount = 1.0,
                            doseUnit = category.displayName.lowercase(),
                            mealInstruction = MealInstruction.AFTER_FOOD,
                            colorHex = selectedColorHex,
                            remainingQuantity = 30,
                            refillThreshold = 5
                        ) ?: MedicineEntity(
                            name = name.trim(),
                            category = category,
                            strength = dosage.trim(),
                            doseAmount = 1.0,
                            doseUnit = category.displayName.lowercase(),
                            mealInstruction = MealInstruction.AFTER_FOOD,
                            colorHex = selectedColorHex,
                            remainingQuantity = 30,
                            refillThreshold = 5
                        )

                        val sched = initialSchedule?.copy(
                            frequencyType = frequencyType,
                            scheduledTimes = listOf(selectedTime),
                            startDateEpochMs = startDateEpochMs,
                            daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7),
                            intervalHours = 8
                        ) ?: MedicineScheduleEntity(
                            medicineId = 0,
                            frequencyType = frequencyType,
                            scheduledTimes = listOf(selectedTime),
                            startDateEpochMs = startDateEpochMs,
                            daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7),
                            intervalHours = 8
                        )

                        onSave(med, sched)
                    },
                    testTag = "btn_save_medicine"
                )
            }
        }

        Spacer(modifier = Modifier.height(40.dp))
    }

}

private fun parseTimeForPicker(value: String): Pair<Int, Int> {
    val match = Regex("^\\s*(\\d{1,2}):(\\d{2})(?:\\s*([AaPp][Mm]))?\\s*$").matchEntire(value)
        ?: return 10 to 0
    val rawHour = match.groupValues[1].toIntOrNull() ?: return 10 to 0
    val minute = match.groupValues[2].toIntOrNull()?.takeIf { it in 0..59 } ?: return 10 to 0
    val meridiem = match.groupValues[3].uppercase(Locale.ROOT)
    val hour = if (meridiem.isNotEmpty()) {
        if (rawHour !in 1..12) return 10 to 0
        (rawHour % 12) + if (meridiem == "PM") 12 else 0
    } else {
        rawHour
    }.takeIf { it in 0..23 } ?: return 10 to 0
    return hour to minute
}

private fun formatTimeForDisplay(value: String): String {
    val (hour, minute) = parseTimeForPicker(value)
    return formatTimeForPicker(hour, minute)
}

private fun formatTimeForPicker(hour: Int, minute: Int): String {
    val time = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
    }.time
    return SimpleDateFormat("h:mm a", Locale.getDefault()).format(time)
}
