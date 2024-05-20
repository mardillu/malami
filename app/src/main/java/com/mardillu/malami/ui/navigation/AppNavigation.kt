package com.mardillu.malami.ui.navigation

import androidx.navigation.NavHostController

/**
 * Created on 20/05/2024 at 10:09 am
 * @author mardillu
 */
class AppNavigation(
    val navController: NavHostController,
) {

    fun back(): Boolean {
        return navController.popBackStack()
    }

    fun gotoHome() {
        navController.navigate(NavRoutes.Home.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun gotoLoginSignup() {
        navController.navigate(NavRoutes.Login.route) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun goToOnboarding() {
        navController.navigate(NavRoutes.Onboarding.route) {
            popUpTo(0) { inclusive = true }
        }
    }


//    fun gotoForgotPassword() {
//        navController.navigate(NavRoutes.ForgotPassword.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//    fun gotoResetPassword() {
//        navController.navigate(NavRoutes.ResetPassword.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//    fun gotoProfile() {
//        navController.navigate(NavRoutes.Profile.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//
//    fun gotoEditProfile() {
//        navController.navigate(NavRoutes.EditProfile.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//    fun gotoChangePassword() {
//        navController.navigate(NavRoutes.ChangePassword.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//    fun gotoSettings() {
//        navController.navigate(NavRoutes.Settings.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//    fun gotoNotifications() {
//        navController.navigate(NavRoutes.Notifications.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//    fun gotoHelp() {
//        navController.navigate(NavRoutes.Help.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//    fun gotoAbout() {
//        navController.navigate(NavRoutes.About.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//    fun gotoContact() {
//        navController.navigate(NavRoutes.Contact.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//    fun gotoPrivacyPolicy() {
//        navController.navigate(NavRoutes.PrivacyPolicy.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }
//
//    fun gotoTermsAndConditions() {
//        navController.navigate(NavRoutes.TermsAndConditions.route) {
//            popUpTo(0) { inclusive = true }
//        }
//    }

}