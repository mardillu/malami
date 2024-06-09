package com.mardillu.malami.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.mardillu.malami.ui.service.AudioPlayerService

/**
 * Created on 20/05/2024 at 10:13 am
 * @author mardillu
 */

@Composable
fun MalamiNavHost(
    isLoggedIn: Boolean,
    audioPlayerService: AudioPlayerService
) {
    val appNavigation = AppNavigation(
        navController = rememberNavController(),
    )

    val startDestination = if (isLoggedIn) NavRoutes.CourseList.route else NavRoutes.Login.route

    NavHost(
        navController = appNavigation.navController,
        startDestination = startDestination
    ) {
       appNavGraph(appNavigation, audioPlayerService)
    }

}
