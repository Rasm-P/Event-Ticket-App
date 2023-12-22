package com.example.eventticket.models.dto

import java.math.BigInteger

data class EventVenueDTO(
    val id: BigInteger,
    val eventName: String,
    val eventDescription: String,
    val numberOfTickets: BigInteger,
    val ticketsLeft: BigInteger,
    val eventLocation: String,
    val eventDate: String,
    val eventTime: String,
    val ticketPrice: BigInteger,
    val ticketsPrCustomer: BigInteger,
    val imageUrl: String,
    val venueOwner: String,
)