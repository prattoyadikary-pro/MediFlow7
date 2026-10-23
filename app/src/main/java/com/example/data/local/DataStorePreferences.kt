package com.example.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.UnitSystem
import com.example.data.remote.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "mediflow_preferences")

class MediFlowPreferences(private val context: Context) {

    companion object {
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode") // SYSTEM, LIGHT, DARK
        val KEY_UNIT_SYSTEM = stringPreferencesKey("unit_system") // METRIC, IMPERIAL
        val KEY_SNOOZE_MINUTES = intPreferencesKey("snooze_minutes") // 10
        val KEY_SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val KEY_VIBRATE_ENABLED = booleanPreferencesKey("vibrate_enabled")
        val KEY_HIDE_NAMES_LOCKSCREEN = booleanPreferencesKey("hide_names_lockscreen")
        val KEY_QUIET_HOURS_ENABLED = booleanPreferencesKey("quiet_hours_enabled")
        val KEY_QUIET_HOURS_START = stringPreferencesKey("quiet_hours_start") // "22:00"
        val KEY_QUIET_HOURS_END = stringPreferencesKey("quiet_hours_end") // "07:00"

        val KEY_USER_NAME = stringPreferencesKey("user_name")
        val KEY_USER_AGE = stringPreferencesKey("user_age")
        val KEY_USER_WEIGHT = stringPreferencesKey("user_weight")
        val KEY_USER_GENDER = stringPreferencesKey("user_gender")
        val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        val KEY_USER_PHONE = stringPreferencesKey("user_phone")
        val KEY_USER_BLOOD_GROUP = stringPreferencesKey("user_blood_group")
        val KEY_USER_EMERGENCY = stringPreferencesKey("user_emergency")
        val KEY_PROFILE_COMPLETED = booleanPreferencesKey("profile_completed")
    }

    val themeMode: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_THEME_MODE] ?: "SYSTEM"
    }

    val unitSystem: Flow<UnitSystem> = context.dataStore.data.map { preferences ->
        val name = preferences[KEY_UNIT_SYSTEM] ?: UnitSystem.METRIC.name
        try { UnitSystem.valueOf(name) } catch (e: Exception) { UnitSystem.METRIC }
    }

    val snoozeMinutes: Flow<Int> = context.dataStore.data.map { preferences ->
        preferences[KEY_SNOOZE_MINUTES] ?: 10
    }

    val soundEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_SOUND_ENABLED] ?: true
    }

    val vibrateEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_VIBRATE_ENABLED] ?: true
    }

    val hideNamesOnLockScreen: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_HIDE_NAMES_LOCKSCREEN] ?: false
    }

    val quietHoursEnabled: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_QUIET_HOURS_ENABLED] ?: false
    }

    val quietHoursStart: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_QUIET_HOURS_START] ?: "22:00"
    }

    val quietHoursEnd: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[KEY_QUIET_HOURS_END] ?: "07:00"
    }

    val userProfileFlow: Flow<UserProfile> = context.dataStore.data.map { preferences ->
        UserProfile(
            displayName = preferences[KEY_USER_NAME] ?: "",
            age = preferences[KEY_USER_AGE] ?: "",
            weightKg = preferences[KEY_USER_WEIGHT] ?: "",
            gender = preferences[KEY_USER_GENDER] ?: "",
            email = preferences[KEY_USER_EMAIL] ?: "",
            phone = preferences[KEY_USER_PHONE] ?: "",
            bloodGroup = preferences[KEY_USER_BLOOD_GROUP] ?: "",
            emergencyContact = preferences[KEY_USER_EMERGENCY] ?: ""
        )
    }

    val isProfileCompletedFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[KEY_PROFILE_COMPLETED] ?: !(preferences[KEY_USER_NAME].isNull_or_blank())
    }

    private fun String?.isNull_or_blank(): Boolean = this == null || this.trim().isEmpty()

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { it[KEY_THEME_MODE] = mode }
    }

    suspend fun setUnitSystem(unit: UnitSystem) {
        context.dataStore.edit { it[KEY_UNIT_SYSTEM] = unit.name }
    }

    suspend fun setSnoozeMinutes(minutes: Int) {
        context.dataStore.edit { it[KEY_SNOOZE_MINUTES] = minutes }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_SOUND_ENABLED] = enabled }
    }

    suspend fun setVibrateEnabled(enabled: Boolean) {
        context.dataStore.edit { it[KEY_VIBRATE_ENABLED] = enabled }
    }

    suspend fun setHideNamesOnLockScreen(hide: Boolean) {
        context.dataStore.edit { it[KEY_HIDE_NAMES_LOCKSCREEN] = hide }
    }

    suspend fun setQuietHours(enabled: Boolean, start: String, end: String) {
        context.dataStore.edit {
            it[KEY_QUIET_HOURS_ENABLED] = enabled
            it[KEY_QUIET_HOURS_START] = start
            it[KEY_QUIET_HOURS_END] = end
        }
    }

    suspend fun saveLocalProfile(
        displayName: String,
        age: String,
        weightKg: String,
        gender: String,
        email: String = "",
        phone: String = "",
        bloodGroup: String = "",
        emergencyContact: String = ""
    ) {
        context.dataStore.edit { preferences ->
            preferences[KEY_USER_NAME] = displayName
            preferences[KEY_USER_AGE] = age
            preferences[KEY_USER_WEIGHT] = weightKg
            preferences[KEY_USER_GENDER] = gender
            preferences[KEY_USER_EMAIL] = email
            preferences[KEY_USER_PHONE] = phone
            preferences[KEY_USER_BLOOD_GROUP] = bloodGroup
            preferences[KEY_USER_EMERGENCY] = emergencyContact
            preferences[KEY_PROFILE_COMPLETED] = displayName.isNotBlank()
        }
    }

    suspend fun clearLocalProfile() {
        context.dataStore.edit { preferences ->
            preferences.remove(KEY_USER_NAME)
            preferences.remove(KEY_USER_AGE)
            preferences.remove(KEY_USER_WEIGHT)
            preferences.remove(KEY_USER_GENDER)
            preferences.remove(KEY_USER_EMAIL)
            preferences.remove(KEY_USER_PHONE)
            preferences.remove(KEY_USER_BLOOD_GROUP)
            preferences.remove(KEY_USER_EMERGENCY)
            preferences[KEY_PROFILE_COMPLETED] = false
        }
    }
}
