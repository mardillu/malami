package com.mardillu.malami.ui.navigation

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.mardillu.malami.ui.auth.LoginSignupScreen
import com.mardillu.malami.ui.courses.course_modules.ModuleContentScreen
import com.mardillu.malami.ui.courses.course_modules.ModuleListScreen
import com.mardillu.malami.ui.courses.create.CreateCourseScreen
import com.mardillu.malami.ui.courses.list.CourseListScreen
import com.mardillu.malami.ui.courses.player.AudioPlayerScreen
import com.mardillu.malami.ui.courses.quiz.QuizResultScreen
import com.mardillu.malami.ui.courses.quiz.TakeQuizScreen
import com.mardillu.malami.ui.onboarding.OnboardingScreen
import com.mardillu.player_service.service.AudioPlayerService

/**
 * Created on 19/05/2024 at 7:33 pm
 * @author mardillu
 */
fun NavGraphBuilder.appNavGraph(
    navigation: AppNavigation,
    startService: () -> Unit
) {
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
            ModuleListScreen(navigation, courseId, hiltViewModel(), hiltViewModel())
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

    composable("${NavRoutes.TakeQuiz.route}/{sectionId}/{courseId}") { backStackEntry ->
        val sectionId = backStackEntry.arguments?.getString("sectionId")
        val courseId = backStackEntry.arguments?.getString("courseId")
        TakeQuizScreen(
            navigation = navigation,
            viewModel = hiltViewModel(),
            quizViewModel = hiltViewModel(),
            courseId = courseId,
            sectionId = sectionId
        )
    }

    composable("${NavRoutes.QuizResult.route}/{passed}/{obtainableScore}/{obtainedScore}") { backStackEntry ->
        val passed = backStackEntry.arguments?.getString("passed")
        val obtainableScore = backStackEntry.arguments?.getString("obtainableScore")
        val obtainedScore = backStackEntry.arguments?.getString("obtainedScore")
        QuizResultScreen(
            navigation = navigation,
            passed = passed.toBoolean(),
            obtainableScore = obtainableScore,
            obtainedScore = obtainedScore,
            viewModel = hiltViewModel()
        )
    }

    composable("${NavRoutes.AudioPlayer.route}/{courseId}/{sectionId}/{moduleId}") { entry ->
        val courseId = entry.arguments?.getString("courseId")
        val sectionId = entry.arguments?.getString("sectionId")
        val moduleId = entry.arguments?.getString("moduleId")
        AudioPlayerScreen(
            courseId,
            sectionId,
            moduleId,
            startService,
            hiltViewModel()
        )
    }
}