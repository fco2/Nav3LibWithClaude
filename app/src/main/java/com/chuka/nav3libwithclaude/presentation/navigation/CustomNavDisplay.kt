package com.chuka.nav3libwithclaude.presentation.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.chuka.nav3libwithclaude.presentation.boy.BoyScreen
import com.chuka.nav3libwithclaude.presentation.boy.BoyViewModel
import com.chuka.nav3libwithclaude.presentation.girl.GirlScreen
import com.chuka.nav3libwithclaude.presentation.girl.GirlViewModel
import com.chuka.nav3libwithclaude.presentation.humans.HumanScreen
import com.chuka.nav3libwithclaude.presentation.humans.HumanViewModel

@OptIn(ExperimentalAnimationApi::class)
@androidx.annotation.RequiresPermission(android.Manifest.permission.POST_NOTIFICATIONS)
@Composable
fun CustomNavDisplay(
    navigationManager: NavigationManager,
    startDestination: NavigationRoute = NavigationRoute.HumanScreenRoute(),
    onExitApp: () -> Unit,
) {
    // Implement your custom navigation display here
    val currentRoute by navigationManager.getCurrentRouteFlow().collectAsState()
    val context = LocalContext.current
    val navigationTransition by navigationManager.navigationTransition.collectAsState()
    val isAnimating by navigationManager.isAnimating.collectAsState()

    // Animated content with custom transitions
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = currentRoute,
            transitionSpec = {
                val transition = navigationTransition ?: NavigationTransition()
                createEnterTransition(transition).togetherWith(createExitTransition(transition))
            },
            label = "navigation_animation"
        ) { route ->
            // Animated content here
            // Render current screen based on route
            when (route) {
                is NavigationRoute.HumanScreenRoute -> {
                    val viewModel: HumanViewModel = hiltViewModel()
                    // process toast data when route changes
                    LaunchedEffect(route.toastData) {
                        viewModel.processToastData(route.toastData)
                    }
                    HumanScreen(
                        viewModel = viewModel,
                        onNavigate = { navigationRoute ->
                            when (navigationRoute) {
                                is NavigationRoute.BoyScreenRoute -> {
                                    val transition = NavigationTransition(
                                        enterAnimation = NavigationAnimation.SLIDE_LEFT,
                                        exitAnimation = NavigationAnimation.SLIDE_RIGHT,
                                        duration = 400
                                    )
                                    viewModel.navigateTo(navigationRoute, transition)
                                }
                                is NavigationRoute.GirlScreenRoute -> {
                                    val transition = NavigationTransition(
                                        enterAnimation = NavigationAnimation.SLIDE_UP,
                                        exitAnimation = NavigationAnimation.FADE,
                                        duration = 350
                                    )
                                    viewModel.navigateTo(navigationRoute, transition)
                                }

                                is NavigationRoute.HumanScreenRoute -> Unit
                            }
                        },
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
                            val transition = NavigationTransition(
                                enterAnimation = NavigationAnimation.FADE,
                                exitAnimation = NavigationAnimation.SCALE,
                                duration = 250
                            )
                            viewModel.navigateTo(NavigationRoute.HumanScreenRoute(fromScreen, toastData), transition)
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
                        onNavigateToHumanScreen = { fromScreen, toastData ->
                            val transition = NavigationTransition(
                                enterAnimation = NavigationAnimation.SLIDE_DOWN,
                                exitAnimation = NavigationAnimation.SLIDE_UP,
                                duration = 300
                            )
                            viewModel.navigateTo(NavigationRoute.HumanScreenRoute(fromScreen, toastData), transition)

                        },
                        onNavigateToBoyScreen = { humanId ->
                            val transition = NavigationTransition(
                                enterAnimation = NavigationAnimation.SLIDE_UP,
                                exitAnimation = NavigationAnimation.SLIDE_DOWN,
                                duration = 300
                            )
                            viewModel.navigateTo(NavigationRoute.BoyScreenRoute(humanId), transition)
                        },
                        onNavigateToGirlScreen = { humanId ->
                            val transition = NavigationTransition(
                                enterAnimation = NavigationAnimation.FADE,
                                exitAnimation = NavigationAnimation.SCALE,
                                duration = 300
                            )
                            viewModel.navigateTo(NavigationRoute.GirlScreenRoute(humanId), transition)
                        },
                        onNavigateBack = { viewModel.navigateBack() }
                    )
                }
                null -> { // Initialize navigation manager with start destination
                    navigationManager.initializeWithRoot(startDestination)
                }
            }
        }
    }
}

// Animation helper functions
private fun createEnterTransition(transition: NavigationTransition): EnterTransition {
    return when (transition.enterAnimation) {
        NavigationAnimation.SLIDE_LEFT -> slideInHorizontally(
            initialOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(transition.duration, easing = FastOutSlowInEasing)
        )
        NavigationAnimation.SLIDE_RIGHT -> slideInHorizontally(
            initialOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(transition.duration, easing = FastOutSlowInEasing)
        )
        NavigationAnimation.SLIDE_UP -> slideInVertically(
            initialOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(transition.duration, easing = FastOutSlowInEasing)
        )
        NavigationAnimation.SLIDE_DOWN -> slideInVertically(
            initialOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(transition.duration, easing = FastOutSlowInEasing)
        )
        NavigationAnimation.FADE -> fadeIn(
            animationSpec = tween(transition.duration, easing = LinearEasing)
        )
        NavigationAnimation.SCALE -> scaleIn(
            initialScale = 0.8f,
            animationSpec = tween(transition.duration, easing = FastOutSlowInEasing)
        ) + fadeIn(animationSpec = tween(transition.duration, easing = LinearEasing))
        NavigationAnimation.NONE -> EnterTransition.None
    }
}

private fun createExitTransition(transition: NavigationTransition): ExitTransition {
    return when (transition.exitAnimation) {
        NavigationAnimation.SLIDE_LEFT -> slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth },
            animationSpec = tween(transition.duration, easing = FastOutSlowInEasing)
        )
        NavigationAnimation.SLIDE_RIGHT -> slideOutHorizontally(
            targetOffsetX = { fullWidth -> fullWidth },
            animationSpec = tween(transition.duration, easing = FastOutSlowInEasing)
        )
        NavigationAnimation.SLIDE_UP -> slideOutVertically(
            targetOffsetY = { fullHeight -> -fullHeight },
            animationSpec = tween(transition.duration, easing = FastOutSlowInEasing)
        )
        NavigationAnimation.SLIDE_DOWN -> slideOutVertically(
            targetOffsetY = { fullHeight -> fullHeight },
            animationSpec = tween(transition.duration, easing = FastOutSlowInEasing)
        )
        NavigationAnimation.FADE -> fadeOut(
            animationSpec = tween(transition.duration, easing = LinearEasing)
        )
        NavigationAnimation.SCALE -> scaleOut(
            targetScale = 0.8f,
            animationSpec = tween(transition.duration, easing = FastOutSlowInEasing)
        ) + fadeOut(animationSpec = tween(transition.duration, easing = LinearEasing))
        NavigationAnimation.NONE -> ExitTransition.None
    }
}