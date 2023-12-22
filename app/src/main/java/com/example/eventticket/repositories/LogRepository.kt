package com.example.eventticket.repositories

import android.content.Context
import android.util.Log
import com.example.eventticket.config.RepositoryConfiguration
import com.example.eventticket.models.LogEntry
import com.example.eventticket.repositories.interfaces.LogRepositoryInterface
import com.example.eventticket.utils.DataState
import com.google.gson.Gson
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LogRepository @Inject internal constructor(@ApplicationContext private val context: Context, private val repositoryConfiguration: RepositoryConfiguration) : LogRepositoryInterface {

    override fun readFromLogFile() = flow {
        emit(DataState.Loading)
        val file = File(context.filesDir.toString() + "/" + repositoryConfiguration.logFileName)
        val transactions = mutableListOf<LogEntry>()
        try {
            if (file.exists()) {
                file.forEachLine { line ->
                    if (line.isNotBlank()) {
                        val logEntry = Gson().fromJson(line, LogEntry::class.java)
                        transactions.add(logEntry)
                    }
                }

                emit(DataState.Success(transactions))
            } else {
                emit(DataState.Error("No log file found!"))
            }
        } catch (e: Exception) {
            Log.d("readFromLogFile",e.message.toString())
            emit(DataState.Error("Something went wrong when reading from log file!"))
        }
    }

    override fun writeToLogFile(logEntry: LogEntry) = flow {
        emit(DataState.Loading)
        val file = File(context.filesDir.toString() + "/" + repositoryConfiguration.logFileName)
        try {
            if (!file.exists()) {
                file.createNewFile()
            }
            val writer = BufferedWriter(FileWriter(file, true))
            val logString = Gson().toJson(logEntry)
            writer.write(logString)
            writer.newLine()
            writer.close()
            emit(DataState.Success(true))
        } catch (e: Exception) {
            Log.d("writeToLogFile", e.message.toString())
            emit(DataState.Error("Something went wrong when writing to log file!"))
        }
    }.flowOn(Dispatchers.IO)
}