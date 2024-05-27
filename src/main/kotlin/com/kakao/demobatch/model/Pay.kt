package com.kakao.demobatch.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Entity
data class Pay(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val amount: Long,
    val txName: String,
    val txDateTime: LocalDateTime,
) {
    constructor(amount: Long?, txName: String?, txDateTime: String?) : this(
        0,
        amount!!,
        txName!!,
        LocalDateTime.parse(txDateTime.toString(), FORMATTER),
    )

    constructor(id: Long, amount: Long, txName: String, txDateTime: String) : this(
        id,
        amount,
        txName,
        LocalDateTime.parse(txDateTime, FORMATTER),
    )

    companion object {
        private val FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
    }
}
