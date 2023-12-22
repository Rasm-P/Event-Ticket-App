package com.example.eventticket.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventticket.models.LogEntry
import com.example.eventticket.repositories.interfaces.LogRepositoryInterface
import com.example.eventticket.utils.DataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogViewModel @Inject internal constructor(private val _logRepository: LogRepositoryInterface): ViewModel() {
    private val _logFileReadState = mutableStateOf<DataState<List<LogEntry>>?>(null)
    val logFileReadState: State<DataState<List<LogEntry>>?> = _logFileReadState

    private val _logFileWriteState = mutableStateOf<DataState<Boolean>?>(null)
    val logFileWriteState: State<DataState<Boolean>?> = _logFileWriteState

    fun readFromLogFile() {
        viewModelScope.launch {
            _logRepository.readFromLogFile().collect {response ->
                _logFileReadState.value = response
            }
        }
    }

    fun writeToLogFile(logEntry: LogEntry) {
        viewModelScope.launch {
            _logRepository.writeToLogFile(logEntry).collect { response ->
                _logFileWriteState.value = response
            }
        }
    }
}