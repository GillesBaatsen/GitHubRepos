package com.baatsen.githubrepos.data.repository

import com.baatsen.githubrepos.data.PER_PAGE
import com.baatsen.githubrepos.data.model.GitHubApiResponse
import com.baatsen.githubrepos.data.model.Owner
import com.baatsen.githubrepos.data.model.toDomain
import com.baatsen.githubrepos.data.remote.GitHubService
import com.baatsen.githubrepos.domain.model.GitHubItem
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class GitHubRepositoryImplTest {
	
	private val gitHubService: GitHubService = mockk()
	private val gitHubRepository = GitHubRepositoryImpl(gitHubService)
	
	@Test
	fun `should return list of GitHubItems on success`() = runTest {
		val fakeApiResponse = List(5) { index -> getFakeRepoApiResponse(index.toLong()) }
		val expectedResult = fakeApiResponse.map { it.toDomain() }
		
		coEvery { gitHubService.getRepos(1, PER_PAGE) } returns fakeApiResponse
		
		val result = gitHubRepository.getRepos(1)
		assertTrue(result.isSuccess)
		assertEquals(expectedResult, result.getOrNull())
	}
	
	@Test
	fun `should return failure when API call fails`() = runTest {
		val exception = RuntimeException("Network error")
		coEvery { gitHubService.getRepos(1, PER_PAGE) } throws exception
		val result = gitHubRepository.getRepos(1)
		assertTrue(result.isFailure)
		assertEquals(exception, result.exceptionOrNull())
	}
	
	@Test
	fun `should return empty list when no repositories are found`() = runTest {
		val fakeApiResponse = emptyList<GitHubApiResponse>()
		val expectedResult = emptyList<GitHubItem>()
		
		coEvery { gitHubService.getRepos(1, PER_PAGE) } returns fakeApiResponse
		
		val result = gitHubRepository.getRepos(1)
		
		assertTrue(result.isSuccess)
		assertEquals(expectedResult, result.getOrNull())
	}
	
	@Test
	fun `should fetch different pages of repositories correctly`() = runTest {
		val fakePage1 = List(10) { index -> getFakeRepoApiResponse(index.toLong()) }
		val fakePage2 = List(9) { index -> getFakeRepoApiResponse(index.toLong() + 5) }
		
		coEvery { gitHubService.getRepos(1, PER_PAGE) } returns fakePage1
		coEvery { gitHubService.getRepos(2, PER_PAGE) } returns fakePage2
		
		val resultPage1 = gitHubRepository.getRepos(1)
		assertTrue(resultPage1.isSuccess)
		assertEquals(fakePage1.map { it.toDomain() }, resultPage1.getOrNull())
		
		val resultPage2 = gitHubRepository.getRepos(2)
		assertTrue(resultPage2.isSuccess)
		assertEquals(fakePage2.map { it.toDomain() }, resultPage2.getOrNull())
	}
	
	private fun getFakeRepoApiResponse(id: Long) = GitHubApiResponse(
		id = id,
		name = "Repo $id",
		owner = Owner("http://www.image.nl/image.jpg"),
		description = "Description for repo $id",
		full_name = "Marco van Basten $id",
		private = false,
		html_url = "http://www.image.nl/",
		visibility = "visible"
	)
}
