package com.example.eventticket.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventticket.repositories.interfaces.TicketRepositoryInterface
import com.example.eventticket.repositories.interfaces.WalletRepositoryInterface
import com.example.eventticket.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import org.web3j.crypto.Credentials
import java.math.BigInteger
import javax.inject.Inject

@HiltViewModel
class WalletViewModel @Inject internal constructor(private val _walletRepository: WalletRepositoryInterface, private val  _ticketRepository: TicketRepositoryInterface): ViewModel() {
    private val _credentialsState = mutableStateOf<Credentials?>(null)
    val credentialsState: State<Credentials?> = _credentialsState

    private val _credentialsError = mutableStateOf<String?>(null)
    val credentialsError: State<String?> = _credentialsError

    private val _balanceState = mutableStateOf<DataState<BigInteger>?>(null)
    val balanceState: State<DataState<BigInteger>?> = _balanceState

    private val _accessRoleState = mutableStateOf<DataState<String>?>(null)
    val accessRoleState: State<DataState<String>?> = _accessRoleState

    fun unlockCreateWallet(password: String, fileName: String, asOrganizer: Boolean) {
        val credentials = _walletRepository.unlockCreateWallet(password, fileName)
        if (credentials is DataState.Success) {
            _credentialsState.value = credentials.data
            fetchAccountBalance()
            if (asOrganizer) {
                fetchAccessRole(credentials.data.address)
            }
        } else if (credentials is DataState.Error) {
            _credentialsError.value = credentials.error
        }
    }

    fun fetchAccountBalance() {
        val credentials = credentialsState.value
        if (credentials != null) {
            viewModelScope.launch {
                _walletRepository.fetchAccountBalance(credentials).collect { response ->
                    _balanceState.value = response
                }
            }
        }
    }

    private fun fetchAccessRole(address: String) {
        val credentials = credentialsState.value
        if (credentials != null) {
            viewModelScope.launch {
                _ticketRepository.fetchAccessRole(credentials, address).collect { response ->
                    _accessRoleState.value = response
                }
            }
        }
    }

    fun logOut() {
        _credentialsState.value = null
        _balanceState.value = null
        _credentialsError.value = null
        _accessRoleState.value = null
    }

    fun resetCredentialsState() {
        _credentialsState.value = null
        _credentialsError.value = null
    }
}