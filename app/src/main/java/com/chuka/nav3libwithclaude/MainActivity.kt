package com.chuka.nav3libwithclaude

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.chuka.nav3libwithclaude.presentation.navigation.CustomNavDisplay
import com.chuka.nav3libwithclaude.presentation.navigation.DeepLinkHandler
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationAnimation
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationManager
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationRoute
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationTransition
import com.chuka.nav3libwithclaude.presentation.navigation.NotificationPermissionHandler
import com.chuka.nav3libwithclaude.ui.theme.Nav3LibWithClaudeTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var navigationManager: NavigationManager
    @Inject
    lateinit var deepLinkHandler: DeepLinkHandler
    @Inject
    lateinit var notificationPermissionHandler: NotificationPermissionHandler

    @androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize notification permission handler
        notificationPermissionHandler.initialize(this)

        // Handle deep link from intent
        handleDeepLinkIntent(intent)
        //enableEdgeToEdge()
        setContent {
            Nav3LibWithClaudeTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .statusBarsPadding(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CustomNavDisplay(
                        navigationManager = navigationManager,
                        startDestination = NavigationRoute.HumanScreenRoute(),
                        onExitApp = { finish() }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        // Handle deep link when app is already running
        setIntent(intent) // Important: update the activity's intent
        handleDeepLinkIntent(intent)
    }

    private fun handleDeepLinkIntent(intent: Intent) {
        val deepLinkRoute = deepLinkHandler.parseDeepLink(intent)
        deepLinkRoute?.let { route ->
            // Use special deep link animation
            val deepLinkTransition = NavigationTransition(
                enterAnimation = NavigationAnimation.SCALE,
                exitAnimation = NavigationAnimation.FADE,
                duration = 500
            )
            navigationManager.handleDeepLink(route, deepLinkTransition)
        }
    }

    // Helper method to request notification permission with callback
    fun requestNotificationPermission(onResult: (Boolean) -> Unit) {
        notificationPermissionHandler.requestNotificationPermission(this, onResult)
    }

    // Helper method to check if permission is granted
    fun hasNotificationPermission(): Boolean {
        return notificationPermissionHandler.hasNotificationPermission(this)
    }
}