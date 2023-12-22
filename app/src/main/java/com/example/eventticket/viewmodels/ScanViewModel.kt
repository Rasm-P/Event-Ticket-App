package com.example.eventticket.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventticket.models.QrCodeMessage
import com.example.eventticket.repositories.interfaces.RegisterRepositoryInterface
import com.example.eventticket.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import javax.inject.Inject

@HiltViewModel
class ScanViewModel @Inject internal constructor(private val _registerRepository: RegisterRepositoryInterface): ViewModel() {
    private val _registerTicketState = mutableStateOf<DataState<String>?>(null)
    val registerTicketState: State<DataState<String>?> = _registerTicketState

    fun registerTicket(credentials: Credentials?, qrCodeMessage: QrCodeMessage) {
        if (credentials != null) {
            viewModelScope.launch {
                _registerRepository.registerTicket(credentials, qrCodeMessage).collect { response ->
                    _registerTicketState.value = response
                }
            }
        }
    }

    fun resetState() {
        _registerTicketState.value = null
    }
}