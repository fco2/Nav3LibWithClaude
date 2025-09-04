package com.chuka.nav3libwithclaude.presentation.navigation

import com.chuka.nav3libwithclaude.domain.models.ToastData

// Sealed class for type-safe navigation
sealed class NavigationRoute {
    // Route with arguments
    data class HumanScreenRoute(
        val fromScreen: String? = null,
        val toastData: ToastData? = null
    ) : NavigationRoute() {
        companion object Companion {
            const val ROUTE = "human_screen"
        }
    }

    // Routes with arguments - now data classes passing Human ID
    data class BoyScreenRoute(
        val humanId: Long? = null
    ) : NavigationRoute() {
        companion object Companion {
            const val ROUTE = "boy_screen"
        }
    }

    data class GirlScreenRoute(
        val humanId: Long? = null
    ) : NavigationRoute() {
        companion object {
            const val ROUTE = "girl_screen"
        }
    }
}