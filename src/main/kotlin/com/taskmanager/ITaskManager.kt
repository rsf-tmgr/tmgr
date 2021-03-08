package com.taskmanager

import java.util.*

interface ITaskManager {

    fun add(process: Process, addPolicy: AddPolicy = AddPolicy.REJECT)

    fun listByCreationTime(): String

    fun listByPriority(): String

    fun listByPid(): String

    fun kill(pid: UUID)

    fun killGroup(priority: Priority)

    fun killAll()
}