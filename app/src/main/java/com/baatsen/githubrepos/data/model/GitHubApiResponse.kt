package com.baatsen.githubrepos.data.model

import com.baatsen.githubrepos.domain.model.GitHubItem
import kotlinx.serialization.Serializable

@Serializable
data class GitHubApiResponse(
	val id: Long,
	val name: String,
	val full_name: String,
	val private: Boolean,
	val owner: Owner,
	val html_url: String,
	val description: String? = null,
	val visibility: String,
)

@Serializable
data class Owner(
	val avatar_url: String,
)

fun GitHubApiResponse.toDomain(): GitHubItem {
	return GitHubItem(
		id = id,
		name = name,
		fullName = full_name,
		isPrivate = private,
		owner = owner.toDomain(),
		htmlUrl = html_url,
		description = description,
		visibility = visibility
	)
}

fun Owner.toDomain(): com.baatsen.githubrepos.domain.model.Owner {
	return com.baatsen.githubrepos.domain.model.Owner(
		avatarUrl = avatar_url
	)
}