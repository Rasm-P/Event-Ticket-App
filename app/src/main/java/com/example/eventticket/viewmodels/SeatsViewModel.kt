package com.example.eventticket.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventticket.models.dto.EventVenueDTO
import com.example.eventticket.repositories.interfaces.TicketRepositoryInterface
import com.example.eventticket.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class SeatsViewModel @Inject internal constructor(private val _ticketRepository: TicketRepositoryInterface): ViewModel() {
    private val _chosenEvent = mutableStateOf<EventVenueDTO?>(null)
    val chosenEvent: State<EventVenueDTO?> = _chosenEvent

    private val _seatsSoldForEventState = mutableStateOf<DataState<List<BigInteger>>?>(null)
    val seatsSoldForEventState: State<DataState<List<BigInteger>>?> = _seatsSoldForEventState

    private val _customerTicketsForEventState = mutableStateOf<DataState<List<BigInteger>>?>(null)
    val customerTicketsForEventState: State<DataState<List<BigInteger>>?> = _customerTicketsForEventState

    fun chooseEvent(event: EventVenueDTO) {
        _chosenEvent.value = event
    }

    fun fetchSeatsSoldForEvent(credentials: Credentials?) {
        val value = _chosenEvent.value
        if (credentials != null && value != null) {
            viewModelScope.launch {
                _ticketRepository.fetchSeatsSoldForEvent(credentials, value.id).collect { response ->
                    _seatsSoldForEventState.value = response
                }
            }
            fetchCustomerTicketsForEvent(credentials, value.id)
        }
    }

    private fun fetchCustomerTicketsForEvent(credentials: Credentials?, eventId: BigInteger) {
        if (credentials != null) {
            viewModelScope.launch {
                _ticketRepository.fetchCustomerTicketsForEvent(credentials, eventId).collect { response ->
                    _customerTicketsForEventState.value = response
                }
            }
        }
    }
}