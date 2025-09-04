package com.chuka.nav3libwithclaude.presentation.humans

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.HumanType
import com.chuka.nav3libwithclaude.domain.models.ToastData
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationRoute
import com.chuka.nav3libwithclaude.presentation.util.Picker
import com.chuka.nav3libwithclaude.presentation.util.PickerState
import com.chuka.nav3libwithclaude.presentation.util.rememberPickerState

@Composable
fun HumanScreen(
    viewModel: HumanViewModel = hiltViewModel(),
    onNavigate: (navigationRoute: NavigationRoute) -> Unit,
    onBackPressed: () -> Unit,
    onExitApp: () -> Unit
) {
    val context = LocalContext.current
    val humans by viewModel.humans.collectAsStateWithLifecycle(initialValue = emptyList())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backStack = viewModel.backStack

    var showAddDialog by remember { mutableStateOf(false) }
    var newHumanName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(HumanType.BOY) }

    // Handle toast messages with background color
    LaunchedEffect(uiState.toastMessage) {
        uiState.toastMessage?.let { message ->
            val toastLength = if (uiState.toastLength == ToastData.LENGTH_LONG) {
                Toast.LENGTH_LONG
            } else {
                Toast.LENGTH_SHORT
            }

            // Create a custom toast (Note: For full custom styling with background color,
            // you would need a custom toast implementation or Snackbar)
            val toast = Toast.makeText(context, message, toastLength)
            toast.show()
            // TODO: should we add a delay to clear toast only after it has shown for the length of time required
            viewModel.clearToast()
        }
    }

    LaunchedEffect(uiState.shouldExitApp) {
        if (uiState.shouldExitApp == ShouldExitApp.YES) {
            onExitApp()
        }
    }

    BackHandler {
        onBackPressed()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header with navigation info
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Human Screen",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "Back Stack Size: ${backStack.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "Back Stack: ${backStack.joinToString(" → ") {
                        when(it) {
                            is NavigationRoute.HumanScreenRoute -> "Human(${it.fromScreen ?: "root"})"
                            is NavigationRoute.BoyScreenRoute -> "Boy${it.humanId?.let { id -> "($id)" } ?: ""}"
                            is NavigationRoute.GirlScreenRoute -> "Girl${it.humanId?.let { id -> "($id)" } ?: ""}"
                            null -> "root"
                        }
                    }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = /*onNavigateToBoys*/ {},
                modifier = Modifier.weight(1f)
            ) {
                Text("Go to Boys")
            }

            Button(
                onClick = /*onNavigateToGirls*/ {},
                modifier = Modifier.weight(1f)
            ) {
                Text("Go to Girls")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Add human button
        Button(
            onClick = { showAddDialog = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add Human")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Humans list
        Text(
            text = "All Humans (${humans.size})",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(humans) { human ->
                val route = when (human.gender) {
                    HumanType.BOY -> NavigationRoute.BoyScreenRoute(human.id)
                    HumanType.GIRL -> NavigationRoute.GirlScreenRoute(human.id)
                    else -> NavigationRoute.BoyScreenRoute(human.id) // default
                }
                HumanItem(
                    human = human,
                    onDelete = { viewModel.deleteHuman(human) },
                    onHumanClick = { onNavigate(route) }
                )
            }
        }
    }

    // Add Human Dialog
    if (showAddDialog) {
        val values = remember { (1..99).toList() }
        val valuesPickerState: PickerState = rememberPickerState()

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Human") },
            text = {
                Column {
                    OutlinedTextField(
                        value = newHumanName,
                        onValueChange = { newHumanName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Age:")

                    // Display selected age
                    Text(
                        text = "Selected: ${valuesPickerState.selectedItem ?: "1"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Picker<Int>(
                        state = valuesPickerState,
                        items = values,
                        visibleItemsCount = 5,
                        modifier = Modifier.fillMaxWidth(0.5f),
                        textModifier = Modifier.padding(8.dp),
                        textStyle = TextStyle(fontSize = 32.sp),
                        dividerColor = Color(0xFFE8E8E8)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text("Type:")
                    Row {
                        RadioButton(
                            selected = selectedType == HumanType.BOY,
                            onClick = { selectedType = HumanType.BOY }
                        )
                        Text("Boy", modifier = Modifier.padding(start = 8.dp))

                        Spacer(modifier = Modifier.width(16.dp))

                        RadioButton(
                            selected = selectedType == HumanType.GIRL,
                            onClick = { selectedType = HumanType.GIRL }
                        )
                        Text("Girl", modifier = Modifier.padding(start = 8.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newHumanName.isNotBlank()) {
                            val selectedAge = (valuesPickerState.selectedItem as? Int) ?: 1
                            val newHuman =
                                Human(name = newHumanName, age = selectedAge, gender = selectedType)
                            viewModel.addHuman(newHuman)
                            newHumanName = ""
                            showAddDialog = false
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun HumanItem(
    human: Human,
    onDelete: () -> Unit,
    onHumanClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = { onHumanClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = human.name ?: "",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Age: ${human.age} • ${human.gender?.name ?: ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            TextButton(onClick = onDelete) {
                Text("Delete", color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

