package com.baatsen.githubrepos.presentation.ui.composables

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.baatsen.githubrepos.R
import com.baatsen.githubrepos.presentation.ui.main.NavigationRoutes.Companion.DETAILS
import com.baatsen.githubrepos.presentation.ui.main.NavigationRoutes.Companion.HOME

@Composable
fun TitleValueText(title: String, value: String?, modifier: Modifier = Modifier) {
	Row(modifier = modifier) {
		Text(text = title, modifier = Modifier.padding(end = 8.dp), fontWeight = FontWeight.Bold)
		Text(text = value ?: "")
	}
}

@Composable
fun OpenUrlButton(url: String, modifier: Modifier = Modifier) {
	val context = LocalContext.current
	val errorMessage = stringResource(R.string.error_loading_url)
	Box(
		modifier = modifier.fillMaxWidth(),
		contentAlignment = Alignment.Center
	) {
		Button(onClick = {
			try {
				val intent = Intent(Intent.ACTION_VIEW, url.toUri())
				context.startActivity(intent)
			} catch(e: Exception) {
				Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show()
			}
		}) {
			Text(text = stringResource(R.string.open_in_browser))
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBarWithNavigation(
	currentRoute: String?,
	canGoBack: Boolean,
	canGoForward: Boolean,
	navController: NavController,
	onPageBack: () -> Unit,
	onPageForward: () -> Unit,
) {
	TopAppBar(
		navigationIcon = {
			if(currentRoute != HOME) {
				IconButton(onClick = { navController.popBackStack() }) {
					Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
				}
			}
		},
		title = { TopAppBarTitle(currentRoute) },
		actions = {
			if(currentRoute == HOME) {
				IconButton(onClick = { onPageBack() }, enabled = canGoBack) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
						contentDescription = stringResource(R.string.previous),
					)
				}
				IconButton(onClick = { onPageForward() }, enabled = canGoForward) {
					Icon(
						imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
						contentDescription = stringResource(R.string.next),
					)
				}
			}
		},
	)
}

@Composable
private fun TopAppBarTitle(currentRoute: String?) {
	val title = when(currentRoute) {
		HOME -> stringResource(R.string.app_name)
		DETAILS -> stringResource(R.string.details)
		else -> ""
	}
	Text(text = title, maxLines = 1)
}

@Preview(showBackground = true)
@Composable
fun PreviewTitleValueText() {
	TitleValueText(title = "Name", value = "My value")
}

@Preview(showBackground = true)
@Composable
fun PreviewOpenUrlButton() {
	OpenUrlButton(url = "")
}

@Preview(showBackground = true)
@Composable
fun PreviewAppBarWithNavigationHome() {
	val navController = rememberNavController()
	
	AppBarWithNavigation(
		currentRoute = HOME,
		canGoBack = true,
		canGoForward = true,
		navController = navController,
		onPageBack = {},
		onPageForward = {},
	)
}

@Preview(showBackground = true)
@Composable
fun PreviewAppBarWithNavigationDetails() {
	val navController = rememberNavController()
	
	AppBarWithNavigation(
		currentRoute = DETAILS,
		canGoBack = true,
		canGoForward = true,
		navController = navController,
		onPageBack = {},
		onPageForward = {},
	)
}