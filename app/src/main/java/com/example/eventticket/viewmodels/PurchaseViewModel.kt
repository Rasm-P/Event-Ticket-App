package com.example.eventticket.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventticket.repositories.interfaces.TicketRepositoryInterface
import com.example.eventticket.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class PurchaseViewModel @Inject internal constructor(private val _ticketRepository: TicketRepositoryInterface): ViewModel() {
    private val _purchaseTicketsState = mutableStateOf<DataState<String>?>(null)
    val purchaseTicketsState: State<DataState<String>?> = _purchaseTicketsState

    private val _contractsApprovedState = mutableStateOf<DataState<Boolean>?>(null)
    val contractsApprovedState: State<DataState<Boolean>?> = _contractsApprovedState

    private val _setContractsApprovedState = mutableStateOf<DataState<String>?>(null)
    val setContractsApprovedState: State<DataState<String>?> = _setContractsApprovedState

    fun purchaseTickets(credentials: Credentials?, eventId: BigInteger, seats: List<Int>, cost: BigInteger) {
        if (credentials != null) {
            viewModelScope.launch {
                _ticketRepository.purchaseTickets(credentials, eventId, seats, cost).collect { response ->
                    _purchaseTicketsState.value = response
                }
            }
        }
    }

    fun fetchContractsApproved(credentials: Credentials?) {
        if (credentials != null) {
            viewModelScope.launch {
                _ticketRepository.fetchContractsApproved(credentials).collect { response ->
                    _contractsApprovedState.value = response
                }
            }
        }
    }

    fun setContractApprovals(credentials: Credentials?) {
        if (credentials != null) {
            viewModelScope.launch {
                _ticketRepository.setContractApprovals(credentials).collect { response ->
                    _setContractsApprovedState.value = response
                    if (response is DataState.Success) {
                        _contractsApprovedState.value = DataState.Success(true)
                    }
                }
            }
        }
    }

    fun resetState() {
        _purchaseTicketsState.value = null
    }
}