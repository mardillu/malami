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
import com.mardillu.malami.ui.courses.course_modules.ModuleContentScreen
import com.mardillu.malami.ui.courses.course_modules.ModuleListScreen
import com.mardillu.malami.ui.courses.list.CourseListScreen
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

    composable(NavRoutes.CourseList.route) {
        CourseListScreen(navigation, hiltViewModel())
    }

    composable("${NavRoutes.Modules.route}/{courseId}") { backStackEntry ->
        val courseId = backStackEntry.arguments?.getString("courseId")
        courseId?.let {
            ModuleListScreen(navigation, courseId)
        }
    }
    composable("${NavRoutes.ModuleContent.route}/{moduleId}") { backStackEntry ->
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        moduleId?.let {
            ModuleContentScreen(navigation, moduleId)
        }
    }
}