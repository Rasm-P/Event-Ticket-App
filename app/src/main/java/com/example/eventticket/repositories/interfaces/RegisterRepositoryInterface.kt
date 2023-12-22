package com.example.eventticket.repositories.interfaces

import com.example.eventticket.models.QrCodeMessage
import com.example.eventticket.utils.DataState
import kotlinx.coroutines.flow.Flow
import org.web3j.crypto.Credentials

interface RegisterRepositoryInterface {
    suspend fun registerTicket(
        credentials: Credentials,
        qrCodeMessage: QrCodeMessage
    ): Flow<DataState<String>>
}