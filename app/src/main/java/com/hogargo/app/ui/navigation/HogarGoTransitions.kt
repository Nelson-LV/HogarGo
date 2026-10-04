package com.hogargo.app.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.navigation.NavBackStackEntry

private const val DurationMs = 420

private fun NavBackStackEntry.tabIndex(): Int =
    BottomNavDestinations.indexOfFirst { it.route == destination.route }

/** Pre-login flow: Welcome (rank 0) -> Join / Create (rank 1). */
private fun NavBackStackEntry.authRank(): Int = when (destination.route) {
    Routes.WELCOME -> 0
    Routes.JOIN, Routes.CREATE -> 1
    else -> -1
}

private fun NavBackStackEntry.isAuth(): Boolean = authRank() >= 0

/**
 * Tab-to-tab changes slide in the direction of the tab bar (so moving from Tareas to Mascota
 * travels right) with a soft scale + fade. Detail screens (Nueva tarea, Acerca de) rise from
 * the bottom like a sheet and fall back down when dismissed.
 */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.direction(): Int {
    val from = initialState.tabIndex()
    val to = targetState.tabIndex()
    return if (from < 0 || to < 0) 0 else if (to > from) 1 else -1
}

/** Auth screens never rise like sheets: they step forward/back, and the hand-off to the app zooms through. */
fun AnimatedContentTransitionScope<NavBackStackEntry>.hogarGoEnter(): EnterTransition {
    val dir = direction()
    val toDetail = targetState.tabIndex() < 0 && !targetState.isAuth()
    val authDir = if (targetState.authRank() > initialState.authRank()) 1 else -1
    return when {
        initialState.isAuth() && targetState.isAuth() ->
            if (targetState.authRank() == initialState.authRank()) {
                fadeIn(tween(DurationMs))
            } else {
                slideInHorizontally(tween(DurationMs, easing = FastOutSlowInEasing)) { authDir * it / 3 } +
                    fadeIn(tween(DurationMs * 3 / 4, delayMillis = 60))
            }
        initialState.isAuth() || targetState.isAuth() ->
            fadeIn(tween(DurationMs, delayMillis = 80)) +
                scaleIn(tween(DurationMs + 80, easing = FastOutSlowInEasing), initialScale = 0.9f)
        toDetail -> slideInVertically(tween(DurationMs, easing = FastOutSlowInEasing)) { it / 3 } +
            fadeIn(tween(DurationMs / 2)) +
            scaleIn(tween(DurationMs, easing = FastOutSlowInEasing), initialScale = 0.96f)
        dir == 0 -> fadeIn(tween(DurationMs)) + scaleIn(tween(DurationMs, easing = FastOutSlowInEasing), initialScale = 1.05f)
        else -> slideInHorizontally(tween(DurationMs, easing = FastOutSlowInEasing)) { dir * it / 3 } +
            fadeIn(tween(DurationMs * 3 / 4, delayMillis = 60)) +
            scaleIn(tween(DurationMs, easing = FastOutSlowInEasing), initialScale = 0.92f)
    }
}

fun AnimatedContentTransitionScope<NavBackStackEntry>.hogarGoExit(): ExitTransition {
    val dir = direction()
    val fromDetail = initialState.tabIndex() < 0 && !initialState.isAuth()
    val authDir = if (targetState.authRank() > initialState.authRank()) 1 else -1
    return when {
        initialState.isAuth() && targetState.isAuth() ->
            if (targetState.authRank() == initialState.authRank()) {
                fadeOut(tween(DurationMs / 2))
            } else {
                slideOutHorizontally(tween(DurationMs, easing = FastOutSlowInEasing)) { -authDir * it / 4 } +
                    fadeOut(tween(DurationMs / 2))
            }
        initialState.isAuth() || targetState.isAuth() ->
            fadeOut(tween(DurationMs / 2)) +
                scaleOut(tween(DurationMs, easing = FastOutSlowInEasing), targetScale = 1.08f)
        fromDetail -> slideOutVertically(tween(DurationMs, easing = FastOutSlowInEasing)) { it / 3 } +
            fadeOut(tween(DurationMs * 2 / 3)) +
            scaleOut(tween(DurationMs, easing = FastOutSlowInEasing), targetScale = 0.96f)
        dir == 0 -> fadeOut(tween(DurationMs / 2)) + scaleOut(tween(DurationMs, easing = FastOutSlowInEasing), targetScale = 0.95f)
        else -> slideOutHorizontally(tween(DurationMs, easing = FastOutSlowInEasing)) { -dir * it / 4 } +
            fadeOut(tween(DurationMs / 2)) +
            scaleOut(tween(DurationMs, easing = FastOutSlowInEasing), targetScale = 0.92f)
    }
}
