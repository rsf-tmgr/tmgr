package com.taskmanager

enum class AddPolicy {
    REJECT,       // default
    FIFO,         // remove the oldest
    PRIORITY_FIFO // remove the oldest with lowest priority
}