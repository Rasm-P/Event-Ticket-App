package com.example.eventticket.models

import java.math.BigInteger

data class QrCodeMessage(
    val e: BigInteger,
    val t: BigInteger,
    val c: String,
    val h: ByteArray,
    val r: ByteArray,
    val s: ByteArray,
    val v: BigInteger
)