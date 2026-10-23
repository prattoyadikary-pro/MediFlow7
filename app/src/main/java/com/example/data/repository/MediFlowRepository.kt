package com.example.data.repository

import android.content.Context
import com.example.alarm.MedicineAlarmScheduler
import com.example.data.local.AppDatabase
import com.example.data.local.MediFlowPreferences
import com.example.data.local.entity.DoseInstanceEntity
import com.example.data.local.entity.HydrationLogEntity
import com.example.data.local.entity.HydrationProfileEntity
import com.example.data.local.entity.MedicineEntity
import com.example.data.local.entity.MedicineScheduleEntity
import com.example.data.model.AdherenceSummary
import com.example.data.model.BeverageType
import com.example.data.model.DailyHydrationSummary
import com.example.data.model.DoseItem
import com.example.data.model.DoseStatus
import com.example.data.model.FrequencyType
import com.example.data.model.MedicineWithSchedule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import org.json.JSONArray
import org.json.JSONObject
import java.util.Calendar

class MediFlowRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val medicineDao = db.medicineDao()
    private val doseDao = db.doseDao()
    private val hydrationDao = db.hydrationDao()
    private val alarmScheduler = MedicineAlarmScheduler(context)
    val preferences = MediFlowPreferences(context)

    suspend fun ensureInitialShowcaseData() {
        val existingMeds = medicineDao.getAllMedicines().first()
        if (existingMeds.isNotEmpty()) return

        val now = System.currentTimeMillis()
        val todayStartCal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        fun todayAt(hour: Int, minute: Int): Long {
            return (todayStartCal.clone() as Calendar).apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
            }.timeInMillis
        }

        // 1. Paracetamol 500 mg • Tablet (8:00 AM • Daily, Active)
        val paracetamolId = medicineDao.insertMedicine(
            MedicineEntity(
                name = "Paracetamol",
                category = com.example.data.model.MedicineCategory.TABLET,
                strength = "500 mg",
                doseAmount = 1.0,
                doseUnit = "tablet",
                mealInstruction = com.example.data.model.MealInstruction.AFTER_FOOD,
                colorHex = "#0284C7",
                remainingQuantity = 24,
                refillThreshold = 5,
                isActive = true
            )
        )
        val paracetamolSchedId = medicineDao.insertSchedule(
            MedicineScheduleEntity(
                medicineId = paracetamolId,
                frequencyType = FrequencyType.TWICE_DAILY,
                scheduledTimes = listOf("08:00 AM", "08:00 PM"),
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7),
                startDateEpochMs = todayStartCal.timeInMillis
            )
        )

        // 2. Vitamin D3 1000 IU • Tablet (2:00 PM • Daily, Active)
        val vitaminDId = medicineDao.insertMedicine(
            MedicineEntity(
                name = "Vitamin D3",
                category = com.example.data.model.MedicineCategory.TABLET,
                strength = "1000 IU",
                doseAmount = 1.0,
                doseUnit = "tablet",
                mealInstruction = com.example.data.model.MealInstruction.WITH_FOOD,
                colorHex = "#E9A23B",
                remainingQuantity = 30,
                refillThreshold = 5,
                isActive = true
            )
        )
        val vitaminDSchedId = medicineDao.insertSchedule(
            MedicineScheduleEntity(
                medicineId = vitaminDId,
                frequencyType = FrequencyType.ONCE_DAILY,
                scheduledTimes = listOf("02:00 PM"),
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7),
                startDateEpochMs = todayStartCal.timeInMillis
            )
        )

        // 3. Amoxicillin 500 mg • Capsule (8:00 PM • 2x Daily, Active)
        val amoxId = medicineDao.insertMedicine(
            MedicineEntity(
                name = "Amoxicillin",
                category = com.example.data.model.MedicineCategory.CAPSULE,
                strength = "500 mg",
                doseAmount = 1.0,
                doseUnit = "capsule",
                mealInstruction = com.example.data.model.MealInstruction.AFTER_FOOD,
                colorHex = "#DF6666",
                remainingQuantity = 18,
                refillThreshold = 5,
                isActive = true
            )
        )
        val amoxSchedId = medicineDao.insertSchedule(
            MedicineScheduleEntity(
                medicineId = amoxId,
                frequencyType = FrequencyType.TWICE_DAILY,
                scheduledTimes = listOf("08:00 PM", "08:00 AM"),
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7),
                startDateEpochMs = todayStartCal.timeInMillis
            )
        )

        // 4. Omega 3 1000 mg • Capsule (9:00 AM • Daily, Paused)
        val omegaId = medicineDao.insertMedicine(
            MedicineEntity(
                name = "Omega 3",
                category = com.example.data.model.MedicineCategory.CAPSULE,
                strength = "1000 mg",
                doseAmount = 1.0,
                doseUnit = "capsule",
                mealInstruction = com.example.data.model.MealInstruction.WITH_FOOD,
                colorHex = "#E9A23B",
                remainingQuantity = 45,
                refillThreshold = 10,
                isActive = false
            )
        )
        medicineDao.insertSchedule(
            MedicineScheduleEntity(
                medicineId = omegaId,
                frequencyType = FrequencyType.ONCE_DAILY,
                scheduledTimes = listOf("09:00 AM"),
                daysOfWeek = listOf(1, 2, 3, 4, 5, 6, 7),
                startDateEpochMs = todayStartCal.timeInMillis
            )
        )

        // Seed 4 doses for today: 2 TAKEN, 2 PENDING (so Today's Progress = 2 of 4 doses, 50%)
        doseDao.insertDose(
            DoseInstanceEntity(
                medicineId = amoxId,
                scheduleId = amoxSchedId,
                scheduledEpochMs = todayAt(7, 30),
                actualActionEpochMs = todayAt(7, 35),
                status = DoseStatus.TAKEN
            )
        )
        doseDao.insertDose(
            DoseInstanceEntity(
                medicineId = vitaminDId,
                scheduleId = vitaminDSchedId,
                scheduledEpochMs = todayAt(9, 0),
                actualActionEpochMs = todayAt(9, 5),
                status = DoseStatus.TAKEN
            )
        )
        // Next Pending Dose: Paracetamol 500 mg • Tablet
        doseDao.insertDose(
            DoseInstanceEntity(
                medicineId = paracetamolId,
                scheduleId = paracetamolSchedId,
                scheduledEpochMs = now + 30 * 60 * 1000L,
                status = DoseStatus.PENDING
            )
        )
        // Second Pending Dose: Vitamin D3 • Tablet
        doseDao.insertDose(
            DoseInstanceEntity(
                medicineId = vitaminDId,
                scheduleId = vitaminDSchedId,
                scheduledEpochMs = now + 3 * 3600 * 1000L,
                status = DoseStatus.PENDING
            )
        )

        // Seed Hydration Profile (2,000 ml goal, 120 min = 2 hours interval) & 1,200 ml logged today (60%)
        hydrationDao.insertOrUpdateProfile(
            HydrationProfileEntity(
                dailyGoalMl = 2000,
                wakeTimeMinutes = 420,
                bedTimeMinutes = 1380,
                reminderIntervalMinutes = 120,
                notificationsEnabled = true
            )
        )
        val existingLogs = hydrationDao.getLogsForRangeSync(todayStartCal.timeInMillis, todayStartCal.timeInMillis + 86400000L)
        if (existingLogs.isEmpty()) {
            hydrationDao.insertLog(
                HydrationLogEntity(
                    amountMl = 500,
                    beverageType = BeverageType.WATER,
                    timestampEpochMs = todayAt(8, 30),
                    notes = "Morning water"
                )
            )
            hydrationDao.insertLog(
                HydrationLogEntity(
                    amountMl = 450,
                    beverageType = BeverageType.WATER,
                    timestampEpochMs = todayAt(11, 0),
                    notes = "Mid-morning hydration"
                )
            )
            hydrationDao.insertLog(
                HydrationLogEntity(
                    amountMl = 250,
                    beverageType = BeverageType.WATER,
                    timestampEpochMs = todayAt(13, 15),
                    notes = "Lunch glass"
                )
            )
        }
    }

    // Flow of all medicines with their schedule and refill warning
    val allMedicinesWithSchedule: Flow<List<MedicineWithSchedule>> = combine(
        medicineDao.getAllMedicines(),
        medicineDao.getAllSchedules()
    ) { medicines, schedules ->
        medicines.map { med ->
            val schedule = schedules.find { it.medicineId == med.id }
            MedicineWithSchedule(
                medicine = med,
                schedule = schedule
            )
        }
    }

    // Get today's start and end epoch milliseconds
    private fun getDayRange(epochMs: Long): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            timeInMillis = epochMs
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val start = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 1)
        val end = cal.timeInMillis
        return Pair(start, end)
    }

    // Today's dose items
    fun getDoseItemsForDay(dateEpochMs: Long): Flow<List<DoseItem>> {
        val (start, end) = getDayRange(dateEpochMs)
        return combine(
            doseDao.getDosesForRange(start, end),
            medicineDao.getAllMedicines()
        ) { doses, medicines ->
            doses.mapNotNull { dose ->
                val med = medicines.find { it.id == dose.medicineId }
                if (med != null) {
                    DoseItem(
                        instance = dose,
                        medicineName = med.name,
                        medicineCategory = med.category,
                        strength = med.strength,
                        doseAmount = med.doseAmount,
                        doseUnit = med.doseUnit,
                        mealInstruction = med.mealInstruction,
                        colorHex = med.colorHex,
                        remainingQuantity = med.remainingQuantity,
                        isRefillNeeded = med.remainingQuantity <= med.refillThreshold
                    )
                } else null
            }
        }
    }

    // Next scheduled pending dose for home hero card
    fun getNextPendingDoseItem(): Flow<DoseItem?> {
        val now = System.currentTimeMillis()
        return combine(
            doseDao.getNextPendingDose(now - 15 * 60 * 1000L), // include doses up to 15m overdue
            medicineDao.getAllMedicines()
        ) { nextDose, medicines ->
            if (nextDose != null) {
                val med = medicines.find { it.id == nextDose.medicineId && it.isActive }
                if (med != null) {
                    DoseItem(
                        instance = nextDose,
                        medicineName = med.name,
                        medicineCategory = med.category,
                        strength = med.strength,
                        doseAmount = med.doseAmount,
                        doseUnit = med.doseUnit,
                        mealInstruction = med.mealInstruction,
                        colorHex = med.colorHex,
                        remainingQuantity = med.remainingQuantity,
                        isRefillNeeded = med.remainingQuantity <= med.refillThreshold
                    )
                } else null
            } else null
        }
    }

    // Adherence summary for selected day
    fun getAdherenceSummaryForDay(dateEpochMs: Long): Flow<AdherenceSummary> {
        val (start, end) = getDayRange(dateEpochMs)
        return doseDao.getDosesForRange(start, end).combine(medicineDao.getAllMedicines()) { doses, _ ->
            val dueDoses = doses.filter { it.scheduledEpochMs <= System.currentTimeMillis() || it.status != DoseStatus.PENDING }
            val taken = dueDoses.count { it.status == DoseStatus.TAKEN }
            val missed = dueDoses.count { it.status == DoseStatus.MISSED }
            val skipped = dueDoses.count { it.status == DoseStatus.SKIPPED }
            AdherenceSummary(
                totalDue = dueDoses.size,
                takenCount = taken,
                missedCount = missed,
                skippedCount = skipped
            )
        }
    }

    // Add new medicine and generate schedule instances
    suspend fun addMedicine(
        medicine: MedicineEntity,
        schedule: MedicineScheduleEntity
    ): Long {
        val medicineId = medicineDao.insertMedicine(medicine)
        val scheduleWithId = schedule.copy(medicineId = medicineId)
        val scheduleId = medicineDao.insertSchedule(scheduleWithId)

        generateDoseInstances(
            medicine = medicine.copy(id = medicineId),
            schedule = scheduleWithId.copy(id = scheduleId),
            daysAhead = 14
        )
        return medicineId
    }

    // Update existing medicine & regenerate future doses
    suspend fun updateMedicine(
        medicine: MedicineEntity,
        schedule: MedicineScheduleEntity
    ) {
        medicineDao.updateMedicine(medicine)
        medicineDao.updateSchedule(schedule)

        // Cancel future alarms and regenerate
        val now = System.currentTimeMillis()
        val futureDoses = doseDao.getDosesForRangeSync(now, now + 30L * 24 * 3600 * 1000)
        futureDoses.filter { it.medicineId == medicine.id && it.status == DoseStatus.PENDING }.forEach {
            alarmScheduler.cancelDose(it.id)
        }
        doseDao.deleteFutureDosesForMedicine(medicine.id, now)

        if (medicine.isActive) {
            generateDoseInstances(medicine, schedule, daysAhead = 14)
        }
    }

    // Delete medicine and associated future alarms
    suspend fun deleteMedicine(medicineId: Long) {
        val now = System.currentTimeMillis()
        val futureDoses = doseDao.getDosesForRangeSync(now, now + 30L * 24 * 3600 * 1000)
        futureDoses.filter { it.medicineId == medicineId }.forEach {
            alarmScheduler.cancelDose(it.id)
        }
        medicineDao.deleteMedicineById(medicineId)
    }

    // Toggle active state
    suspend fun toggleMedicineActive(medicineId: Long, isActive: Boolean) {
        val med = medicineDao.getMedicineByIdSync(medicineId) ?: return
        val updatedMed = med.copy(isActive = isActive)
        medicineDao.updateMedicine(updatedMed)

        val schedule = medicineDao.getScheduleForMedicineSync(medicineId) ?: return
        val now = System.currentTimeMillis()

        if (!isActive) {
            val futureDoses = doseDao.getDosesForRangeSync(now, now + 30L * 24 * 3600 * 1000)
            futureDoses.filter { it.medicineId == medicineId }.forEach {
                alarmScheduler.cancelDose(it.id)
            }
            doseDao.deleteFutureDosesForMedicine(medicineId, now)
        } else {
            generateDoseInstances(updatedMed, schedule, daysAhead = 14)
        }
    }

    // Refill medicine quantity
    suspend fun refillMedicine(medicineId: Long, additionalQuantity: Int) {
        val med = medicineDao.getMedicineByIdSync(medicineId) ?: return
        val newQty = med.remainingQuantity + additionalQuantity
        medicineDao.updateRemainingQuantity(medicineId, newQty)
    }

    // Helper: Generate dose instances based on frequency schedule
    private suspend fun generateDoseInstances(
        medicine: MedicineEntity,
        schedule: MedicineScheduleEntity,
        daysAhead: Int = 14
    ) {
        val instances = mutableListOf<DoseInstanceEntity>()
        val cal = Calendar.getInstance()
        val now = System.currentTimeMillis()

        for (dayOffset in 0..daysAhead) {
            val dayCal = Calendar.getInstance().apply {
                timeInMillis = schedule.startDateEpochMs
                if (timeInMillis < now) {
                    timeInMillis = now
                }
                add(Calendar.DAY_OF_YEAR, dayOffset)
            }

            // Check if past end date
            if (schedule.endDateEpochMs != null && dayCal.timeInMillis > schedule.endDateEpochMs) {
                break
            }

            // Check if day of week matches (Calendar: Sunday=1, Monday=2 ... Saturday=7)
            val dayOfWeek = dayCal.get(Calendar.DAY_OF_WEEK)
            if (schedule.frequencyType == FrequencyType.SPECIFIC_DAYS && !schedule.daysOfWeek.contains(dayOfWeek)) {
                continue
            }

            when (schedule.frequencyType) {
                FrequencyType.ONCE_DAILY,
                FrequencyType.TWICE_DAILY,
                FrequencyType.THREE_TIMES_DAILY,
                FrequencyType.CUSTOM_TIMES,
                FrequencyType.SPECIFIC_DAYS -> {
                    for (timeStr in schedule.scheduledTimes) {
                        val (hour, minute) = parseScheduledTime(timeStr) ?: continue
                        val doseCal = (dayCal.clone() as Calendar).apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val doseEpochMs = doseCal.timeInMillis
                        if (doseEpochMs > now - 15 * 60 * 1000L) {
                            instances.add(
                                DoseInstanceEntity(
                                    medicineId = medicine.id,
                                    scheduleId = schedule.id,
                                    scheduledEpochMs = doseEpochMs,
                                    status = DoseStatus.PENDING
                                )
                            )
                        }
                    }
                }

                FrequencyType.EVERY_N_HOURS -> {
                    val interval = schedule.intervalHours.coerceAtLeast(1)
                    var hour = 8
                    while (hour < 23) {
                        val doseCal = (dayCal.clone() as Calendar).apply {
                            set(Calendar.HOUR_OF_DAY, hour)
                            set(Calendar.MINUTE, 0)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        val doseEpochMs = doseCal.timeInMillis
                        if (doseEpochMs > now - 15 * 60 * 1000L) {
                            instances.add(
                                DoseInstanceEntity(
                                    medicineId = medicine.id,
                                    scheduleId = schedule.id,
                                    scheduledEpochMs = doseEpochMs,
                                    status = DoseStatus.PENDING
                                )
                            )
                        }
                        hour += interval
                    }
                }
            }
        }

        // Insert and schedule alarms
        for (inst in instances) {
            val insertedId = doseDao.insertDose(inst)
            if (insertedId > 0 && inst.scheduledEpochMs > now) {
                alarmScheduler.scheduleDose(
                    doseId = insertedId,
                    medicineId = medicine.id,
                    medicineName = medicine.name,
                    scheduledEpochMs = inst.scheduledEpochMs
                )
            }
        }
    }

    private fun parseScheduledTime(value: String): Pair<Int, Int>? {
        val match = Regex("^\\s*(\\d{1,2}):(\\d{2})(?:\\s*([AaPp][Mm]))?\\s*$").matchEntire(value)
            ?: return null
        val rawHour = match.groupValues[1].toIntOrNull() ?: return null
        val minute = match.groupValues[2].toIntOrNull()?.takeIf { it in 0..59 } ?: return null
        val meridiem = match.groupValues[3].uppercase()
        val hour = if (meridiem.isNotEmpty()) {
            if (rawHour !in 1..12) return null
            (rawHour % 12) + if (meridiem == "PM") 12 else 0
        } else {
            rawHour
        }.takeIf { it in 0..23 } ?: return null
        return hour to minute
    }

    // Mark dose as taken
    suspend fun markDoseTaken(doseId: Long) {
        val dose = doseDao.getDoseByIdSync(doseId) ?: return
        if (dose.status != DoseStatus.TAKEN) {
            doseDao.updateDose(
                dose.copy(
                    status = DoseStatus.TAKEN,
                    actualActionEpochMs = System.currentTimeMillis()
                )
            )
            // Decrement remaining pill quantity
            val med = medicineDao.getMedicineByIdSync(dose.medicineId)
            if (med != null && med.remainingQuantity > 0) {
                val newQty = (med.remainingQuantity - 1).coerceAtLeast(0)
                medicineDao.updateRemainingQuantity(med.id, newQty)
            }
            alarmScheduler.cancelDose(doseId)
        }
    }

    // Snooze dose
    suspend fun snoozeDose(doseId: Long, customMinutes: Int? = null) {
        val dose = doseDao.getDoseByIdSync(doseId) ?: return
        val minutes = customMinutes ?: preferences.snoozeMinutes.first()
        val snoozeTime = System.currentTimeMillis() + (minutes * 60 * 1000L)

        doseDao.updateDose(
            dose.copy(
                status = DoseStatus.SNOOZED,
                notes = "Snoozed for $minutes min"
            )
        )

        val med = medicineDao.getMedicineByIdSync(dose.medicineId)
        val medName = med?.name ?: "Medicine"
        alarmScheduler.scheduleDose(
            doseId = dose.id,
            medicineId = dose.medicineId,
            medicineName = medName,
            scheduledEpochMs = snoozeTime
        )
    }

    // Skip dose with optional reason
    suspend fun skipDose(doseId: Long, reason: String? = null) {
        val dose = doseDao.getDoseByIdSync(doseId) ?: return
        doseDao.updateDose(
            dose.copy(
                status = DoseStatus.SKIPPED,
                skipReason = reason ?: "User skipped",
                actualActionEpochMs = System.currentTimeMillis()
            )
        )
        alarmScheduler.cancelDose(doseId)
    }

    // Hydration functions
    fun getHydrationProfile(): Flow<HydrationProfileEntity> = flow {
        var profile = hydrationDao.getProfileSync()
        if (profile == null) {
            profile = HydrationProfileEntity(
                dailyGoalMl = 2000,
                wakeTimeMinutes = 420,
                bedTimeMinutes = 1380,
                reminderIntervalMinutes = 90
            )
            hydrationDao.insertOrUpdateProfile(profile)
        }
        emit(profile)
    }

    fun getTodayHydrationSummary(): Flow<DailyHydrationSummary> {
        val (start, end) = getDayRange(System.currentTimeMillis())
        return combine(
            hydrationDao.getLogsForRange(start, end),
            getHydrationProfile()
        ) { logs, profile ->
            val totalMl = logs.sumOf { it.amountMl }
            DailyHydrationSummary(
                dateEpochMs = start,
                totalAmountMl = totalMl,
                goalMl = profile.dailyGoalMl,
                logsCount = logs.size
            )
        }
    }

    fun getHydrationLogsForDay(dateEpochMs: Long): Flow<List<HydrationLogEntity>> {
        val (start, end) = getDayRange(dateEpochMs)
        return hydrationDao.getLogsForRange(start, end)
    }

    suspend fun logWaterIntake(amountMl: Int, beverageType: BeverageType = BeverageType.WATER, notes: String? = null): Long {
        val log = HydrationLogEntity(
            amountMl = amountMl,
            beverageType = beverageType,
            timestampEpochMs = System.currentTimeMillis(),
            notes = notes
        )
        return hydrationDao.insertLog(log)
    }

    suspend fun deleteHydrationLog(logId: Long) {
        hydrationDao.deleteLog(logId)
    }

    suspend fun updateHydrationProfile(profile: HydrationProfileEntity) {
        hydrationDao.insertOrUpdateProfile(profile)
    }

    // Data Export JSON
    suspend fun exportDataAsJson(): String {
        val medicines = medicineDao.getAllMedicines().first()
        val schedules = medicineDao.getAllSchedules().first()
        val (start, end) = Pair(0L, System.currentTimeMillis() + 30L * 24 * 3600 * 1000)
        val doses = doseDao.getDosesForRangeSync(start, end)
        val hydrationLogs = hydrationDao.getLogsForRangeSync(0L, System.currentTimeMillis())
        val profile = hydrationDao.getProfileSync()

        val root = JSONObject()
        root.put("version", 1)
        root.put("exportTime", System.currentTimeMillis())

        val medArray = JSONArray()
        for (m in medicines) {
            val obj = JSONObject().apply {
                put("id", m.id)
                put("name", m.name)
                put("category", m.category.name)
                put("strength", m.strength)
                put("doseAmount", m.doseAmount)
                put("doseUnit", m.doseUnit)
                put("remainingQuantity", m.remainingQuantity)
                put("refillThreshold", m.refillThreshold)
            }
            medArray.put(obj)
        }
        root.put("medicines", medArray)

        val hydArray = JSONArray()
        for (h in hydrationLogs) {
            val obj = JSONObject().apply {
                put("amountMl", h.amountMl)
                put("beverage", h.beverageType.name)
                put("timestamp", h.timestampEpochMs)
            }
            hydArray.put(obj)
        }
        root.put("hydrationLogs", hydArray)

        return root.toString(2)
    }

    // Clear all data
    suspend fun clearAllData() {
        val now = System.currentTimeMillis()
        val futureDoses = doseDao.getDosesForRangeSync(now, now + 365L * 24 * 3600 * 1000)
        futureDoses.forEach { alarmScheduler.cancelDose(it.id) }
        db.clearAllTables()
    }
}
