package com.chuka.nav3libwithclaude.presentation.boy

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.ToastData
import com.chuka.nav3libwithclaude.domain.repositories.HumanRepository
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationManager
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationRoute
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationTransition
import com.chuka.nav3libwithclaude.presentation.util.HasAgeMates
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BoyViewModel @Inject constructor(
    private val repository: HumanRepository,
    private val navigationManager: NavigationManager
) : ViewModel(){
    private val _boyUiState = MutableStateFlow<BoyUiState>(BoyUiState())
    val boyUiState = _boyUiState.asStateFlow()

    init { getHumanBoy() }

    private fun getHumanBoy() {
        viewModelScope.launch {
            navigationManager.getCurrentRouteFlow().collect { route ->
                (route as? NavigationRoute.BoyScreenRoute)?.let { nonNullRoute ->
                    nonNullRoute.humanId?.let { id ->
                        val human = repository.getHumanById(id).first()
                        _boyUiState.value = _boyUiState.value.copy(
                            currentHuman = human,
                            currentBackStack = navigationManager.backStack,
                            isLoading = false,
                            ageMates = emptyList(),
                            hasAgeMates = HasAgeMates.NOT_AVAILABLE
                        )
                    }
                }
            }
        }
    }

    fun navigateTo(route: NavigationRoute, transition: NavigationTransition = NavigationTransition()) {
        navigationManager.navigateTo(route, transition)
    }

    fun showAgeMates() {
        _boyUiState.value = _boyUiState.value.copy(
            isLoading = true
        )
        viewModelScope.launch {
            val ageMates = repository.getHumansBetweenAgeRange(
                age =boyUiState.value.currentHuman.age ?: 0
            ).first().filterNot { it.id == boyUiState.value.currentHuman.id } // exclude yourself
            _boyUiState.value = _boyUiState.value.copy(
                ageMates = ageMates,
                isLoading = false,
                hasAgeMates = if (ageMates.isNotEmpty()) HasAgeMates.YES else HasAgeMates.NO
            )
        }
    }

    fun navigateBack() {
        navigationManager.navigateBack()
    }
}



data class BoyUiState(
    val currentHuman: Human = Human(),
    val currentBackStack: SnapshotStateList<NavigationRoute?> = emptyList<NavigationRoute?>().toMutableStateList(),
    val isLoading: Boolean = false,
    val ageMates: List<Human> = emptyList(),
    val hasAgeMates: HasAgeMates = HasAgeMates.NOT_AVAILABLE,

)