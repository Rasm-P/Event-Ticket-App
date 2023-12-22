package com.example.eventticket.di

import com.example.eventticket.repositories.TicketRepository
import com.example.eventticket.repositories.LogRepository
import com.example.eventticket.repositories.RegisterRepository
import com.example.eventticket.repositories.ResaleRepository
import com.example.eventticket.repositories.WalletRepository
import com.example.eventticket.repositories.interfaces.TicketRepositoryInterface
import com.example.eventticket.repositories.interfaces.LogRepositoryInterface
import com.example.eventticket.repositories.interfaces.RegisterRepositoryInterface
import com.example.eventticket.repositories.interfaces.ResaleRepositoryInterface
import com.example.eventticket.repositories.interfaces.WalletRepositoryInterface
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun walletRepository(
        walletRepository: WalletRepository
    ): WalletRepositoryInterface

    @Binds
    abstract fun logRepository(
        logRepository: LogRepository
    ): LogRepositoryInterface

    @Binds
    abstract fun ticketRepository(
        ticketRepository: TicketRepository
    ): TicketRepositoryInterface

    @Binds
    abstract fun resaleRepository(
        resaleRepository: ResaleRepository
    ): ResaleRepositoryInterface

    @Binds
    abstract fun registerRepository(
        registerRepository: RegisterRepository
    ): RegisterRepositoryInterface
}