package com.chuka.nav3libwithclaude.presentation.girl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.ToastData
import com.chuka.nav3libwithclaude.domain.repositories.HumanRepository
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationManager
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GirlViewModel @Inject constructor(
    private val repository: HumanRepository,
    private val navigationManager: NavigationManager
): ViewModel(){

    private val _currentHuman = MutableStateFlow(Human())
    val currentHuman = _currentHuman.asStateFlow()
    val backStack = navigationManager.backStack

    init {
        getCurrentHuman()
    }

    private fun getCurrentHuman() {
        viewModelScope.launch {
            navigationManager.getCurrentRouteFlow().collect { route ->
                if (route is NavigationRoute.GirlScreenRoute) {
                    route.humanId?.let { id ->
                        // we should normally null check here
                        _currentHuman.value = repository.getHumanById(id).first()
                    }

                }
            }
        }
    }

    fun navigateTo(route: NavigationRoute) {
        navigationManager.navigateTo(route)
    }

    fun navigateToGirlScreen(humanId: Long) {
        navigationManager.navigateTo(NavigationRoute.GirlScreenRoute(humanId))
    }

    fun navigateToBoyScreen(humanId: Long) {
        navigationManager.navigateTo(NavigationRoute.BoyScreenRoute(humanId))
    }

    fun navigateToHumanScreen() {
        // Activate Toast showing we specified navigating to Human screen
        navigationManager.navigateTo(NavigationRoute.HumanScreenRoute(
            fromScreen = NavigationRoute.GirlScreenRoute.ROUTE,
            toastData = ToastData(
                message = "Navigating from Girl: ${currentHuman.value.name} to Human",
                duration = ToastData.LENGTH_LONG,
                backgroundColor = android.R.color.darker_gray.toLong()
            )
        ))
    }

    fun navigateBack() {
        navigationManager.navigateBack()
    }
}