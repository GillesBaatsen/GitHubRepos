package com.baatsen.githubrepos.presentation.ui.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.baatsen.githubrepos.presentation.ui.composables.AppBarWithNavigation
import com.baatsen.githubrepos.presentation.ui.details.DetailsScreen
import com.baatsen.githubrepos.presentation.ui.list.ListScreen
import com.baatsen.githubrepos.presentation.ui.main.NavigationRoutes.Companion.DETAILS
import com.baatsen.githubrepos.presentation.ui.main.NavigationRoutes.Companion.HOME
import com.baatsen.githubrepos.presentation.ui.theme.GitHubReposTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

sealed class NavigationRoutes {
	companion object {
		
		const val HOME = "home"
		const val DETAILS = "details/{repoItemId}"
	}
}

class MainActivity : ComponentActivity() {
	
	private val viewModel by viewModel<MainViewModel>()
	
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		viewModel.fetchRepositories()
		enableEdgeToEdge()
		setContent {
			GitHubReposTheme {
				val navController = rememberNavController()
				val backStackEntry by navController.currentBackStackEntryAsState()
				val currentRoute = backStackEntry?.destination?.route
				val uiState by viewModel.uiState.collectAsState()
				Scaffold(
					topBar = {
						AppBarWithNavigation(
							currentRoute = currentRoute,
							canGoBack = if(uiState is UiState.Success) (uiState as UiState.Success).canGoBack else false,
							canGoForward = if(uiState is UiState.Success) (uiState as UiState.Success).canGoForward else false,
							navController = navController,
							onPageBack = { viewModel.pageBack() },
							onPageForward = { viewModel.pageForward() },
						)
					},
				) { innerPadding ->
					Column(modifier = Modifier.padding(innerPadding)) {
						NavHost(navController = navController, startDestination = HOME) {
							composable("home") {
								ListScreen(
									uiState = uiState,
									onRetryClick = { viewModel.fetchRepositories() },
									navController = navController,
								)
							}
							composable(
								route = DETAILS,
								arguments = listOf(navArgument("repoItemId") { type = NavType.LongType })
							) { backStackEntry ->
								val repoItemId = backStackEntry.arguments?.getLong("repoItemId")
								val repoItem = viewModel.getRepoItemById(repoItemId)
								if(repoItem != null) {
									DetailsScreen(repoItem = repoItem)
								}
							}
						}
					}
				}
			}
		}
	}
}
