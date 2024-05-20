package com.mardillu.malami.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mardillu.malami.data.model.UserPreferences
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created on 19/05/2024 at 1:21 pm
 * @author mardillu
 */
class PreferencesRepository @Inject constructor(private val firestore: FirebaseFirestore) {

    suspend fun saveUserPreferences(userPreferences: UserPreferences): Result<Unit> {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return Result.failure(Exception("User not authenticated"))

        return try {
            firestore.collection("userPreferences")
                .document(userId)
                .set(userPreferences)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}