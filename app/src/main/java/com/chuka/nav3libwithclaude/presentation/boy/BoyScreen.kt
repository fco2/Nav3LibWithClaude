package com.chuka.nav3libwithclaude.presentation.boy

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chuka.nav3libwithclaude.domain.models.ToastData
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationRoute

@Composable
fun BoyScreen(
    viewModel: BoyViewModel = hiltViewModel(),
    selectedHumanId: Long?,
    onNavigateToGirlScreen: (humanId: Long) -> Unit,
    onNavigateToHumanScreen: (fromScreen: String, toastData: ToastData) -> Unit,
    onNavigateBack: () -> Unit
) {
    val currentHuman by viewModel.currentHuman.collectAsStateWithLifecycle()
    val backStack = viewModel.currentBackStack

    // Handle system back press
    BackHandler {
        onNavigateBack()
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
                        }
                    }}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        // Top bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }

            Text(
                text = "Boy Screen${selectedHumanId?.let { " (Viewing ID: $it)" } ?: ""}",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.weight(1f)
            )

            Button(onClick = {
                onNavigateToHumanScreen("Boy Screen", ToastData("From Boy Screen",
                    ToastData.LENGTH_LONG, 0xFFF1F1F1
                ))
            }) {
                Text("Back to Human Screen")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Current human info if accessed via navigation
        currentHuman.let { human ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "👁️ Currently Viewing:",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Name: ${human.name}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "ID: ${human.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Type: ${human.gender?: "Unknown"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Text(
                        text = "Age: ${human.age}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}