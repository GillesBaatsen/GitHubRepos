package com.baatsen.githubrepos.ui.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.baatsen.githubrepos.R
import com.baatsen.githubrepos.data.models.GitHubResponseItem
import com.baatsen.githubrepos.data.models.Owner
import com.baatsen.githubrepos.ui.UiState
import com.baatsen.githubrepos.ui.composables.TitleValueText

@Composable
fun ListScreen(uiState: UiState, onRetryClick: () -> Unit, navController: NavController) {
	when(uiState) {
		is UiState.Loading -> {
			Box(
				modifier = Modifier.fillMaxSize(),
				contentAlignment = Alignment.Center
			) {
				CircularProgressIndicator()
			}
		}
		
		is UiState.Error -> {
			Column(
				modifier = Modifier.fillMaxSize(),
				verticalArrangement = Arrangement.Center,
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Text(
					text = stringResource(R.string.loading_error),
					modifier = Modifier.clickable { onRetryClick() }
				)
				Button(onClick = { onRetryClick() }) {
					Text(stringResource(R.string.retry))
				}
			}
		}
		
		is UiState.Success -> {
			val repoItems = uiState.list
			LazyColumn {
				items(repoItems) { repoItem ->
					RepoItem(
						repoItem = repoItem,
						navController = navController
					)
				}
				if (repoItems.isEmpty()) {
					item {
						Box(
							modifier = Modifier.fillMaxSize(),
							contentAlignment = Alignment.Center
						) {
							Text(text = stringResource(R.string.no_items))
						}
					}
				}
			}
		}
	}
}

@Composable
fun RepoItem(repoItem: GitHubResponseItem, navController: NavController) {
	Row(
		verticalAlignment = Alignment.CenterVertically,
		modifier = Modifier
			.fillMaxWidth()
			.padding(16.dp)
			.clickable {
				navController.navigate("details/${repoItem.id}")
			}
	) {
		AsyncImage(
			model = repoItem.owner.avatarUrl,
			contentDescription = "Repo Owner Avatar",
			error = painterResource(R.drawable.ic_launcher_background),
			fallback = painterResource(R.drawable.ic_launcher_background),
			placeholder = painterResource(R.drawable.ic_launcher_background),
			modifier = Modifier
				.size(64.dp)
				.padding(end = 16.dp)
		)
		Column {
			TitleValueText(stringResource(R.string.name), repoItem.name)
			TitleValueText(stringResource(R.string.visibility), repoItem.visibility)
			TitleValueText(stringResource(R.string.is_private), if(repoItem.private) stringResource(R.string.yes) else stringResource(R.string.no))
		}
	}
}

fun getFakeRepoItem(id: Long = 1L) = GitHubResponseItem(
	id = id,
	name = "Sample Repo $id",
	fullName = "User/SampleRepo$id",
	description = "This is a sample repository $id.",
	visibility = "public",
	private = false,
	owner = Owner(avatarUrl = ""),
	htmlUrl = ""
)

@Preview(showBackground = true)
@Composable
fun RepoItemPreview() {
	RepoItem(repoItem = getFakeRepoItem(), navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun ListScreenPreview() {
	val dummyUiState = UiState.Success(
		List(5) { index -> getFakeRepoItem(index.toLong()) }
	)
	
	ListScreen(
		uiState = dummyUiState,
		onRetryClick = { },
		navController = rememberNavController(),
	)
}

@Preview(showBackground = true)
@Composable
fun ListScreenEmptyPreview() {
	val dummyUiState = UiState.Success(emptyList())
	
	ListScreen(
		uiState = dummyUiState,
		onRetryClick = { },
		navController = rememberNavController(),
	)
}

@Preview(showBackground = true)
@Composable
fun ListScreenErrorPreview() {
	val dummyUiState = UiState.Error(IllegalStateException())
	
	ListScreen(
		uiState = dummyUiState,
		onRetryClick = { },
		navController = rememberNavController(),
	)
}
@Preview(showBackground = true)
@Composable
fun ListScreenLoadingPreview() {
	val dummyUiState = UiState.Loading
	
	ListScreen(
		uiState = dummyUiState,
		onRetryClick = { },
		navController = rememberNavController(),
	)
}


