package com.example

import android.app.Application
import com.example.alarm.NotificationHelper
import com.example.data.repository.MediFlowRepository

class MediFlowApplication : Application() {

    lateinit var repository: MediFlowRepository
        private set

    override fun onCreate() {
        super.onCreate()
        repository = MediFlowRepository(this)
        NotificationHelper.createNotificationChannels(this)
    }
}
