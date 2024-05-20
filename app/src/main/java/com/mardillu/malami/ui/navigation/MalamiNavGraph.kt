package com.mardillu.malami.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mardillu.malami.ui.auth.AuthState
import com.mardillu.malami.ui.auth.AuthViewModel
import com.mardillu.malami.ui.auth.LoginSignupScreen
import com.mardillu.malami.ui.onboarding.OnboardingScreen

/**
 * Created on 19/05/2024 at 7:33 pm
 * @author mardillu
 */
fun NavGraphBuilder.appNavGraph(navigation: AppNavigation){
    composable(NavRoutes.Login.route) {
        LoginSignupScreen(navigation, hiltViewModel())
    }

    composable(NavRoutes.Home.route) {

    }

    composable(NavRoutes.Onboarding.route) {
        OnboardingScreen(navigation, hiltViewModel())
    }
}