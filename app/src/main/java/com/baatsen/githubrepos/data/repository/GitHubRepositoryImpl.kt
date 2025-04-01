package com.baatsen.githubrepos.data.repository

import com.baatsen.githubrepos.data.PER_PAGE
import com.baatsen.githubrepos.data.model.toDomain
import com.baatsen.githubrepos.data.remote.GitHubService
import com.baatsen.githubrepos.domain.model.GitHubItem
import com.baatsen.githubrepos.domain.repository.GitHubRepository

class GitHubRepositoryImpl(private val service: GitHubService) : GitHubRepository {
	
	override suspend fun getRepos(page: Int): Result<List<GitHubItem>> {
		return try {
			val response = service.getRepos(page = page, perPage = PER_PAGE)
			Result.success(response.map { it.toDomain() })
		} catch(e: Exception) {
			Result.failure(e)
		}
	}
}
