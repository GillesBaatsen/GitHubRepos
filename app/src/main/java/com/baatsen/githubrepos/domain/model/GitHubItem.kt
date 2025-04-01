package com.baatsen.githubrepos.domain.model

data class GitHubItem(
	val id: Long,
	val name: String,
	val fullName: String,
	val isPrivate: Boolean,
	val owner: Owner,
	val htmlUrl: String,
	val description: String? = null,
	val visibility: String,
)

data class Owner(
	val avatarUrl: String,
)