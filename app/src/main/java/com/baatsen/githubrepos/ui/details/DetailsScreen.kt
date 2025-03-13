package com.baatsen.githubrepos.ui.details

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.baatsen.githubrepos.R
import com.baatsen.githubrepos.data.models.GitHubResponseItem
import com.baatsen.githubrepos.data.models.Owner
import com.baatsen.githubrepos.ui.composables.OpenUrlButton
import com.baatsen.githubrepos.ui.composables.TitleValueText

@Composable
fun DetailsScreen(repoItem: GitHubResponseItem) {
	Column(
		modifier = Modifier
			.fillMaxSize()
			.padding(16.dp)
	) {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.padding(bottom = 16.dp),
			contentAlignment = Alignment.Center
		) {
			AsyncImage(
				model = repoItem.owner.avatarUrl,
				contentDescription = "Repo Owner Avatar",
				error = painterResource(R.drawable.ic_launcher_background),
				fallback = painterResource(R.drawable.ic_launcher_background),
				placeholder = painterResource(R.drawable.ic_launcher_background),
				modifier = Modifier
					.size(128.dp)
			)
		}
		TitleValueText(stringResource(R.string.name), repoItem.name)
		TitleValueText(stringResource(R.string.full_name), repoItem.fullName)
		TitleValueText(stringResource(R.string.description), repoItem.description)
		TitleValueText(stringResource(R.string.visibility), repoItem.visibility)
		TitleValueText(stringResource(R.string.is_private), if(repoItem.private) stringResource(R.string.yes) else stringResource(R.string.no))
		OpenUrlButton(repoItem.htmlUrl)
	}
}

@Preview(showBackground = true)
@Composable
fun DetailsScreenPreview() {
	val repoItem = GitHubResponseItem(
		id = 1L,
		name = "Sample Repo",
		fullName = "user/sample-repo",
		owner = Owner(avatarUrl = ""),
		description = "This is a sample description.",
		visibility = "Public",
		private = false,
		htmlUrl = ""
	)
	
	DetailsScreen(repoItem = repoItem)
}
