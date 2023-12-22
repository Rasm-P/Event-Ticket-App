package com.example.eventticket.repositories.interfaces

import com.example.eventticket.models.dto.EventVenueDTO
import com.example.eventticket.models.dto.NFTDataDTO
import com.example.eventticket.utils.DataState
import kotlinx.coroutines.flow.Flow
import org.web3j.crypto.Credentials
import java.math.BigInteger

interface TicketRepositoryInterface {
    suspend fun fetchAllEvents(credentials: Credentials): Flow<DataState<List<EventVenueDTO>>>
    suspend fun fetchSeatsSoldForEvent(
        credentials: Credentials,
        eventId: BigInteger
    ): Flow<DataState<List<BigInteger>>>
    suspend fun purchaseTickets(
        credentials: Credentials,
        eventId: BigInteger,
        seats: List<Int>,
        cost: BigInteger
    ): Flow<DataState<String>>
    suspend fun fetchCustomerTicketsForEvent(
        credentials: Credentials,
        eventId: BigInteger
    ): Flow<DataState<List<BigInteger>>>
    suspend fun fetchSenderNFTs(credentials: Credentials): Flow<DataState<List<NFTDataDTO>>>
    suspend fun fetchContractsApproved(credentials: Credentials): Flow<DataState<Boolean>>
    suspend fun setContractApprovals(credentials: Credentials): Flow<DataState<String>>
    suspend fun fetchAccessRole(credentials: Credentials, address: String): Flow<DataState<String>>
}