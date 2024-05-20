package com.mardillu.malami.ui.navigation

import androidx.annotation.DrawableRes

/**
 * Created on 20/05/2024 at 10:10 am
 * @author mardillu
 */
sealed class NavRoutes(val route: String) {
    object Login : NavRoutes("/login")
    object Onboarding : NavRoutes("/onboarding")
    object Home : NavRoutes("/home")
}