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
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationTransition
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

    private val _humans = MutableStateFlow<List<Human>>(emptyList())
    val humans: StateFlow<List<Human>> = _humans.asStateFlow()

    val backStack: SnapshotStateList<NavigationRoute?> = navigationManager.backStack
    val currentRoute = navigationManager.getCurrentRouteFlow()

    private val _dragState = MutableStateFlow(DragState())
    val dragState: StateFlow<DragState> = _dragState.asStateFlow()

    init {
        initializeData()
        collectHumans()
    }

    private fun collectHumans() {
        viewModelScope.launch {
            repository.getAllHumans().collect { humansList ->
                _humans.value = humansList
            }
        }
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
                toastBackgroundColor = 0xFF00FF00,
            )
        }
    }

    fun deleteHuman(human: Human) {
        viewModelScope.launch {
            repository.deleteHuman(human)
        }
    }

    fun navigateTo(route: NavigationRoute, transition: NavigationTransition = NavigationTransition()) {
        navigationManager.navigateTo(route, transition)
    }

    fun navigateBack() {
        val previousRoute = navigationManager.navigateBack()
        if (previousRoute == null && navigationManager.shouldExitApp())
        {
            // Could emit an event here to close the app
            // For now, we'll just stay on the current screen
            _uiState.value = _uiState.value.copy(shouldExitApp = ShouldExitApp.YES)
        }
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

    fun startDrag(index: Int) {
        _dragState.value = _dragState.value.copy(
            draggedIndex = index,
            isDragInProgress = true,
            dropTargetIndex = index
        )
    }

    fun updateDrag(draggedIndex: Int, offset: Float) {
        if (_dragState.value.draggedIndex == draggedIndex) {
            val listSize = _humans.value.size
            // Calculate drop target index with better accuracy
            val cardPadding = 16f * 2 // Card internal padding
            val itemContentHeight = 72f // Approximate content height
            val itemSpacing = 8f // LazyColumn spacing
            val totalItemHeight = itemContentHeight + cardPadding + itemSpacing

            val positionChange = (offset / totalItemHeight).toInt()
            val newTargetIndex = (draggedIndex + positionChange).coerceIn(0, listSize - 1)

            _dragState.value = _dragState.value.copy(
                dropTargetIndex = newTargetIndex
            )
        }
    }

    fun endDrag() {
        val currentDragState = _dragState.value
        val currentHumans = _humans.value

        if (currentDragState.draggedIndex != -1 &&
            currentDragState.dropTargetIndex != currentDragState.draggedIndex &&
            currentHumans.isNotEmpty() &&
            currentDragState.draggedIndex < currentHumans.size &&
            currentDragState.dropTargetIndex < currentHumans.size
        ) {
            // Perform the reorder operation
            viewModelScope.launch {
                val mutableList = currentHumans.toMutableList()
                val item = mutableList.removeAt(currentDragState.draggedIndex)
                mutableList.add(currentDragState.dropTargetIndex, item)

                repository.reorderHumans(mutableList)
                _uiState.value = _uiState.value.copy(
                    toastMessage = "Order updated successfully",
                    toastBackgroundColor = 0xFF4CAF50,
                )
            }
        }

        // Reset drag state
        _dragState.value = DragState()
    }

    fun cancelDrag() {
        _dragState.value = DragState()
    }
}

private fun buildSeedHumanData(): List<Human> = listOf(
    Human(1L, "Alice", 24, HumanType.GIRL, rank = 6),
    Human(2L, "Bob", 34, HumanType.BOY, rank = 5),
    Human(3L, "Charlie", 22, HumanType.BOY, rank = 4),
    Human(4L, "Diana", 24, HumanType.GIRL, rank = 3),
    Human(5L, "Eve", 25, HumanType.GIRL, rank = 2),
    Human(6L, "Frank", 30, HumanType.BOY, rank = 1)
)

data class HumanUiState(
    val toastMessage: String? = null,
    val toastBackgroundColor: Long? = null,
    val toastLength: Int = ToastData.LENGTH_SHORT,
    val shouldExitApp: ShouldExitApp = ShouldExitApp.NO
)

data class DragState(
    val draggedIndex: Int = -1,
    val isDragInProgress: Boolean = false,
    val dropTargetIndex: Int = -1
)

enum class ShouldExitApp {
    YES,
    NO
}