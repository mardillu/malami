package com.mardillu.malami.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mardillu.malami.ui.auth.LoginSignupScreen
import com.mardillu.malami.ui.courses.course_modules.ModuleContentScreen
import com.mardillu.malami.ui.courses.course_modules.ModuleListScreen
import com.mardillu.malami.ui.courses.create.CreateCourseScreen
import com.mardillu.malami.ui.courses.list.CourseListScreen
import com.mardillu.malami.ui.onboarding.OnboardingScreen

/**
 * Created on 19/05/2024 at 7:33 pm
 * @author mardillu
 */
fun NavGraphBuilder.appNavGraph(navigation: AppNavigation) {
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
            ModuleListScreen(navigation, courseId, hiltViewModel())
        }
    }
    composable("${NavRoutes.ModuleContent.route}/{moduleId}/{sectionId}/{courseId}") { backStackEntry ->
        val moduleId = backStackEntry.arguments?.getString("moduleId")
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val courseId = backStackEntry.arguments?.getString("courseId")
        ModuleContentScreen(
            navigation,
            moduleId!!,
            sectionId!!,
            courseId!!,
            hiltViewModel(),
            hiltViewModel()
        )
    }

    composable(NavRoutes.CreateCourse.route) {
        CreateCourseScreen(
            navigation,
            hiltViewModel()
        )
    }
}