package com.agnidating.agni.module

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.agnidating.agni.BuildConfig
import com.agnidating.agni.network.ApiService
import com.agnidating.agni.utils.CommonKeys
import com.agnidating.agni.utils.sharedPreference.SharedPrefs
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
class ApplicationModule {

    @Provides
    @Singleton
    fun providerSharedPref(activity: Application): SharedPreferences {
        return (activity as Context).getSharedPreferences(
            "AgniApp",
            Context.MODE_PRIVATE
        )
    }

    @Singleton
    @Provides
    fun provideLogger(): HttpLoggingInterceptor {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY
        return logger
    }

    @Provides
    @Singleton
    fun addInterceptor(sharedPrefs: SharedPrefs): Interceptor {
        return Interceptor { chain ->
            val request: Request = if (sharedPrefs.getString(CommonKeys.TOKEN).isNullOrBlank()) {
                chain.request().newBuilder().build()
            } else {
                chain.request().newBuilder()
                    .addHeader("Authorization", sharedPrefs.getString(CommonKeys.TOKEN)!!)
                    .build()
            }
            chain.proceed(request)
        }
    }


    @Singleton
    @Provides
    fun provideClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        interceptor: Interceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .readTimeout(1, TimeUnit.MINUTES)
            .writeTimeout(1, TimeUnit.MINUTES)
            .connectTimeout(1, TimeUnit.MINUTES)
            .addInterceptor(interceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()

    }

    @Singleton
    @Provides
    fun getRetrofitClient(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideApiService(retrofit: Retrofit): ApiService {
        return retrofit.create(ApiService::class.java)
    }


}