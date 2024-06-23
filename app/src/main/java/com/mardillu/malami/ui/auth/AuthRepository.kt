package com.mardillu.malami.ui.auth

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created on 22/06/2024 at 8:19 pm
 * @author mardillu
 */
class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {

    suspend fun loginWithGoogle(account: GoogleSignInAccount): Boolean {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
       val authResult = firebaseAuth.signInWithCredential(credential).await()
        return authResult.additionalUserInfo?.isNewUser == true
    }
}