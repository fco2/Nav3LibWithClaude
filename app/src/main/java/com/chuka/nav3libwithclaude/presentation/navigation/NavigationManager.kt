package com.chuka.nav3libwithclaude.presentation.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface NavigationManager {
    fun navigateTo(route: NavigationRoute)

    fun navigateBack(): NavigationRoute?

    fun getCurrentRoute(): NavigationRoute?

    fun clearBackStack()

    fun navigateAndClearBackStack(route: NavigationRoute)

    fun getBackStackSize(): Int

    fun initializeWithRoot(route: NavigationRoute)

    fun getCurrentRouteFlow(): StateFlow<NavigationRoute?>

    val backStack: SnapshotStateList<NavigationRoute?>

    fun canNavigateBack(): Boolean
    fun shouldExitApp(): Boolean
}

class NavigationManagerImpl @Inject constructor() : NavigationManager {
    private val _backStack = mutableStateListOf<NavigationRoute?>()
    override val backStack: SnapshotStateList<NavigationRoute?> = _backStack
    override fun canNavigateBack(): Boolean {
        return backStack.size > 1
    }

    override fun shouldExitApp(): Boolean {
        return backStack.size <= 1
    }

    private val _currentRoute = MutableStateFlow<NavigationRoute?>(null)
    val currentRoute: StateFlow<NavigationRoute?> = _currentRoute.asStateFlow()

    private var isInitialized = false
    override fun navigateTo(route: NavigationRoute) {
        _backStack.add(route)
        _currentRoute.value = route
    }

    override fun navigateBack(): NavigationRoute? {
        if (_backStack.size > 1) {
            _backStack.removeLastOrNull()
            val previous = _backStack.lastOrNull()
            _currentRoute.value = previous
            return previous
        } else { return null }
    }
    override fun getCurrentRoute(): NavigationRoute? = _backStack.lastOrNull()

    override fun initializeWithRoot(route: NavigationRoute) {
        if (!isInitialized) {
            _backStack.clear()
            _backStack.add(route)
            _currentRoute.value = route
            isInitialized = true
        }
    }

    override fun getCurrentRouteFlow(): StateFlow<NavigationRoute?> = currentRoute
    override fun clearBackStack() {
        _backStack.clear()
        _currentRoute.value = null
    }

    override fun navigateAndClearBackStack(route: NavigationRoute) {
        clearBackStack()
        navigateTo(route)
    }

    override fun getBackStackSize(): Int = _backStack.size

}