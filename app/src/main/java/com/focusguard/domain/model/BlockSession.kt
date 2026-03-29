package com.focusguard.domain.model

data class BlockSession(
    val id: Long = 0,
    val appPackage: String,
    val blockedAt: Long,
    val blockAction: String,
    val dateKey: String // yyyy-MM-dd
)
