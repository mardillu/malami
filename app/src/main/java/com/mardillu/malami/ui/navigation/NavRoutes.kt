package com.mardillu.malami.ui.navigation

import androidx.annotation.DrawableRes

/**
 * Created on 20/05/2024 at 10:10 am
 * @author mardillu
 */
sealed class NavRoutes(val route: String) {
    data object Login : NavRoutes("/login")
    data object Onboarding : NavRoutes("/onboarding")
    data object Home : NavRoutes("/home")
    data object CourseList : NavRoutes("/course-list")
    data object Modules : NavRoutes("/modules")
    data object ModuleContent : NavRoutes("/module-content")

}