package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import com.example.data.local.MediFlowPreferences
import com.example.data.model.DoseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class NotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action ?: return
        val doseId = intent.getLongExtra(NotificationHelper.EXTRA_DOSE_ID, -1L)
        val medicineId = intent.getLongExtra(NotificationHelper.EXTRA_MEDICINE_ID, -1L)

        if (doseId == -1L) return

        // Immediately dismiss the notification
        NotificationHelper.dismissNotification(context, doseId.toInt())

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val db = AppDatabase.getDatabase(context)
                val doseDao = db.doseDao()
                val medicineDao = db.medicineDao()
                val dose = doseDao.getDoseByIdSync(doseId) ?: return@launch

                when (action) {
                    NotificationHelper.ACTION_MARK_TAKEN -> {
                        // Prevent duplicate actions
                        if (dose.status != DoseStatus.TAKEN) {
                            doseDao.updateDose(
                                dose.copy(
                                    status = DoseStatus.TAKEN,
                                    actualActionEpochMs = System.currentTimeMillis()
                                )
                            )

                            // Decrement remaining medicine pill count if applicable
                            val medicine = medicineDao.getMedicineByIdSync(dose.medicineId)
                            if (medicine != null && medicine.remainingQuantity > 0) {
                                val newQty = (medicine.remainingQuantity - 1).coerceAtLeast(0)
                                medicineDao.updateRemainingQuantity(medicine.id, newQty)
                            }
                        }
                    }

                    NotificationHelper.ACTION_SNOOZE -> {
                        val prefs = MediFlowPreferences(context)
                        val snoozeMinutes = prefs.snoozeMinutes.first()
                        val snoozeTimeEpochMs = System.currentTimeMillis() + (snoozeMinutes * 60 * 1000L)

                        val medicine = medicineDao.getMedicineByIdSync(dose.medicineId)
                        val medicineName = medicine?.name ?: "Medicine"

                        // Mark as SNOOZED and reschedule
                        doseDao.updateDose(
                            dose.copy(
                                status = DoseStatus.SNOOZED,
                                notes = "Snoozed for $snoozeMinutes minutes"
                            )
                        )

                        val scheduler = MedicineAlarmScheduler(context)
                        scheduler.scheduleDose(
                            doseId = dose.id,
                            medicineId = dose.medicineId,
                            medicineName = medicineName,
                            scheduledEpochMs = snoozeTimeEpochMs
                        )
                    }
                }
            } catch (e: Exception) {
                // Handle or log gracefully
            } finally {
                pendingResult.finish()
            }
        }
    }
}
