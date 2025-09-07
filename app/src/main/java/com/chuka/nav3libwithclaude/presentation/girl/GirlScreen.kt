package com.chuka.nav3libwithclaude.presentation.girl

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chuka.nav3libwithclaude.domain.models.HumanType
import com.chuka.nav3libwithclaude.domain.models.ToastData
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationRoute
import com.chuka.nav3libwithclaude.presentation.util.HasAgeMates

@Composable
fun GirlScreen(
    viewModel: GirlViewModel = hiltViewModel(),
    selectedHumanId: Long?,
    onNavigateToGirlScreen: (humanId: Long?) -> Unit,
    onNavigateToBoyScreen: (humanId: Long?) -> Unit,
    onNavigateToHumanScreen: (fromScreen: String, toastData: ToastData) -> Unit,
    onNavigateBack: () -> Unit
) {
    val girlUIState by viewModel.girlUiState.collectAsStateWithLifecycle()

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
                    text = "Back Stack Size: ${girlUIState.currentBackStack.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )

                Text(
                    text = "Back Stack: ${girlUIState.currentBackStack.joinToString(" → ") {
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

        // Top bar
        Column(modifier = Modifier.fillMaxWidth()) {
            Button(onClick = {
                onNavigateToHumanScreen("Girl Screen", ToastData("From Girl Screen",
                    ToastData.LENGTH_LONG, 0xFFF1F1F1
                ))
            },
                modifier = Modifier.align(Alignment.End)) {
                Text("Back to Human Screen")
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }

                Text(
                    text = "Girl Screen${selectedHumanId?.let { " (Viewing ID: $it)" } ?: ""}",
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier.weight(1f)
                )
            }
        }


        Spacer(modifier = Modifier.height(16.dp))

        // Current human info if accessed via navigation
        girlUIState.currentHuman.let { human ->
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

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(onClick = { viewModel.showAgeMates() }) {
            Text(text = "Show Age Mates")
        }

        if (girlUIState.hasAgeMates == HasAgeMates.NO) {
            Text(
                text = "No age mates found",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error
            )
        } else if (girlUIState.hasAgeMates == HasAgeMates.YES) {
            LazyColumn(
                modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(girlUIState.ageMates) { mate ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .clickable {
                                when (mate.gender) {
                                    HumanType.BOY -> onNavigateToBoyScreen(mate.id)
                                    HumanType.GIRL -> onNavigateToGirlScreen(mate.id)
                                    else -> onNavigateToBoyScreen(mate.id) // default
                                }
                            },
                        colors = CardDefaults.cardColors(
                            containerColor = when(mate.gender) {
                                HumanType.BOY -> MaterialTheme.colorScheme.secondaryContainer
                                HumanType.GIRL -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.primaryContainer
                            }
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                text = mate.name ?: "Unknown",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            Text(
                                text = mate.age.toString(),
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }
    }

}