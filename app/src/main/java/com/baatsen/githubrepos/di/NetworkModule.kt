package com.baatsen.githubrepos.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import com.baatsen.githubrepos.services.GitHubService
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.Cache
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.File
import java.util.concurrent.TimeUnit

const val CACHE_CONTROL_HEADER = "Cache-Control"
const val TEN_MEGABYTE = 10 * 1024 * 1024L

@OptIn(ExperimentalSerializationApi::class)
val networkModule = module {
	
	val logging = HttpLoggingInterceptor().apply { setLevel(HttpLoggingInterceptor.Level.BODY) }
	
	single {
		val cacheDir = File(get<Context>().cacheDir, "http")
		val cache = Cache(
			cacheDir,
			TEN_MEGABYTE
		)
		
		OkHttpClient.Builder()
			.cache(cache)
			.addNetworkInterceptor(get<CacheInterceptor>())
			.addInterceptor(logging)
			.build()
	}
	
	fun provideJson() = Json {
		ignoreUnknownKeys = true
		isLenient = true
	}
	
	fun provideKotlinSerialization(json: Json): Converter.Factory {
		val contentType = "application/json".toMediaType()
		return json.asConverterFactory(contentType)
	}
	
	single { CacheInterceptor(get()) }
	single { provideJson() }
	single { provideKotlinSerialization(get()) }
	
	single<Retrofit> {
		Retrofit.Builder()
			.client(get())
			.baseUrl("https://api.github.com/users/abnamrocoesd/")
			.addConverterFactory(provideKotlinSerialization(provideJson()))
			.build()
	}
	
	single {
		get<Retrofit>().create(GitHubService::class.java)
	}
}

private fun isNetworkAvailable(context: Context): Boolean {
	val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
	val network: Network? = connectivityManager.activeNetwork
	val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
	return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
}

class CacheInterceptor(private val context: Context) : Interceptor {
	
	override fun intercept(chain: Interceptor.Chain): Response {
		val request = chain.request()
		
		if(isNetworkAvailable(context)) {
			val cacheControl = CacheControl.Builder()
				.maxAge(1, TimeUnit.DAYS)
				.build()
			
			return chain.proceed(request).newBuilder()
				.header(CACHE_CONTROL_HEADER, cacheControl.toString())
				.build()
		} else {
			val cacheControl = CacheControl.Builder()
				.maxStale(1, TimeUnit.DAYS)
				.build()
			
			return chain.proceed(request).newBuilder()
				.header(CACHE_CONTROL_HEADER, cacheControl.toString())
				.build()
		}
	}
}

