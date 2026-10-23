package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class UserProfileRepositoryRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun saveAndGetUserProfile_authenticatedOwner_succeeds() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = UserProfileRepository(firestore, auth)

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveUserProfile(
                displayName = "Alex Johnson",
                email = ALICE_EMAIL,
                phone = "+1 555-0199",
                age = "29",
                bloodGroup = "O+",
                weightKg = "72 kg",
                emergencyContact = "+1 555-0100"
            )
        }
        assertTrue(saveResult.isSuccess)

        val getResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.getUserProfileById(aliceUid)
        }
        assertTrue(getResult.isSuccess)
        val profile = getResult.getOrNull()
        assertNotNull(profile)
        assertEquals("Alex Johnson", profile?.displayName)
        assertEquals("O+", profile?.bloodGroup)
    }

    @Test
    fun observeUserProfile_authenticatedOwner_emitsRealtimeUpdates() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val repository = UserProfileRepository(firestore, auth)

        withTimeout(DEFAULT_TIMEOUT_MS) {
            repository.saveUserProfile(
                displayName = "Alex Updated",
                email = ALICE_EMAIL
            ).getOrThrow()
        }

        val emitted = withTimeout(FLOW_TIMEOUT_MS) {
            repository.observeUserProfile(aliceUid).first { it != null && it.displayName == "Alex Updated" }
        }
        assertNotNull(emitted)
        assertEquals("Alex Updated", emitted?.displayName)
    }

    @Test
    fun getUserProfileById_crossUserAccess_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val aliceRepo = UserProfileRepository(firestore, auth)
        withTimeout(DEFAULT_TIMEOUT_MS) {
            aliceRepo.saveUserProfile(
                displayName = "Alex Johnson",
                email = ALICE_EMAIL
            ).getOrThrow()
        }

        signInTestUser(BOB_EMAIL)
        val bobRepo = UserProfileRepository(firestore, auth)
        val crossResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobRepo.getUserProfileById(aliceUid)
        }
        assertTrue(crossResult.isFailure)
        val fsException = crossResult.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull(fsException)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsException?.code)
    }

    @Test
    fun observeUserProfile_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        auth.signOut()
        val repository = UserProfileRepository(firestore, auth)

        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                repository.observeUserProfile(aliceUid).first()
            }
            fail("Expected exception for unauthenticated user")
        } catch (e: Exception) {
            val fsException = checkNotNull(e.unwrapFirestoreException()) {
                "Expected nested FirebaseFirestoreException, got $e"
            }
            assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, fsException.code)
        }
    }

    private companion object {
        const val ALICE_EMAIL = "alice@mediflow.test"
        const val BOB_EMAIL = "bob@mediflow.test"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 3000L
    }
}
