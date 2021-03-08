package com.taskmanager

import java.util.*

internal class ProcessContainer(val maxCapacity: Int) {

    private val processes: MutableList<Process> = mutableListOf()

    @Synchronized
    fun addOrReject(process: Process) {
        if (processes.size < maxCapacity) {
            processes.add(process)
        } else {
            throw RuntimeException("Maximum capacity reached.")
        }
    }

    @Synchronized
    fun addFifo(process: Process) {
        if (processes.size < maxCapacity) {
            processes.add(process)
        } else {
            processes.removeAt(0)
            processes.add(process)
        }
    }

    @Synchronized
    fun addPriorityFifo(process: Process) {
        if (processes.size < maxCapacity) {
            processes.add(process)
        } else {
            val lowestPriority: Priority? = getLowestPriority()
            if (lowestPriority != null && lowestPriority.compareTo(process.priority) < 0) {
                processes.removeAt(processes.indexOfFirst { it.priority == lowestPriority })
                processes.add(process)
            } else {
                throw RuntimeException("Maximum capacity reached.")
            }
        }
    }

    @Synchronized
    fun kill(pid: UUID) {
        processes.removeAt(processes.indexOfFirst { it.pid == pid }).kill()
    }

    @Synchronized
    fun killGroup(priority: Priority) {
        processes.filter { it.priority == priority }.forEach { it.kill() }
        processes.removeIf { it.priority == priority }
    }

    @Synchronized
    fun killAll() {
        processes.forEach { it.kill() }
        processes.clear()
    }

    fun getAllProcessesByDate(): List<Process> {
        return processes.toList()
    }

    fun getAllProcessesByPriority(): List<Process> {
        return processes.sortedBy { it.priority }
    }

    fun getAllProcessesByPid(): List<Process> {
        return processes.sortedBy { it.pid }
    }

    private fun getLowestPriority(): Priority? {
        return processes.map { it.priority }.sorted().firstOrNull()
    }
}
