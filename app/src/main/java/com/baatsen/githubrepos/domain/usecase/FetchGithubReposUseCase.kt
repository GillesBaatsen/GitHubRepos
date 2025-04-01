package com.baatsen.githubrepos.domain.usecase

import com.baatsen.githubrepos.domain.model.GitHubItem
import com.baatsen.githubrepos.domain.repository.GitHubRepository

class FetchGithubReposUseCase(private val repository: GitHubRepository) {
	
	suspend operator fun invoke(page: Int): Result<List<GitHubItem>> = repository.getRepos(page)
}
