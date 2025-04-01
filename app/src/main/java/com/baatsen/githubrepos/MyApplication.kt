package com.baatsen.githubrepos

import android.app.Application
import com.baatsen.githubrepos.presentation.di.appModule
import com.baatsen.githubrepos.presentation.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class MyApplication : Application() {
	
	override fun onCreate() {
		super.onCreate()
		
		startKoin {
			androidLogger()
			androidContext(this@MyApplication)
			modules(networkModule, appModule)
		}
	}
}