package com.chuka.nav3libwithclaude.presentation.navigation

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

// NavigationAnimation.kt
enum class NavigationAnimation {
    SLIDE_LEFT,
    SLIDE_RIGHT,
    SLIDE_UP,
    SLIDE_DOWN,
    FADE,
    SCALE,
    NONE
}

data class NavigationTransition(
    val enterAnimation: NavigationAnimation = NavigationAnimation.SLIDE_LEFT,
    val exitAnimation: NavigationAnimation = NavigationAnimation.SLIDE_RIGHT,
    val duration: Int = 300
)

interface NavigationManager {

    val backStack: SnapshotStateList<NavigationRoute?>
    val navigationTransition: StateFlow<NavigationTransition?>
    val isAnimating: StateFlow<Boolean>
    fun navigateTo(route: NavigationRoute, transition: NavigationTransition = NavigationTransition())

    fun navigateBack(): NavigationRoute?

    fun getCurrentRoute(): NavigationRoute?

    fun clearBackStack()

    fun navigateAndClearBackStack(route: NavigationRoute)

    fun getBackStackSize(): Int

    fun initializeWithRoot(route: NavigationRoute)

    fun getCurrentRouteFlow(): StateFlow<NavigationRoute?>

    fun canNavigateBack(): Boolean
    fun shouldExitApp(): Boolean
}

class NavigationManagerImpl @Inject constructor() : NavigationManager {
    private val _backStack = mutableStateListOf<NavigationRoute?>()
    override val backStack: SnapshotStateList<NavigationRoute?> = _backStack

    private val _navigationTransition = MutableStateFlow<NavigationTransition?>(null)
    override val navigationTransition: StateFlow<NavigationTransition?> = _navigationTransition.asStateFlow()

    private val _isAnimating = MutableStateFlow(false)
    override val isAnimating: StateFlow<Boolean> = _isAnimating.asStateFlow()

    override fun canNavigateBack(): Boolean = backStack.size > 1
    override fun shouldExitApp(): Boolean = backStack.size <= 1
    private val _currentRoute = MutableStateFlow<NavigationRoute?>(null)
    val currentRoute: StateFlow<NavigationRoute?> = _currentRoute.asStateFlow()
    private var isInitialized = false
    override fun navigateTo(route: NavigationRoute, transition: NavigationTransition) {
        _isAnimating.value = true
        _navigationTransition.value = transition
        _backStack.add(route)
        _currentRoute.value = route

        // Reset animation state after transition duration
        CoroutineScope(Dispatchers.Main).launch {
            delay(transition.duration.toLong())
            _isAnimating.value = false
            _navigationTransition.value = null
        }
    }

    override fun navigateBack(): NavigationRoute? {
       return if (_backStack.size > 1) {
           _isAnimating.value = true
           // Use reverse animation for back navigation
           val backTransition = NavigationTransition(
               enterAnimation = NavigationAnimation.SLIDE_RIGHT,
               exitAnimation = NavigationAnimation.SLIDE_LEFT,
               duration = 300
           )
           _navigationTransition.value = backTransition

            _backStack.removeLastOrNull()
            val previous = _backStack.lastOrNull()
            _currentRoute.value = previous
           // Reset animation state
           CoroutineScope(Dispatchers.Main).launch {
               delay(backTransition.duration.toLong())
               _isAnimating.value = false
               _navigationTransition.value = null
           }
           // return previous
           previous
        } else { null }
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
        _isAnimating.value = false
        isInitialized = false
    }

    override fun navigateAndClearBackStack(route: NavigationRoute) {
        clearBackStack()
        navigateTo(route)
    }

    override fun getBackStackSize(): Int = _backStack.size

}