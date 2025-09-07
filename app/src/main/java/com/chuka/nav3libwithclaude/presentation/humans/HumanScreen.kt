package com.chuka.nav3libwithclaude.presentation.humans

import android.Manifest
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.chuka.nav3libwithclaude.domain.models.Human
import com.chuka.nav3libwithclaude.domain.models.HumanType
import com.chuka.nav3libwithclaude.domain.models.ToastData
import com.chuka.nav3libwithclaude.presentation.navigation.DeepLinkHandler
import com.chuka.nav3libwithclaude.presentation.navigation.NavigationRoute
import com.chuka.nav3libwithclaude.presentation.navigation.NotificationHelper
import com.chuka.nav3libwithclaude.presentation.navigation.NotificationHelperProvider
import com.chuka.nav3libwithclaude.presentation.navigation.NotificationPermissionHandler
import com.chuka.nav3libwithclaude.presentation.navigation.NotificationPermissionProvider
import com.chuka.nav3libwithclaude.presentation.util.BackStackInfoComposable
import com.chuka.nav3libwithclaude.presentation.util.Picker
import com.chuka.nav3libwithclaude.presentation.util.PickerState
import com.chuka.nav3libwithclaude.presentation.util.rememberPickerState
import kotlin.math.roundToInt

@RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun HumanScreen(
    viewModel: HumanViewModel = hiltViewModel(),
    onNavigate: (navigationRoute: NavigationRoute) -> Unit,
    onBackPressed: () -> Unit,
    onExitApp: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as ComponentActivity
    val humans by viewModel.humans.collectAsStateWithLifecycle(initialValue = emptyList())
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val backStack = viewModel.backStack

    // Get NotificationPermissionHandler from Hilt
    val permissionHandler: NotificationPermissionHandler = hiltViewModel<NotificationPermissionProvider>().permissionHandler
    val notificationHelper: NotificationHelper = hiltViewModel<NotificationHelperProvider>().notificationHelper

    var showAddDialog by remember { mutableStateOf(false) }
    var newHumanName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(HumanType.BOY) }

    var showPermissionDialog by remember { mutableStateOf(false) }
    var pendingNotificationAction by remember { mutableStateOf<(() -> Unit)?>(null) }

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

    // Helper function to handle notification with permission check
    fun handleNotificationWithPermission(action: () -> Unit) {
        if (permissionHandler.hasNotificationPermission(context)) {
            action()
        } else {
            pendingNotificationAction = action
            showPermissionDialog = true
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
    ) {
        // Screen content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with navigation info
            BackStackInfoComposable(backStack)

            Spacer(modifier = Modifier.height(16.dp))

            // Deep Link Testing Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Column(modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔗 Deep Link Testing",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        // Permission status indicator
                        val permissionGranted = permissionHandler.hasNotificationPermission(context)
                        Badge(
                            containerColor = if (permissionGranted) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        ) {
                            Text(
                                text = if (permissionGranted) "✓ Permission OK" else "! Permission Needed",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            handleNotificationWithPermission {
                                val notificationHelper = NotificationHelper(
                                    context,
                                    DeepLinkHandler(),
                                    NotificationPermissionHandler()
                                )
                                notificationHelper.showGeneralNotification {
                                    Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Send General Notification")
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Test deep links (for example):\n" +
                                "• navdisplay://app/boy/2\n" +
                                "• navdisplay://app/girl/1\n" +
                                "• navdisplay://app/human?notification=true",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
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
                itemsIndexed(humans) { index, human ->
                    val route = when (human.gender) {
                        HumanType.BOY -> NavigationRoute.BoyScreenRoute(human.id)
                        HumanType.GIRL -> NavigationRoute.GirlScreenRoute(human.id)
                        else -> NavigationRoute.BoyScreenRoute(human.id) // default
                    }
                    HumanItem(
                        human = human,
                        onDelete = { viewModel.deleteHuman(human) },
                        onHumanClick = { onNavigate(route) },
                        modifier = Modifier,
                        onSendNotification = {
                            if (human.gender == HumanType.BOY) {
                                human.id?.let {
                                    notificationHelper.showBoyNotification(human.id, human.name.orEmpty()) {
                                        Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                human.id?.let {
                                    notificationHelper.showGirlNotification(human.id, human.name.orEmpty()) {
                                        Toast.makeText(context, "Notification permission denied", Toast.LENGTH_SHORT).show()
                                    }
                                }

                            }
                        }
                    )
                    if (index == humans.lastIndex) {
                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
            }
        }

        // Permission request dialog
        if (showPermissionDialog) {
            val pair = requestPermissionDialog(
                true,
                pendingNotificationAction,
                activity,
                permissionHandler,
                context
            )
            pendingNotificationAction = pair.first
            showPermissionDialog = pair.second
        }

        // Add Human Dialog
        if (showAddDialog) {
            val values = remember { (1..99).toList() }
            val valuesPickerState: PickerState = rememberPickerState()

            AddHumanDialog(
                newHumanName,
                { newHumanName = it },
                valuesPickerState,
                values,
                selectedType,
                { selectedType = it },
                { showAddDialog = false },
                { name, age, type ->
                    if (name.isNotBlank()) {
                        val newHuman = Human(name = name, age = age, gender = type)
                        viewModel.addHuman(newHuman)
                        newHumanName = ""
                        showAddDialog = false
                    }
                }
            )
        }
        // Add Human button
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd),
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            elevation = FloatingActionButtonDefaults.elevation(
                defaultElevation = 6.dp,
                pressedElevation = 12.dp
            ),
            interactionSource = null,
            content = {
                Icon(Icons.Filled.Add, contentDescription = "Add")
            }
        )
    }
}

@Composable
private fun requestPermissionDialog(
    showPermissionDialog: Boolean,
    pendingNotificationAction: (() -> Unit)?,
    activity: ComponentActivity,
    permissionHandler: NotificationPermissionHandler,
    context: ComponentActivity
): Pair<(() -> Unit)?, Boolean> {
    var showPermissionDialog1 = showPermissionDialog
    var pendingNotificationAction1 = pendingNotificationAction
    AlertDialog(
        onDismissRequest = {
            showPermissionDialog1 = false
            pendingNotificationAction1 = null
        },
        title = { Text("Notification Permission") },
        text = {
            Text("This app needs notification permission to send you deep link notifications. " +
                    "Grant permission to test the notification features.")
        },
        confirmButton = {
            TextButton(
                onClick = {
                    activity.let { act ->
                        permissionHandler.requestNotificationPermission(context) { granted ->
                            showPermissionDialog1 = false
                            if (granted) {
                                pendingNotificationAction1?.invoke()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Permission denied. You can enable it in Settings.",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                            pendingNotificationAction1 = null
                        }
                    }
                }
            ) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showPermissionDialog1 = false
                    pendingNotificationAction1 = null
                    Toast.makeText(context, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            ) {
                Text("Cancel")
            }
        }
    )
    return Pair(pendingNotificationAction1, showPermissionDialog1)
}

@Composable
private fun AddHumanDialog(
    newHumanName: String,
    onNameChange: (String) -> Unit,
    valuesPickerState: PickerState,
    values: List<Int>,
    selectedType: HumanType,
    onTypeChange: (HumanType) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (String, Int, HumanType) -> Unit
) {
    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Add New Human") },
        text = {
            Column {
                OutlinedTextField(
                    value = newHumanName,
                    onValueChange = onNameChange,
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
                        onClick = { onTypeChange(HumanType.BOY) }
                    )
                    Text("Boy", modifier = Modifier.padding(start = 8.dp))

                    Spacer(modifier = Modifier.width(16.dp))

                    RadioButton(
                        selected = selectedType == HumanType.GIRL,
                        onClick = { onTypeChange(HumanType.GIRL) }
                    )
                    Text("Girl", modifier = Modifier.padding(start = 8.dp))
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(newHumanName, (valuesPickerState.selectedItem as? Int) ?: 1, selectedType)
                }
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun HumanItem(
    human: Human,
    onDelete: () -> Unit,
    onHumanClick: () -> Unit,
    modifier: Modifier = Modifier,
    onSendNotification: (Long) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        onClick = { onHumanClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                /* matte drag handle on the left; long-press to start drag */
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(30.dp)
                        .padding(horizontal = 1.dp, vertical = 1.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f))
                        .pointerInput(Unit) {
                            detectDragGesturesAfterLongPress(
                                onDragStart = { },
                                onDragEnd = { },
                                onDragCancel = { },
                            ) { change, dragAmount ->
                                change.consume()
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Grid pattern like Google Keep (3x6 dots)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    ) {
                        repeat(6) { rowIndex ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(3.dp)
                            ) {
                                repeat(3) { colIndex ->
                                    Box(
                                        modifier = Modifier
                                            .size(1.5.dp)
                                            .background(
                                                MaterialTheme.colorScheme.onSurfaceVariant.copy(
                                                    alpha = 0.4f
                                                ),
                                                RoundedCornerShape(50.dp) // Circular dots
                                            )
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

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
            }


            Row {
                IconButton(onClick = { human.id?.let { onSendNotification(it) } }) {
                    Icon(
                        imageVector = Icons.Filled.Notifications,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        contentDescription = "Send notification"
                    )
                }
                IconButton(onClick = { onDelete() }) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        tint = MaterialTheme.colorScheme.error,
                        contentDescription = "Edit human"
                    )
                }
            }
        }
    }
}

