package com.taskmanager

import java.util.*

class Process(val pid: UUID, val priority: Priority) {

    fun kill() {}

    override fun toString(): String {
        return "$pid : ${priority.name}"
    }
}