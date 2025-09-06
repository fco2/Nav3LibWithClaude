package com.chuka.nav3libwithclaude.presentation.girl

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.ToastData
import com.chuka.nav3libwithclaude.domain.repositories.HumanRepository
import com.chuka.nav3libwithclaude.presentation.girl.GirlUiState
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationManager
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationRoute
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationTransition
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

    private val _girlUiState = MutableStateFlow<GirlUiState>(GirlUiState())
    val girlUiState = _girlUiState.asStateFlow()

    init {
        getHumanGirl()
    }

    private fun getHumanGirl() {
        viewModelScope.launch {
            navigationManager.getCurrentRouteFlow().collect { route ->
                (route as? NavigationRoute.GirlScreenRoute)?.let { nonNullRoute ->
                    nonNullRoute.humanId?.let { id ->
                        val human = repository.getHumanById(id).first()
                        _girlUiState.value = _girlUiState.value.copy(
                            currentHuman = human,
                            currentBackStack = navigationManager.backStack,
                            isLoading = false,
                            ageMates = emptyList()
                        )
                    }
                }
            }
        }
    }

    fun navigateTo(route: NavigationRoute, navigationTransition: NavigationTransition) {
        navigationManager.navigateTo(route, navigationTransition)
    }

    fun navigateToHumanScreen() {
        // Activate Toast showing we specified navigating to Human screen
        navigationManager.navigateTo(NavigationRoute.HumanScreenRoute(
            fromScreen = NavigationRoute.GirlScreenRoute.ROUTE,
            toastData = ToastData(
                message = "Navigating from Girl: ${_girlUiState.value.currentHuman.name} to Human",
                duration = ToastData.LENGTH_LONG,
                backgroundColor = android.R.color.darker_gray.toLong()
            )
        ))
    }

    fun navigateBack() {
        navigationManager.navigateBack()
    }

    fun showAgeMates() {
        _girlUiState.value = _girlUiState.value.copy(
            isLoading = true
        )
        viewModelScope.launch {
            val ageMates = repository.getHumansBetweenAgeRange(
                age = girlUiState.value.currentHuman.age ?: 0
            ).first().filterNot { it.id == girlUiState.value.currentHuman.id } // exclude yourself
            _girlUiState.value = _girlUiState.value.copy(
                ageMates = ageMates,
                isLoading = false
            )
        }
    }
}

data class GirlUiState(
    val currentHuman: Human = Human(),
    val currentBackStack: SnapshotStateList<NavigationRoute?> = emptyList<NavigationRoute?>().toMutableStateList(),
    val isLoading: Boolean = false,
    val ageMates: List<Human> = emptyList()
)