package com.example.eventticket.models

import com.example.eventticket.models.dto.EventVenueDTO
import com.example.eventticket.models.dto.NFTDataDTO

data class ResaleTicketData(
    val event: EventVenueDTO,
    val nftData: NFTDataDTO
)