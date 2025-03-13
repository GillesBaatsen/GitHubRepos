package com.baatsen.githubrepos.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baatsen.githubrepos.data.models.GitHubResponseItem
import com.baatsen.githubrepos.data.repositories.GitHubRepository
import com.baatsen.githubrepos.data.repositories.PER_PAGE
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UiState {
	data object Loading : UiState()
	data class Error(val e: Exception) : UiState()
	data class Success(val list: List<GitHubResponseItem>) : UiState()
}

class MainViewModel(
	private val repository: GitHubRepository,
) : ViewModel() {
	
	private var page = 1
	private var maxPage = -1
	private val _uiState = MutableStateFlow<UiState>(UiState.Loading)
	val uiState: StateFlow<UiState> = _uiState
	
	fun loadData() {
		_uiState.value = UiState.Loading
		viewModelScope.launch {
			try {
				val data = repository.getRepos(page)
				if(data.size < PER_PAGE) {
					maxPage = page
				}
				_uiState.value = UiState.Success(data)
			} catch(e: Exception) {
				_uiState.value = UiState.Error(e)
			}
		}
	}
	
	fun getRepoItemById(repoItemId: Long?): GitHubResponseItem? =
		if(uiState.value is UiState.Success) {
			(uiState.value as UiState.Success).list.firstOrNull { it.id == repoItemId }
		} else null
	
	fun pageBack() {
		if(page > 1) {
			page--
			loadData()
		}
	}
	
	fun pageForward() {
		if(maxPage == -1 || page < maxPage) {
			page++
			loadData()
		}
	}
}
