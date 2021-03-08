package com.taskmanager

import java.util.*

class TaskManager(maxCapacity: Int) : ITaskManager {

    private val processContainer: ProcessContainer = ProcessContainer(maxCapacity)

    override fun add(process: Process, addPolicy: AddPolicy) {
        when (addPolicy) {
            AddPolicy.REJECT -> {
                processContainer.addOrReject(process)
            }
            AddPolicy.FIFO -> {
                processContainer.addFifo(process)
            }
            AddPolicy.PRIORITY_FIFO -> {
                processContainer.addPriorityFifo(process)
            }
        }
    }

    override fun listByCreationTime(): String {
        return processContainer.getAllProcessesByDate().joinToString()
    }

    override fun listByPriority(): String {
        return processContainer.getAllProcessesByPriority().joinToString()
    }

    override fun listByPid(): String {
        return processContainer.getAllProcessesByPid().joinToString()
    }

    override fun kill(pid: UUID) {
        processContainer.kill(pid)
    }

    override fun killGroup(priority: Priority) {
        processContainer.killGroup(priority)
    }

    override fun killAll() {
        processContainer.killAll()
    }
}