package com.baatsen.githubrepos.ui

import com.baatsen.githubrepos.CoroutineTestExtension
import com.baatsen.githubrepos.domain.usecase.FetchGithubReposUseCase
import com.baatsen.githubrepos.presentation.ui.list.getFakeRepoItem
import com.baatsen.githubrepos.presentation.ui.main.MainViewModel
import com.baatsen.githubrepos.presentation.ui.main.UiState
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(CoroutineTestExtension::class)
class MainViewModelTest {
	
	private val fetchGithubReposUseCase: FetchGithubReposUseCase = mockk()
	private val viewModel = MainViewModel(fetchGithubReposUseCase)
	
	@Test
	fun `fetchRepositories should update uiState with Success when repos are fetched`() = runTest {
		val fakeList = List(5) { index -> getFakeRepoItem(index.toLong()) }
		coEvery { fetchGithubReposUseCase(1) } returns Result.success(fakeList)
		
		viewModel.fetchRepositories()
		advanceUntilIdle()
		val result = viewModel.uiState.value
		assertTrue(result is UiState.Success)
		assertEquals(fakeList, (result as UiState.Success).list)
		assertFalse(result.canGoBack)
		assertFalse(result.canGoForward)
	}
	
	@Test
	fun `fetchRepositories should update uiState with Error when repository fails`() = runTest {
		val exception = RuntimeException("API error")
		coEvery { fetchGithubReposUseCase(1) } returns Result.failure(exception)
		
		viewModel.fetchRepositories()
		advanceUntilIdle()
		val result = viewModel.uiState.value
		assertTrue(result is UiState.Error)
		assertEquals(exception, (result as UiState.Error).e)
		
	}
	
	@Test
	fun `pageForward and pageBack should return next and previous items and set canGoBack and canGoForward properly`() = runTest {
		val fakeList = List(10) { index -> getFakeRepoItem(index.toLong()) }
		val fakeList2 = List(9) { index -> getFakeRepoItem(index.toLong() + 10) }
		coEvery { fetchGithubReposUseCase(1) } returns Result.success(fakeList)
		coEvery { fetchGithubReposUseCase(2) } returns Result.success(fakeList2)
		//first fetch
		viewModel.fetchRepositories()
		advanceUntilIdle()
		val result = viewModel.uiState.value
		assertEquals(fakeList, (result as UiState.Success).list)
		assertFalse(result.canGoBack)
		assertTrue(result.canGoForward)
		//go forward
		viewModel.pageForward()
		advanceUntilIdle()
		val result2 = viewModel.uiState.value
		assertEquals(fakeList2, (result2 as UiState.Success).list)
		assertTrue(result2.canGoBack)
		assertFalse(result2.canGoForward)
		//go back
		viewModel.pageBack()
		advanceUntilIdle()
		val result3 = viewModel.uiState.value
		assertEquals(fakeList, (result3 as UiState.Success).list)
		assertFalse(result3.canGoBack)
		assertTrue(result3.canGoForward)
	}
	
	@Test
	fun `getRepoItemById should return correct item when valid repoId is provided`() = runTest {
		val fakeList = List(5) { index -> getFakeRepoItem(index.toLong()) }
		coEvery { fetchGithubReposUseCase(1) } returns Result.success(fakeList)
		viewModel.fetchRepositories()
		advanceUntilIdle()
		val validRepoItem = viewModel.getRepoItemById(2L)
		assertNotNull(validRepoItem)
		assertEquals(2L, validRepoItem?.id)
	}
	
	@Test
	fun `getRepoItemById should return null when no valid repoId is provided`() = runTest {
		val fakeList = List(5) { index -> getFakeRepoItem(index.toLong()) }
		coEvery { fetchGithubReposUseCase(1) } returns Result.success(fakeList)
		viewModel.fetchRepositories()
		advanceUntilIdle()
		val invalidRepoItem = viewModel.getRepoItemById(99L)
		assertNull(invalidRepoItem)
	}
	
	@Test
	fun `getRepoItemById should return null when no repos are loaded`() = runTest {
		val result = viewModel.getRepoItemById(1L)
		assertNull(result)
	}
}
