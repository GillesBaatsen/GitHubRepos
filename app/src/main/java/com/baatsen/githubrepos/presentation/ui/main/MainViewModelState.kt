package com.baatsen.githubrepos.presentation.ui.main

import com.baatsen.githubrepos.domain.model.GitHubItem

sealed class UiState {
	data object Loading : UiState()
	data class Error(val e: Exception) : UiState()
	data class Success(
		val list: List<GitHubItem>,
		val canGoBack: Boolean,
		val canGoForward: Boolean,
	) : UiState()
}

data class PageState(
	var currentPage: Int = 1,
	var maxPage: Int = -1,
) {
	
	val canGoBack: Boolean get() = currentPage > 1
	val canGoForward: Boolean get() = maxPage == -1 || currentPage < maxPage
	
	fun nextPage() {
		if(canGoForward) currentPage++
	}
	
	fun previousPage() {
		if(canGoBack) currentPage--
	}
	
	fun updateMaxPage() {
		if(maxPage == -1 || currentPage > maxPage) maxPage = currentPage
	}
}
