package com.baatsen.githubrepos.ui

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
import com.baatsen.githubrepos.ui.NavigationRoutes.Companion.DETAILS
import com.baatsen.githubrepos.ui.NavigationRoutes.Companion.HOME
import com.baatsen.githubrepos.ui.composables.AppBarWithNavigation
import com.baatsen.githubrepos.ui.details.DetailsScreen
import com.baatsen.githubrepos.ui.list.ListScreen
import com.baatsen.githubrepos.ui.theme.GitHubReposTheme
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
		viewModel.loadData()
		enableEdgeToEdge()
		setContent {
			GitHubReposTheme {
				val navController = rememberNavController()
				val backStackEntry by navController.currentBackStackEntryAsState()
				val currentRoute = backStackEntry?.destination?.route
				Scaffold(
					topBar = {
						AppBarWithNavigation(
							currentRoute = currentRoute,
							navController = navController,
							onPageBack = { viewModel.pageBack() },
							onPageForward = { viewModel.pageForward() },
						)
					},
				) { innerPadding ->
					Column(modifier = Modifier.padding(innerPadding)) {
						NavHost(navController = navController, startDestination = HOME) {
							composable("home") {
								val uiState by viewModel.uiState.collectAsState()
								ListScreen(
									uiState = uiState,
									onRetryClick = { viewModel.loadData() },
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
