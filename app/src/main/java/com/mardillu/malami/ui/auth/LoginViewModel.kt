package com.mardillu.malami.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.mardillu.malami.data.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Created on 19/05/2024 at 6:47 pm
 * @author mardillu
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val authRepository: AuthRepository,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> get() = _authState

    fun handleGoogleSignInResult(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val account = task.getResult(Exception::class.java)
                val isNewUser = authRepository.loginWithGoogle(account)
                if (isNewUser) {
                    _authState.value = AuthState.Authenticated("signup")
                } else {
                    _authState.value = AuthState.Authenticated("login")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Google sign-in failed")
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                auth.signInWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Authenticated("login")
                preferencesManager.isLoggedIn = true
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signup(email: String, password: String, confirmPassword: String) {
        if (password != confirmPassword) {
            _authState.value = AuthState.Error("Passwords do not match")
            return
        }

        viewModelScope.launch {
            try {
                _authState.value = AuthState.Loading
                auth.createUserWithEmailAndPassword(email, password).await()
                _authState.value = AuthState.Authenticated("signup")
                preferencesManager.isLoggedIn = true
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    private fun checkAuthState() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated("")
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    private fun checkLocalAuthState() {
        if (preferencesManager.isLoggedIn && auth.currentUser != null) {
            _authState.value = AuthState.Authenticated("")
        } else {
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun logout() {
        auth.signOut()
        preferencesManager.isLoggedIn = false
        _authState.value = AuthState.Unauthenticated
    }
}


sealed class AuthState {
    data object Idle : AuthState()
    data object Loading : AuthState()
    data class Authenticated(val through: String) : AuthState()
    data object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
}

enum class AuthType {
    Email, Google
}