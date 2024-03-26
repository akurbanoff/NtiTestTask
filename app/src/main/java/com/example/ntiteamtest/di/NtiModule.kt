package com.example.ntiteamtest.di

import android.app.Application
import android.content.res.Resources
import com.example.ntiteamtest.data.remote.Api
import com.example.ntiteamtest.data.repository.ApiRepository
import com.example.ntiteamtest.data.repository.CartRepository
import com.example.ntiteamtest.data.repository.Repository
import com.example.ntiteamtest.ui.view_models.ApiViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.Callable
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NtiModule {

    @Provides
    @Singleton
    fun provideResources(application: Application) : Resources{
        return application.resources
    }

    @Provides
    @Singleton
    fun provideExecutor(): ExecutorService {
        return Executors.newSingleThreadExecutor()
    }

    @Provides
    @Singleton
    fun provideMockWebServer(executor: ExecutorService): MockWebServer{
        val mockWebServer = MockWebServer()
        executor.execute {
            try {
                mockWebServer.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return mockWebServer
    }

    @Provides
    @Singleton
    fun provideBaseUrl(executor: ExecutorService, mockWebServer: MockWebServer): HttpUrl {
        return executor.submit(Callable {
            mockWebServer.url("/")
        }).get()
    }

    @Provides
    @Singleton
    fun provideRetrofit(mockWebServer: MockWebServer, baseUrl: HttpUrl): Api{
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(Api::class.java)
    }

    @Provides
    @Singleton
    fun provideCart(): CartRepository{
        return CartRepository()
    }

    @Provides
    @Singleton
    fun provideRepository(api: Api, mockWebServer: MockWebServer, resources: Resources) : Repository{
        return ApiRepository(api, mockWebServer, resources)
    }

    @Provides
    @Singleton
    fun provideViewModel(repository: Repository, cart: CartRepository) : ApiViewModel{
        return ApiViewModel(repository, cart)
    }
}