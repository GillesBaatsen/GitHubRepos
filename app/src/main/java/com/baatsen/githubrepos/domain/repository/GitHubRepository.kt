package com.baatsen.githubrepos.domain.repository

import com.baatsen.githubrepos.domain.model.GitHubItem

interface GitHubRepository {
	
	suspend fun getRepos(page: Int): Result<List<GitHubItem>>
}