package com.example.eventticket.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventticket.models.dto.ListedTicketDTO
import com.example.eventticket.repositories.interfaces.ResaleRepositoryInterface
import com.example.eventticket.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class ResaleViewModel @Inject internal constructor(private val _resaleRepository: ResaleRepositoryInterface): ViewModel() {
    private val _resellTicketsState = mutableStateOf<DataState<String>?>(null)
    val resellTicketsState: State<DataState<String>?> = _resellTicketsState

    private val _resaleListState = mutableStateOf<DataState<List<ListedTicketDTO>>?>(null)
    val resaleListState: State<DataState<List<ListedTicketDTO>>?> = _resaleListState

    private val _ticketsListedBySenderState = mutableStateOf<DataState<List<ListedTicketDTO>>?>(null)
    val ticketsListedBySenderState: State<DataState<List<ListedTicketDTO>>?> = _ticketsListedBySenderState

    private val _withdrawFromResaleState = mutableStateOf<DataState<String>?>(null)
    val withdrawFromResaleState: State<DataState<String>?> = _withdrawFromResaleState

    private val _purchaseResaleState = mutableStateOf<DataState<String>?>(null)
    val purchaseResaleState: State<DataState<String>?> = _purchaseResaleState

    fun listTicketForSale(credentials: Credentials?, ticketId: BigInteger, resalePrice: BigInteger) {
        if (credentials != null) {
            viewModelScope.launch {
                _resaleRepository.listTicketForSale(credentials, ticketId, resalePrice).collect { response ->
                    _resellTicketsState.value = response
                }
            }
        }
    }

    fun fetchTicketsForResale(credentials: Credentials?) {
        if (credentials != null) {
            viewModelScope.launch {
                _resaleRepository.fetchTicketsForResale(credentials).collect { response ->
                    _resaleListState.value = response
                }
            }
        }
    }

    fun fetchTicketsListedBySender(credentials: Credentials?) {
        if (credentials != null) {
            viewModelScope.launch {
                _resaleRepository.fetchTicketsListedBySender(credentials).collect { response ->
                    _ticketsListedBySenderState.value = response
                }
            }
        }
    }

    fun withdrawFromResaleList(credentials: Credentials?, listingId: BigInteger) {
        if (credentials != null) {
            viewModelScope.launch {
                _resaleRepository.withdrawFromResaleList(credentials, listingId).collect { response ->
                    _withdrawFromResaleState.value = response
                }
            }
        }
    }

    fun purchaseTicketFromResale(credentials: Credentials?, listingId: BigInteger, cost: BigInteger) {
        if (credentials != null) {
            viewModelScope.launch {
                _resaleRepository.purchaseTicketFromResale(credentials, listingId, cost).collect { response ->
                    _purchaseResaleState.value = response
                }
            }
        }
    }

    fun resetListingState() {
        _resellTicketsState.value = null
    }

    fun resetWithdrawResaleState() {
        _withdrawFromResaleState.value = null
    }

    fun resetPurchaseResaleState() {
        _purchaseResaleState.value = null
    }
}