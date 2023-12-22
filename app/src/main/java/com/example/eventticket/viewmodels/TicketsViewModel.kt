package com.example.eventticket.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventticket.models.dto.NFTDataDTO
import com.example.eventticket.repositories.interfaces.TicketRepositoryInterface
import com.example.eventticket.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import javax.inject.Inject

@HiltViewModel
class TicketsViewModel @Inject internal constructor(private val _ticketRepository: TicketRepositoryInterface): ViewModel() {
    private val _senderNFTsState = mutableStateOf<DataState<List<NFTDataDTO>>?>(null)
    val senderNFTsState: State<DataState<List<NFTDataDTO>>?> = _senderNFTsState

    fun fetchSenderNFTs(credentials: Credentials?) {
        if (credentials != null) {
            viewModelScope.launch {
                _ticketRepository.fetchSenderNFTs(credentials).collect { response ->
                    _senderNFTsState.value = response
                }
            }
        }
    }
}