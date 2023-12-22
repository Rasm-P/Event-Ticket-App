package com.example.eventticket.utils

import com.example.eventticket.contractWrappers.ResaleContract.ListedTicket
import com.example.eventticket.contractWrappers.TicketContract.EventVenue
import com.example.eventticket.contractWrappers.TicketContract.NFTData
import com.example.eventticket.models.dto.EventVenueDTO
import com.example.eventticket.models.dto.ListedTicketDTO
import com.example.eventticket.models.dto.NFTDataDTO

fun EventVenue.toDTO() = EventVenueDTO(
    id = id,
    eventName = eventName,
    eventDescription = eventDescription,
    numberOfTickets = numberOfTickets,
    ticketsLeft = ticketsLeft,
    eventLocation = eventLocation,
    eventDate = eventDate,
    eventTime = eventTime,
    ticketPrice = ticketPrice,
    ticketsPrCustomer = ticketsPrCustomer,
    imageUrl = imageUrl,
    venueOwner = venueOwner,
)

fun NFTData.toDTO() = NFTDataDTO(
    tokenId = tokenId,
    eventId = eventId,
    seatNr = seatNr,
    usedStatus = usedStatus,
)

fun ListedTicket.toDTO() = ListedTicketDTO(
    listingId = listingId,
    listingOwner = listingOwner,
    listingPrice = listingPrice,
    eventId = eventId,
    seatNr = seatNr,
    eventName = eventName,
    eventLocation = eventLocation,
    eventDate = eventDate,
    eventTime = eventTime
)