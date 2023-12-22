package com.example.eventticket.di

import com.example.eventticket.BuildConfig
import com.example.eventticket.config.RepositoryConfiguration
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import org.web3j.protocol.Web3j
import org.web3j.protocol.http.HttpService
import org.web3j.utils.Async
import javax.inject.Singleton

const val DEFAULT_BLOCK_TIME = 2 * 1000

@Module
@InstallIn(SingletonComponent::class)
object ProviderModule {
    @Provides
    @Singleton
    fun provideWeb3j(): Web3j {
        return Web3j.build(HttpService("https://polygon-mumbai.g.alchemy.com/v2/${BuildConfig.API_KEY}"),DEFAULT_BLOCK_TIME.toLong(),
            Async.defaultExecutorService())
    }

    @Provides
    @Singleton
    fun provideRepositoryConfig(): RepositoryConfiguration {
        return RepositoryConfiguration()
    }
}