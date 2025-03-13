package com.baatsen.githubrepos.data.repositories

import com.baatsen.githubrepos.services.GitHubService

const val PER_PAGE = 10

class GitHubRepository(private val service: GitHubService) {
	
	suspend fun getRepos(page: Int) =
		service.getRepos(
			page = page,
			perPage = PER_PAGE
		)
}