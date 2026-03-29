package com.focusguard.domain.model

enum class BlockMode {
    BLOCK_ALL,  // immediately block every detected short video
    CURIOUS,    // allow N videos then block
    PAUSED      // blocking paused for X minutes
}
