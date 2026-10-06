package com.candyspace.stackoverflowusers.ui.navigation

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.paging.compose.collectAsLazyPagingItems
import com.candyspace.stackoverflowusers.feature.usersearch.presentation.SearchUserScreen
import com.candyspace.stackoverflowusers.feature.usersearch.presentation.UserSearchNavEvent
import com.candyspace.stackoverflowusers.feature.usersearch.presentation.UserSearchViewModel
import com.candyspace.stackoverflowusers.navigation.SearchUserRoute

/**
 * Navigation Host for the Stack Overflow Users application.
 * Configures the app navigation graph starting at [SearchUserRoute] and defines
 * destinations for user search.
 */
@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = SearchUserRoute,
        modifier = modifier,
    ) {
        searchUserDestination(
            navController = navController,
            context = context,
        )
    }
}

/**
 * Registers the [SearchUserRoute] destination in the navigation graph.
 *
 * Binds the [UserSearchViewModel], collects UI states and navigation events, and renders [SearchUserScreen].
 *
 * @param navController Controller to perform navigation.
 * @param context Context used to exit or finish the activity when navigation backstack is empty.
 */
private fun NavGraphBuilder.searchUserDestination(
    navController: NavHostController,
    context: Context,
) {
    composable<SearchUserRoute> {
        val searchViewModel: UserSearchViewModel = hiltViewModel()
        val uiState by searchViewModel.uiState.collectAsStateWithLifecycle()
        val searchQuery by searchViewModel.searchQuery.collectAsStateWithLifecycle()
        val usersPagingItems = searchViewModel.usersPagingDataFlow.collectAsLazyPagingItems()

        LaunchedEffect(Unit) {
            searchViewModel.navEvent.collect { navEvent ->
                when (navEvent) {
                    is UserSearchNavEvent.NavigateToProfile -> {
                        // User Profile destination is handled in its feature branch
                    }
                    is UserSearchNavEvent.ExitApp -> {
                        if (navController.previousBackStackEntry != null) {
                            navController.popBackStack()
                        } else {
                            (context as? Activity)?.finish()
                        }
                    }
                }
            }
        }

        SearchUserScreen(
            uiState = uiState,
            searchQuery = searchQuery,
            usersPagingItems = usersPagingItems,
            onEvent = searchViewModel::onEvent,
        )
    }
}
