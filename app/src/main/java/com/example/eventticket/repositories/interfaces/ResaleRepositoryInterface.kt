package com.example.eventticket.repositories.interfaces

import com.example.eventticket.models.dto.ListedTicketDTO
import com.example.eventticket.utils.DataState
import kotlinx.coroutines.flow.Flow
import org.web3j.crypto.Credentials
import java.math.BigInteger

interface ResaleRepositoryInterface {
    suspend fun listTicketForSale(
        credentials: Credentials,
        ticketId: BigInteger,
        resalePrice: BigInteger
    ): Flow<DataState<String>>
    suspend fun fetchTicketsForResale(credentials: Credentials): Flow<DataState<List<ListedTicketDTO>>>
    suspend fun withdrawFromResaleList(
        credentials: Credentials,
        listingId: BigInteger
    ): Flow<DataState<String>>
    suspend fun purchaseTicketFromResale(
        credentials: Credentials,
        listingId: BigInteger,
        cost: BigInteger
    ): Flow<DataState<String>>
    suspend fun fetchTicketsListedBySender(credentials: Credentials): Flow<DataState<List<ListedTicketDTO>>>
}