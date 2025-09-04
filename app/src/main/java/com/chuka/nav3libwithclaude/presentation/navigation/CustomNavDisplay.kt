package com.chuka.nav3libwithclaude.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.chuka.nav3libwithclaude.presentation.boy.BoyScreen
import com.chuka.nav3libwithclaude.presentation.boy.BoyViewModel
import com.chuka.nav3libwithclaude.presentation.girl.GirlScreen
import com.chuka.nav3libwithclaude.presentation.girl.GirlViewModel
import com.chuka.nav3libwithclaude.presentation.humans.HumanScreen
import com.chuka.nav3libwithclaude.presentation.humans.HumanViewModel

@Composable
fun CustomNavDisplay(
    navigationManager: NavigationManager,
    startDestination: NavigationRoute = NavigationRoute.HumanScreenRoute(),
    onExitApp: () -> Unit,
) {
    // Implement your custom navigation display here
    val context = LocalContext.current
    val currentRoute by navigationManager.getCurrentRouteFlow().collectAsState()

    //Initialize navigation if needed
    LaunchedEffect(Unit) {
        navigationManager.initializeWithRoot(startDestination)
    }

    // Render current screen based on route
    when (val route = currentRoute) {
        is NavigationRoute.HumanScreenRoute -> {
            val viewModel: HumanViewModel = hiltViewModel()
            // process toast data when route changes
            LaunchedEffect(route.toastData) {
                viewModel.processToastData(route.toastData)
            }
            HumanScreen(
                viewModel = viewModel,
                onNavigate = { navigationRoute -> viewModel.navigateTo(navigationRoute) },
                onBackPressed = { viewModel.navigateBack() },
                onExitApp = onExitApp
            )
        }
        is NavigationRoute.BoyScreenRoute -> {
            val viewModel: BoyViewModel = hiltViewModel()

            BoyScreen(
                viewModel = viewModel,
                selectedHumanId = route.humanId,
                onNavigateToHumanScreen = { fromScreen, toastData ->
                    viewModel.navigateTo(NavigationRoute.HumanScreenRoute(fromScreen, toastData))
                },
                onNavigateToGirlScreen = { humanId -> viewModel.navigateTo(NavigationRoute.GirlScreenRoute(humanId)) },
                onNavigateToBoyScreen = { humanId -> viewModel.navigateTo(NavigationRoute.BoyScreenRoute(humanId)) },
                onNavigateBack = { viewModel.navigateBack() }
            )
        }
        is NavigationRoute.GirlScreenRoute -> {
            val viewModel: GirlViewModel = hiltViewModel()
            GirlScreen(
                viewModel = viewModel,
                selectedHumanId = route.humanId,
                onNavigateToHumanScreen = { fromScreen, toastData -> viewModel.navigateTo(NavigationRoute.HumanScreenRoute(fromScreen, toastData)) },
                onNavigateToBoyScreen = { humanId -> viewModel.navigateTo(NavigationRoute.BoyScreenRoute(humanId)) },
                onNavigateToGirlScreen = { humanId -> viewModel.navigateTo(NavigationRoute.GirlScreenRoute(humanId)) },
                onNavigateBack = { viewModel.navigateBack() }
            )
        }
        null -> {
            // Fallback to default screen - but initialization should handle this
            // The initializeWithRoot call above should prevent this case
        }
    }
}