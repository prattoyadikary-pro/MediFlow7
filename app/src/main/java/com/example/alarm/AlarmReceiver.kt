package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.local.MediFlowPreferences
import com.example.data.model.DoseStatus
import com.example.data.model.MealInstruction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.Calendar

class AlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val doseId = intent.getLongExtra(NotificationHelper.EXTRA_DOSE_ID, -1L)
        val medicineId = intent.getLongExtra(NotificationHelper.EXTRA_MEDICINE_ID, -1L)
        if (doseId == -1L || medicineId == -1L) return

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val dose = db.doseDao().getDoseByIdSync(doseId)
                val medicine = db.medicineDao().getMedicineByIdSync(medicineId)

                if (dose != null && dose.status == DoseStatus.PENDING && medicine != null && medicine.isActive) {
                    val prefs = MediFlowPreferences(context)
                    val quietHoursEnabled = prefs.quietHoursEnabled.first()
                    var isQuiet = false

                    if (quietHoursEnabled) {
                        val startStr = prefs.quietHoursStart.first() // e.g. "22:00"
                        val endStr = prefs.quietHoursEnd.first() // e.g. "07:00"
                        isQuiet = isCurrentTimeInQuietHours(startStr, endStr)
                    }

                    if (!isQuiet) {
                        val doseInfo = "${medicine.doseAmount} ${medicine.doseUnit} • ${medicine.strength}".trim(' ', '•')
                        NotificationHelper.showMedicineNotification(
                            context = context,
                            doseId = dose.id,
                            medicineId = medicine.id,
                            medicineName = medicine.name,
                            doseInfo = doseInfo,
                            mealInstruction = medicine.mealInstruction
                        )
                    }
                }
            } catch (e: Exception) {
                // Log or handle gracefully
            } finally {
                pendingResult.finish()
            }
        }
    }

    private fun isCurrentTimeInQuietHours(startStr: String, endStr: String): Boolean {
        try {
            val cal = Calendar.getInstance()
            val currentMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

            val startParts = startStr.split(":").map { it.toInt() }
            val endParts = endStr.split(":").map { it.toInt() }
            val startMin = startParts[0] * 60 + startParts[1]
            val endMin = endParts[0] * 60 + endParts[1]

            return if (startMin <= endMin) {
                currentMinutes in startMin..endMin
            } else {
                currentMinutes >= startMin || currentMinutes < endMin
            }
        } catch (e: Exception) {
            return false
        }
    }
}
