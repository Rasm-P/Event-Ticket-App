package com.example.eventticket.repositories.interfaces

import com.example.eventticket.models.LogEntry
import com.example.eventticket.utils.DataState
import kotlinx.coroutines.flow.Flow

interface LogRepositoryInterface {
    fun readFromLogFile(): Flow<DataState<List<LogEntry>>>
    fun writeToLogFile(logEntry: LogEntry): Flow<DataState<Boolean>>
}