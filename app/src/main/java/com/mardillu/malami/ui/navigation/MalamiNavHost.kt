package com.mardillu.malami.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mardillu.malami.ui.auth.AuthViewModel

/**
 * Created on 20/05/2024 at 10:13 am
 * @author mardillu
 */

@Composable
fun MalamiNavHost() {
    val appNavigation = AppNavigation(
        navController = rememberNavController(),
    )

    NavHost(
        navController = appNavigation.navController,
        startDestination = NavRoutes.Login.route
    ) {
       appNavGraph(appNavigation)
    }

}
