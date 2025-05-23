package com.baatsen.githubrepos.data.remote

import com.baatsen.githubrepos.data.model.GitHubApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubService {
	
	@GET("repos")
	suspend fun getRepos(
		@Query("page") page: Int,
		@Query("per_page") perPage: Int,
	): List<GitHubApiResponse>
}
