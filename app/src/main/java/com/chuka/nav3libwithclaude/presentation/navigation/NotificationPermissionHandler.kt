package com.chuka.nav3libwithclaude.presentation.navigation

// NotificationPermissionHandler.kt
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import javax.inject.Singleton

// Helper ViewModels to provide dependencies to Composables
@HiltViewModel
class NotificationPermissionProvider @Inject constructor(
    val permissionHandler: NotificationPermissionHandler
) : ViewModel()

@HiltViewModel
class NotificationHelperProvider @Inject constructor(
    val notificationHelper: NotificationHelper
) : ViewModel()
{}

@Singleton
class NotificationPermissionHandler @Inject constructor() {

    private var permissionLauncher: ActivityResultLauncher<String>? = null
    private var onPermissionResult: ((Boolean) -> Unit)? = null

    fun initialize(activity: ComponentActivity) {
        permissionLauncher = activity.registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            onPermissionResult?.invoke(isGranted)
            onPermissionResult = null
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Notifications are always allowed below API 33
            true
        }
    }

    fun requestNotificationPermission(
        context: Context,
        onResult: (Boolean) -> Unit
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (hasNotificationPermission(context)) {
                onResult(true)
                return
            }

            onPermissionResult = onResult
            permissionLauncher?.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            // Always granted below API 33
            onResult(true)
        }
    }

    fun shouldShowRationale(activity: ComponentActivity): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.POST_NOTIFICATIONS
            )
        } else {
            false
        }
    }
}
