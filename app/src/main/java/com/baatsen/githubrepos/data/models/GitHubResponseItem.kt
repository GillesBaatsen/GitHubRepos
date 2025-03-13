package com.baatsen.githubrepos.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GitHubResponseItem(
	val id: Long,
	val name: String,
	@SerialName("full_name") val fullName: String,
	val private: Boolean,
	val owner: Owner,
	@SerialName("html_url") val htmlUrl: String,
	val description: String? = null,
	val visibility: String,
)

@Serializable
data class Owner(
	@SerialName("avatar_url") val avatarUrl: String,
)