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
import javax.inject.Inject

@HiltViewModel
class EventsViewModel @Inject internal constructor(private val _ticketRepository: TicketRepositoryInterface): ViewModel() {
    private val _allEventTicketsState = mutableStateOf<DataState<List<EventVenueDTO>>?>(null)
    val allEventTicketsState: State<DataState<List<EventVenueDTO>>?> = _allEventTicketsState

    fun fetchAllEvents(credentials: Credentials?) {
        if (credentials != null) {
            viewModelScope.launch {
                _ticketRepository.fetchAllEvents(credentials).collect { response ->
                    _allEventTicketsState.value = response
                }
            }
        }
    }
}