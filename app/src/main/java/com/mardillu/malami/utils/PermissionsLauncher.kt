package com.mardillu.malami.utils

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue

/**
 * Created on 15/06/2024 at 6:39 am
 * @author mardillu
 */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun RequestNotificationPermissions() {
    // State to track whether notification permission is granted
    var hasNotificationPermission by rememberSaveable { mutableStateOf(false) }

    // Request notification permission and update state based on the result
    val permissionResult = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { hasNotificationPermission = it }
    )

    // Request notification permission when the component is launched
    LaunchedEffect(key1 = true) {
        permissionResult.launch(Manifest.permission.POST_NOTIFICATIONS)
    }
}