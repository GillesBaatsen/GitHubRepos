package com.baatsen.githubrepos.ui

import com.baatsen.githubrepos.data.repositories.GitHubRepository
import com.baatsen.githubrepos.ui.list.getFakeRepoItem
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifySequence
import io.mockk.just
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MainViewModelTest {
	
	private val mockRepo = mockk<GitHubRepository>()
	private val viewModel = MainViewModel(mockRepo)
	private val fakeData = List(5) { index -> getFakeRepoItem(index.toLong()) }
	
	@Test
	fun `Loading state is set when loadData is called`() = runTest {
		viewModel.loadData()
		val uiState = viewModel.uiState.first()
		assert(uiState is UiState.Loading)
	}
	
	@Test
	fun `Error state is set when loadData throws an exception`() = runTest {
		coEvery { mockRepo.getRepos(1) } throws RuntimeException("Network error")
		viewModel.loadData()
		val uiState = viewModel.uiState
			.filterIsInstance<UiState.Error>()
			.first()
		assertTrue(uiState.e is RuntimeException)
		assertTrue((uiState.e.message == "Network error"))
	}
	
	@Test
	fun `Success state is set when loadData returns data`() = runTest {
		coEvery { mockRepo.getRepos(any()) } returns fakeData
		viewModel.loadData()
		val uiState = viewModel.uiState
			.filterIsInstance<UiState.Success>()
			.first()
		assertEquals(fakeData, uiState.list)
	}
	
	@Test
	fun `getRepoItemById returns correct item when uiState is Success`() = runTest {
		coEvery { mockRepo.getRepos(any()) } returns fakeData
		viewModel.loadData()
		
		viewModel.uiState
			.filterIsInstance<UiState.Success>()
			.first()
		val result = viewModel.getRepoItemById(0)
		assertEquals(fakeData[0], result)
	}
	
	@Test
	fun `getRepoItemById returns null when uiState is not Success`() = runTest {
		coEvery { mockRepo.getRepos(1) } throws RuntimeException("Network error")
		viewModel.loadData()
		val result = viewModel.getRepoItemById(0)
		assertNull(result)
	}
	
	@Test
	fun `pageForward calls loadData`() = runTest {
		val mockViewModel = spyk(MainViewModel(mockRepo))
		coEvery { mockViewModel.loadData() } just Runs
		mockViewModel.pageForward()
		coVerifySequence {
			mockViewModel.pageForward()
			mockViewModel.loadData()
		}
	}
	
	@Test
	fun `pageBack does not call loadData when at start`() = runTest {
		val mockViewModel = spyk(MainViewModel(mockRepo))
		coEvery { mockViewModel.loadData() } just Runs
		mockViewModel.pageBack()
		coVerify {
			mockViewModel.pageBack()
		}
		coVerify(exactly = 0) { mockViewModel.loadData() }
	}
}