package com.baatsen.githubrepos.domain.usecase

import com.baatsen.githubrepos.CoroutineTestExtension
import com.baatsen.githubrepos.domain.repository.GitHubRepository
import com.baatsen.githubrepos.presentation.ui.list.getFakeRepoItem
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(CoroutineTestExtension::class)
class FetchGithubReposUseCaseTest {
	
	private val repository: GitHubRepository = mockk()
	private var useCase = FetchGithubReposUseCase(repository)
	
	@Test
	fun `invoke should return list of GitHubItems on success`() = runTest {
		val fakeList = List(5) { index -> getFakeRepoItem(index.toLong()) }
		coEvery { repository.getRepos(1) } returns Result.success(fakeList)
		
		val result = useCase(1)
		
		assertTrue(result.isSuccess)
		assertEquals(fakeList, result.getOrNull())
	}
	
	@Test
	fun `invoke should return failure when repository fails`() = runTest {
		val exception = RuntimeException("API error")
		coEvery { repository.getRepos(1) } returns Result.failure(exception)
		
		val result = useCase(1)
		
		assertTrue(result.isFailure)
		assertEquals(exception, result.exceptionOrNull())
	}
}
