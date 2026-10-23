package com.example.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.local.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED ||
            intent.action == "android.intent.action.QUICKBOOT_POWERON"
        ) {
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val db = AppDatabase.getDatabase(context)
                    val pendingDoses = db.doseDao().getAllPendingDosesSync()
                    val scheduler = MedicineAlarmScheduler(context)
                    val now = System.currentTimeMillis()

                    for (dose in pendingDoses) {
                        if (dose.scheduledEpochMs > now) {
                            val medicine = db.medicineDao().getMedicineByIdSync(dose.medicineId)
                            if (medicine != null && medicine.isActive) {
                                scheduler.scheduleDose(
                                    doseId = dose.id,
                                    medicineId = medicine.id,
                                    medicineName = medicine.name,
                                    scheduledEpochMs = dose.scheduledEpochMs
                                )
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Ignore or log
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
