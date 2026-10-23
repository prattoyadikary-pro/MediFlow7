package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.remote.OperationType
import com.example.data.remote.UserProfile
import com.example.data.remote.handleFirestoreError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class UserProfileRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeUserProfile(userId: String = requireUserId()): Flow<UserProfile?> = flow {
        val path = "users/$userId"
        val docRef = db.collection("users").document(userId)
        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfile::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else {
                        null
                    }
                }
                .catch { error ->
                    if (error is Exception) {
                        handleFirestoreError(error, OperationType.GET, path)
                    }
                    throw error
                }
        )
    }

    suspend fun getUserProfileById(targetUserId: String): Result<UserProfile?> {
        val docRef = db.collection("users").document(targetUserId)
        return try {
            val snapshot = docRef.get().await()
            val profile = if (snapshot.exists()) {
                snapshot.toObject(
                    UserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
            } else {
                null
            }
            Result.success(profile)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.GET, docRef.path)
            Result.failure(e)
        }
    }

    suspend fun saveUserProfile(
        displayName: String,
        email: String = "",
        phone: String = "",
        age: String = "",
        gender: String = "",
        bloodGroup: String = "",
        weightKg: String = "",
        emergencyContact: String = "",
        avatarUrl: String = ""
    ): Result<UserProfile> {
        val uid = requireUserId()
        val docRef = db.collection("users").document(uid)
        return try {
            val existingSnap = docRef.get().await()
            if (existingSnap.exists()) {
                val existing = existingSnap.toObject(
                    UserProfile::class.java,
                    DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                )
                val updated = UserProfile(
                    id = uid,
                    userId = uid,
                    displayName = displayName.trim().ifBlank { "Alex Johnson" }.take(100),
                    email = email.trim().take(150),
                    phone = phone.trim().take(40),
                    age = age.trim().take(10),
                    gender = gender.trim().take(20),
                    bloodGroup = bloodGroup.trim().take(15),
                    weightKg = weightKg.trim().take(20),
                    emergencyContact = emergencyContact.trim().take(100),
                    avatarUrl = avatarUrl.trim().take(500),
                    createdAt = existing?.createdAt,
                    updatedAt = null
                )
                docRef.set(updated).await()
                Result.success(updated)
            } else {
                val created = UserProfile(
                    id = uid,
                    userId = uid,
                    displayName = displayName.trim().ifBlank { "Alex Johnson" }.take(100),
                    email = email.trim().take(150),
                    phone = phone.trim().take(40),
                    age = age.trim().take(10),
                    gender = gender.trim().take(20),
                    bloodGroup = bloodGroup.trim().take(15),
                    weightKg = weightKg.trim().take(20),
                    emergencyContact = emergencyContact.trim().take(100),
                    avatarUrl = avatarUrl.trim().take(500),
                    createdAt = null,
                    updatedAt = null
                )
                docRef.set(created).await()
                Result.success(created)
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, docRef.path)
            Result.failure(e)
        }
    }
}
