package com.example.eventticket.repositories

import android.util.Log
import com.example.eventticket.config.RepositoryConfiguration
import com.example.eventticket.contractWrappers.TicketContract
import com.example.eventticket.contractWrappers.TicketContract.EventVenue
import com.example.eventticket.contractWrappers.TicketContract.NFTData
import com.example.eventticket.repositories.interfaces.TicketRepositoryInterface
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
class TicketRepository @Inject internal constructor(private val web3j: Web3j, private val repositoryConfiguration: RepositoryConfiguration) :
    TicketRepositoryInterface {

    override suspend fun fetchAllEvents(credentials: Credentials) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadTicketContract(web3j, credentials)
            val response = contract.allEvents.sendAsync().await().filterIsInstance<EventVenue>()
            val listAsDTOs = response.map { it.toDTO() }
            emit(DataState.Success(listAsDTOs))
        } catch (e: Exception) {
            Log.d("ticket",e.message.toString())
            emit(DataState.Error("Events could not be fetched!"))
        }
    }

    override suspend fun fetchSeatsSoldForEvent(credentials: Credentials, eventId: BigInteger) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadTicketContract(web3j, credentials)
            val response = contract.getSeatsSoldForEvent(eventId).sendAsync().await().filterIsInstance<BigInteger>()
            emit(DataState.Success(response))
        } catch (e: Exception) {
            Log.d("seats",e.message.toString())
            emit(DataState.Error("Seats could not be fetched!"))
        }
    }

    override suspend fun purchaseTickets(credentials: Credentials, eventId: BigInteger, seats: List<Int>, cost: BigInteger) = flow {
        try {
            emit(DataState.Loading)
            val selectedSeats = seats.map { BigInteger.valueOf(it.toLong()) }
            val contract = loadTicketContract(web3j, credentials)
            val response = contract.purchaseTicket(eventId,selectedSeats,cost).sendAsync().await()
            emit(DataState.Success(response.transactionHash))
        } catch (e: Exception) {
            Log.d("purchase",e.message.toString())
            emit(DataState.Error("Something went wrong during the purchase!"))
        }
    }

    override suspend fun fetchCustomerTicketsForEvent(credentials: Credentials, eventId: BigInteger) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadTicketContract(web3j, credentials)
            val response = contract.getCustomerTicketsForEvent(eventId).sendAsync().await().filterIsInstance<BigInteger>()
            emit(DataState.Success(response))
        } catch (e: Exception) {
            Log.d("customerTickets",e.message.toString())
            emit(DataState.Error("Tickets already owned by the customer could not be fetched!"))
        }
    }

    override suspend fun fetchSenderNFTs(credentials: Credentials) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadTicketContract(web3j, credentials)
            val response = contract.senderNFTs.sendAsync().await().filterIsInstance<NFTData>()
            val listAsDTOs = response.map { it.toDTO() }
            emit(DataState.Success(listAsDTOs))
        } catch (e: Exception) {
            Log.d("customerNFTs",e.message.toString())
            emit(DataState.Error("Customer NFTs could not be fetched!"))
        }
    }

    override suspend fun fetchContractsApproved(credentials: Credentials) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadTicketContract(web3j, credentials)
            val response = contract.areContractsApproved().sendAsync().await()
            emit(DataState.Success(response))
        } catch (e: Exception) {
            Log.d("AreContractsApproved",e.message.toString())
            emit(DataState.Error("Could not fetch contract approval status!"))
        }
    }

    override suspend fun setContractApprovals(credentials: Credentials) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadTicketContract(web3j, credentials)
            val response = contract.setContractApprovals().sendAsync().await()
            emit(DataState.Success(response.transactionHash))
        } catch (e: Exception) {
            Log.d("SetContractApprovals",e.message.toString())
            emit(DataState.Error("Could not set contract approval status!"))
        }
    }

    override suspend fun fetchAccessRole(credentials: Credentials, address: String) = flow {
        try {
            emit(DataState.Loading)
            val contract = loadTicketContract(web3j, credentials)
            val response = contract.getAccessRole(address).sendAsync().await()
            emit(DataState.Success(response))
        } catch (e: Exception) {
            Log.d("GetAccessRole",e.message.toString())
            emit(DataState.Error("Could not fetch access role!"))
        }
    }

    // Adapted from ArtNiche app loadContract function, Sergio Sánchez Sánchez: https://github.com/sergio11/art_niche_nft_marketplace/blob/main/app/src/main/java/com/dreamsoftware/artcollectibles/data/blockchain/datasource/impl/ArtMarketplaceBlockchainDataSourceImpl.kt (Accessed: 17-10-2023)
    private fun loadTicketContract(web3j: Web3j, credentials: Credentials): TicketContract {
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
            return TicketContract.load(
                ticketContract,
                web3j,
                transactionManager,
                gasProvider
            )
        }
    }
}