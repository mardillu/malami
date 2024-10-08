package com.mardillu.malami.ui.auth

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mardillu.auth_with_google.OneTapGoogleButton
import com.mardillu.malami.BuildConfig
import com.mardillu.malami.R
import com.mardillu.malami.ui.navigation.AppNavigation
import com.mardillu.malami.ui.navigation.NavRoutes
import kotlinx.coroutines.launch

/**
 * Created on 19/05/2024 at 6:46 pm
 * @author mardillu
 */
@Composable
fun LoginSignupScreen(
    navigation: AppNavigation,
    viewModel: AuthViewModel,
) {
    var isLoginScreen by rememberSaveable { mutableStateOf(true) }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val authState by viewModel.authState.collectAsState()
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        Column(
            modifier = Modifier
                .padding(it)
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_app_logo),
                contentDescription = "App Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OneTapGoogleButton(
                clientId = BuildConfig.LOGIN_WITH_GOOGLE_WEB_CLIENT_ID,
                rememberAccount = false,
                onTokenIdReceived = { tokenId ->
                    scope.launch {
                        Log.d("LoginSignupScreen", "onTokenIdReceived: $tokenId")
                        viewModel.finishGoogleSignIn(tokenId)
                    }
                },
                onUserReceived = {
                    scope.launch {
                        Log.d("LoginSignupScreen", "onUserReceived: $it")
                        //viewModel.handleGoogleSignInResult(it)
                    }
                },
                onDialogDismissed = {
                    scope.launch {
                        Log.d("LoginSignupScreen", "onDialogDismissed: $it")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalSeparatorWithOr()
            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Next,
                    keyboardType = KeyboardType.Email
                ),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image =
                        if (showPassword) R.drawable.ic_visibility else R.drawable.ic_visibility_off
                    IconButton(onClick = { showPassword = !showPassword }) {
                        Icon(painterResource(id = image), "Toggle Password Visibility")
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            )

            if (!isLoginScreen) {
                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isLoginScreen) {
                        viewModel.login(email, password)
                    } else {
                        viewModel.signup(email, password, confirmPassword)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = email.isNotEmpty() && password.isNotEmpty() && authState !is AuthState.Loading
            ) {
                Text(text = if (isLoginScreen) "Login" else "Sign Up", fontSize = 18.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isLoginScreen) "Don't have an account? Sign Up" else "Already have an account? Login",
                modifier = Modifier.clickable { isLoginScreen = !isLoginScreen },
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }
    }

    when {
        authState == AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Loading -> {

            }

            is AuthState.Error -> {
                val errorMessage = (authState as AuthState.Error).message
                scope.launch {
                    snackbarHostState.showSnackbar(errorMessage)
                }
            }

            is AuthState.Authenticated -> {
                if ((authState as AuthState.Authenticated).through == "login") {
                    navigation.gotToCourseList()
                } else if ((authState as AuthState.Authenticated).through == "signup") {
                    navigation.goToOnboarding(NavRoutes.Onboarding.route)
                }
            }

            else -> {}
        }
    }
}

@Composable
fun HorizontalSeparatorWithOr() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        )
        Text(
            text = "OR",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(horizontal = 8.dp)
        )
        HorizontalDivider(
            modifier = Modifier
                .weight(1f)
                .height(1.dp)
                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f))
        )
    }
}