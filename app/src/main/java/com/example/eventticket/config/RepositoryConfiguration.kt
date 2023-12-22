package com.example.eventticket.config

import com.example.eventticket.BuildConfig
import java.math.BigInteger

// Built with inspiration from ArtNiche, Sergio Sánchez Sánchez: https://github.com/sergio11/art_niche_nft_marketplace/blob/main/app/src/main/java/com/dreamsoftware/artcollectibles/data/blockchain/config/BlockchainConfig.kt (Accessed: 17-10-2023)

class RepositoryConfiguration {
    val maxFeePerGas: BigInteger = BigInteger.valueOf(3500000000L)
    val maxPriorityFeePerGas: BigInteger = BigInteger.valueOf(3500000000L)
    val gasLimit: BigInteger = BigInteger.valueOf(5000000L)
    val requestAttempts = 60
    val requestSleepDuration = 2 * 1000
    val chainId: Long = BuildConfig.CHAINID
    val ticketContract: String = BuildConfig.TICKET_CONTRACT
    val resaleContract: String = BuildConfig.RESALE_CONTRACT
    val registerContract: String = BuildConfig.REGISTER_CONTRACT
    val logFileName: String = "log.txt"
}