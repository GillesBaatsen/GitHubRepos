package com.baatsen.githubrepos.di

import com.baatsen.githubrepos.data.repositories.GitHubRepository
import com.baatsen.githubrepos.ui.MainViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { GitHubRepository(get()) }
    viewModel { MainViewModel(get()) }
}