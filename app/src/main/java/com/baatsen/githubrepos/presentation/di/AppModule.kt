package com.baatsen.githubrepos.presentation.di

import com.baatsen.githubrepos.data.repository.GitHubRepositoryImpl
import com.baatsen.githubrepos.domain.repository.GitHubRepository
import com.baatsen.githubrepos.domain.usecase.FetchGithubReposUseCase
import com.baatsen.githubrepos.presentation.ui.main.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
	//Repositories
	single<GitHubRepository> { GitHubRepositoryImpl(get()) }
	
	//Use cases
	factory { FetchGithubReposUseCase(get()) }
	
	//ViewModels
	viewModel { MainViewModel(get()) }
}