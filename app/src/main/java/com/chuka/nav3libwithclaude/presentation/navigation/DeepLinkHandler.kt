package com.chuka.nav3libwithclaude.presentation.navigation

// DeepLinkHandler.kt
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.graphics.Color
import com.chuka.nav3libwithclaude.domain.models.ToastData
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DeepLinkHandler @Inject constructor() {

    companion object {
        const val SCHEME = "navdisplay"
        const val HOST = "app"

        // Deep link patterns:
        // navdisplay://app/human
        // navdisplay://app/human?from=notification
        // navdisplay://app/boy/{id}
        // navdisplay://app/girl/{id}
        // navdisplay://app/boy/{id}?notification=true
    }

    fun parseDeepLink(intent: Intent): NavigationRoute? {
        val data = intent.data ?: return null
        return parseUri(data)
    }

    fun parseUri(uri: Uri): NavigationRoute? {
        if (uri.scheme != SCHEME || uri.host != HOST) return null

        val pathSegments = uri.pathSegments
        if (pathSegments.isEmpty()) return null

        return when (pathSegments[0]) {
            "human" -> {
                val fromParam = uri.getQueryParameter("from")
                val notificationParam = uri.getQueryParameter("notification")

                val toastData = if (notificationParam == "true") {
                    ToastData.create(
                        message = "Opened from notification!",
                        backgroundColor = 0xFF2196F3, // Blue
                        duration = ToastData.LENGTH_LONG
                    )
                } else null

                NavigationRoute.HumanScreenRoute(
                    fromScreen = fromParam,
                    toastData = toastData
                )
            }

            "boy" -> {
                val id = pathSegments.getOrNull(1)?.toLongOrNull()
                NavigationRoute.BoyScreenRoute(humanId = id)
            }

            "girl" -> {
                val id = pathSegments.getOrNull(1)?.toLongOrNull()
                NavigationRoute.GirlScreenRoute(humanId = id)
            }

            else -> null
        }
    }

    fun createDeepLink(route: NavigationRoute): String {
        return when (route) {
            is NavigationRoute.HumanScreenRoute -> {
                val baseUrl = "$SCHEME://$HOST/human"
                val params = mutableListOf<String>()

                route.fromScreen?.let { params.add("from=$it") }
                route.toastData?.let { params.add("notification=true") }

                if (params.isNotEmpty()) {
                    "$baseUrl?${params.joinToString("&")}"
                } else baseUrl
            }

            is NavigationRoute.BoyScreenRoute -> {
                if (route.humanId != null) {
                    "$SCHEME://$HOST/boy/${route.humanId}"
                } else {
                    "$SCHEME://$HOST/boy"
                }
            }

            is NavigationRoute.GirlScreenRoute -> {
                if (route.humanId != null) {
                    "$SCHEME://$HOST/girl/${route.humanId}"
                } else {
                    "$SCHEME://$HOST/girl"
                }
            }
        }
    }
}