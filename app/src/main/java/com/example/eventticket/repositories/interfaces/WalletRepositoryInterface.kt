package com.example.eventticket.repositories.interfaces

import com.example.eventticket.utils.DataState
import kotlinx.coroutines.flow.Flow
import org.web3j.crypto.Credentials
import java.math.BigInteger

interface WalletRepositoryInterface {
    fun unlockCreateWallet(password: String, fileName: String): DataState<Credentials>
    suspend fun fetchAccountBalance(credentials: Credentials): Flow<DataState<BigInteger>>
}