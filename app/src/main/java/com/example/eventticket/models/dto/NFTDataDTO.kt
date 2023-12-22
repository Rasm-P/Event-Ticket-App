package com.example.eventticket.models.dto

import java.math.BigInteger

data class NFTDataDTO(
    val tokenId: BigInteger,
    val eventId: BigInteger,
    val seatNr: BigInteger,
    val usedStatus: Boolean,
)