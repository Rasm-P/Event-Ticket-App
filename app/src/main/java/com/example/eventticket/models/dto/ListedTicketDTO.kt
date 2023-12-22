package com.example.eventticket.models.dto

import java.math.BigInteger

class ListedTicketDTO(
    val listingId: BigInteger,
    val listingOwner: String,
    val listingPrice: BigInteger,
    val eventId: BigInteger,
    val seatNr: BigInteger,
    val eventName: String,
    val eventLocation: String,
    val eventDate: String,
    val eventTime: String,
)