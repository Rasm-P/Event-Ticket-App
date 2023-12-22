package com.example.eventticket.models

import java.math.BigInteger

data class LogEntry(
    val ticketId: BigInteger,
    val dateTime: String,
    val transactionHash: String
)