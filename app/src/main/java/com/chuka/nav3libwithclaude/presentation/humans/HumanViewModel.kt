package com.chuka.nav3libwithclaude.presentation.humans

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.HumanType
import com.chuka.nav3libwithclaude.domain.models.ToastData
import com.chuka.nav3libwithclaude.domain.repositories.HumanRepository
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationManager
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HumanViewModel @Inject constructor(
    private val repository: HumanRepository,
    private val navigationManager: NavigationManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(HumanUiState())
    val uiState: StateFlow<HumanUiState> = _uiState.asStateFlow()

    val humans = repository.getAllHumans()
    val backStack: SnapshotStateList<NavigationRoute> = navigationManager.backStack
    val currentRoute = navigationManager.getCurrentRouteFlow()

    init {
        initializeData()
    }

    private fun initializeData() {
        viewModelScope.launch {
            val sampleHumans = buildSeedHumanData()
            repository.insertHumans(sampleHumans)
        }
    }

    fun addHuman(human: Human) {
        viewModelScope.launch {
            repository.insertHuman(human)
            _uiState.value = _uiState.value.copy(
                toastMessage = "Human added successfully",
                toastBackgroundColor = 0xFF00FF00.toLong(),
            )
        }
    }

    fun deleteHuman(human: Human) {
        viewModelScope.launch {
            repository.deleteHuman(human)
        }
    }

    fun navigateTo(route: NavigationRoute) {
        navigationManager.navigateTo(route)
    }

    fun navigateBack() {
        navigationManager.navigateBack()
    }

    fun processToastData(toastData: ToastData?) {
        toastData?.let { toast ->
            _uiState.value = _uiState.value.copy(
                toastMessage = toast.message,
                toastBackgroundColor = toast.backgroundColor,
                toastLength = toast.duration
            )
        }
    }

    fun showToast(message: String) {
        _uiState.value = _uiState.value.copy(toastMessage = message)
    }

    fun clearToast() {
        _uiState.value = _uiState.value.copy(
            toastMessage = null,
            toastBackgroundColor = null,
            toastLength = ToastData.LENGTH_SHORT
        )
    }
}

private fun buildSeedHumanData(): List<Human> = listOf(
    Human(1L, "Alice", 24, HumanType.GIRL),
    Human(2L, "Bob", 34, HumanType.BOY),
    Human(3L, "Charlie", 22, HumanType.BOY),
    Human(4L, "Diana", 24, HumanType.GIRL),
    Human(5L, "Eve", 25, HumanType.GIRL),
    Human(6L, "Frank", 30, HumanType.BOY)
)

data class HumanUiState(
    val toastMessage: String? = null,
    val toastBackgroundColor: Long? = null,
    val toastLength: Int = ToastData.LENGTH_SHORT
)