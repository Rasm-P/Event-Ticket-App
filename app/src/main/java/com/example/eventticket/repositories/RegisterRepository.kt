package com.example.eventticket.repositories

import android.util.Log
import com.example.eventticket.config.RepositoryConfiguration
import com.example.eventticket.contractWrappers.RegisterContract
import com.example.eventticket.models.QrCodeMessage
import com.example.eventticket.repositories.interfaces.RegisterRepositoryInterface
import com.example.eventticket.utils.DataState
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.future.await
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.StaticEIP1559GasProvider
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RegisterRepository @Inject internal constructor(private val web3j: Web3j, private val repositoryConfiguration: RepositoryConfiguration) :
    RegisterRepositoryInterface {

    override suspend fun registerTicket (credentials: Credentials, qrCodeMessage: QrCodeMessage) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadRegisterContract(web3j, credentials)
            val response = contract.registerTicket(
                qrCodeMessage.e,
                qrCodeMessage.t,
                qrCodeMessage.h,
                qrCodeMessage.r,
                qrCodeMessage.s,
                qrCodeMessage.v
            ).sendAsync().await()
            emit(DataState.Success(response.transactionHash))
        } catch (e: Exception) {
            Log.d("RegisterTicket",e.message.toString())
            emit(DataState.Error("Ticket could not be registered!"))
        }
    }

    // Adapted from ArtNiche app loadContract function, Sergio Sánchez Sánchez: https://github.com/sergio11/art_niche_nft_marketplace/blob/main/app/src/main/java/com/dreamsoftware/artcollectibles/data/blockchain/datasource/impl/ArtMarketplaceBlockchainDataSourceImpl.kt (Accessed: 17-10-2023)
    private fun loadRegisterContract(web3j: Web3j, credentials: Credentials): RegisterContract {
        with(repositoryConfiguration) {
            val transactionManager = RawTransactionManager(
                web3j,
                credentials,
                chainId,
                requestAttempts,
                requestSleepDuration.toLong()
            )
            val gasProvider = StaticEIP1559GasProvider(
                chainId,
                maxFeePerGas,
                maxPriorityFeePerGas,
                gasLimit
            )
            return RegisterContract.load(
                registerContract,
                web3j,
                transactionManager,
                gasProvider
            )
        }
    }
}