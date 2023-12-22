package com.example.eventticket.repositories

import android.util.Log
import com.example.eventticket.config.RepositoryConfiguration
import com.example.eventticket.contractWrappers.ResaleContract
import com.example.eventticket.contractWrappers.ResaleContract.ListedTicket
import com.example.eventticket.repositories.interfaces.ResaleRepositoryInterface
import com.example.eventticket.utils.DataState
import com.example.eventticket.utils.toDTO
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.future.await
import org.web3j.crypto.Credentials
import org.web3j.protocol.Web3j
import org.web3j.tx.RawTransactionManager
import org.web3j.tx.gas.StaticEIP1559GasProvider
import java.math.BigInteger
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ResaleRepository @Inject internal constructor(private val web3j: Web3j, private val repositoryConfiguration: RepositoryConfiguration) :
    ResaleRepositoryInterface {

    override suspend fun listTicketForSale(credentials: Credentials, ticketId: BigInteger, resalePrice: BigInteger) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadResaleContract(web3j, credentials)
            val response = contract.listTicketForSale(ticketId, resalePrice).sendAsync().await()
            emit(DataState.Success(response.transactionHash))
        } catch (e: Exception) {
            Log.d("listTicket",e.message.toString())
            emit(DataState.Error("Ticket could not be listed!"))
        }
    }

    override suspend fun fetchTicketsForResale(credentials: Credentials) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadResaleContract(web3j, credentials)
            val response = contract.ticketsForResale.sendAsync().await().filterIsInstance<ListedTicket>()
            val listAsDTOs = response.map { it.toDTO() }
            emit(DataState.Success(listAsDTOs))
        } catch (e: Exception) {
            Log.d("ListedTickets",e.message.toString())
            emit(DataState.Error("Listed tickets could not be fetched!"))
        }
    }

    override suspend fun fetchTicketsListedBySender(credentials: Credentials) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadResaleContract(web3j, credentials)
            val response = contract.ticketsListedBySender.sendAsync().await().filterIsInstance<ListedTicket>()
            val listAsDTOs = response.map { it.toDTO() }
            emit(DataState.Success(listAsDTOs))
        } catch (e: Exception) {
            Log.d("TicketsListedBySender",e.message.toString())
            emit(DataState.Error("Tickets listed by sender could not be fetched!"))
        }
    }

    override suspend fun withdrawFromResaleList(credentials: Credentials, listingId: BigInteger) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadResaleContract(web3j, credentials)
            val response = contract.withdrawFromResaleList(listingId).sendAsync().await()
            emit(DataState.Success(response.transactionHash))
        } catch (e: Exception) {
            Log.d("WithdrawResale",e.message.toString())
            emit(DataState.Error("Ticket could not be withdrawn from resale!"))
        }
    }

    override suspend fun purchaseTicketFromResale(credentials: Credentials, listingId: BigInteger, cost: BigInteger) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadResaleContract(web3j, credentials)
            val response = contract.purchaseTicketFromResale(listingId, cost).sendAsync().await()
            emit(DataState.Success(response.transactionHash))
        } catch (e: Exception) {
            Log.d("PurchaseResale",e.message.toString())
            emit(DataState.Error("Ticket could not be purchased from resale!"))
        }
    }

    // Adapted from ArtNiche app loadContract function, Sergio Sánchez Sánchez: https://github.com/sergio11/art_niche_nft_marketplace/blob/main/app/src/main/java/com/dreamsoftware/artcollectibles/data/blockchain/datasource/impl/ArtMarketplaceBlockchainDataSourceImpl.kt (Accessed: 17-10-2023)
    private fun loadResaleContract(web3j: Web3j, credentials: Credentials): ResaleContract {
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
            return ResaleContract.load(
                resaleContract,
                web3j,
                transactionManager,
                gasProvider
            )
        }
    }
}