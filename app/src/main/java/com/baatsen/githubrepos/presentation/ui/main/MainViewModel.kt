package com.baatsen.githubrepos.presentation.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baatsen.githubrepos.data.PER_PAGE
import com.baatsen.githubrepos.domain.model.GitHubItem
import com.baatsen.githubrepos.domain.usecase.FetchGithubReposUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MainViewModel(
	private val fetchGithubReposUseCase: FetchGithubReposUseCase,
) : ViewModel() {
	
	private var pageState = PageState()
	
	private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
	val uiState: StateFlow<UiState> = _uiState
	
	fun fetchRepositories() {
		_uiState.value = UiState.Loading
		viewModelScope.launch {
			val result = fetchGithubReposUseCase(pageState.currentPage)
			_uiState.value = result.fold(
				onSuccess = { list ->
					if(list.size < PER_PAGE) pageState.updateMaxPage()
					UiState.Success(
						list = list,
						canGoBack = pageState.canGoBack,
						canGoForward = pageState.canGoForward
					)
				},
				onFailure = { UiState.Error(it as Exception) }
			)
		}
	}
	
	fun pageBack() {
		if(pageState.canGoBack) {
			pageState.previousPage()
			fetchRepositories()
		}
	}
	
	fun pageForward() {
		if(pageState.canGoForward) {
			pageState.nextPage()
			fetchRepositories()
		}
	}
	
	fun getRepoItemById(repoItemId: Long?): GitHubItem? {
		val state = uiState.value
		return if(state is UiState.Success) {
			state.list.firstOrNull { it.id == repoItemId }
		} else null
	}
}
