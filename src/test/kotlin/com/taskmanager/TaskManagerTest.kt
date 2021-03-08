package com.taskmanager

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

internal class TaskManagerTest {

    @Test
    fun `rejects addition of a new process in default mode`() {
        val taskManager = TaskManager(2)
        taskManager.add(Process(UUID.randomUUID(), Priority.LOW))
        taskManager.add(Process(UUID.randomUUID(), Priority.HIGH))
        assertThrows(
                RuntimeException::class.java,
                { taskManager.add(Process(UUID.randomUUID(), Priority.HIGH)) },
                "Maximum capacity reached."
        )
    }

    @Test
    fun `oldest process is replaced in FIFO mode`() {
        val taskManager = TaskManager(2)
        taskManager.add(Process(UUID.randomUUID(), Priority.LOW), AddPolicy.FIFO)
        taskManager.add(Process(UUID.randomUUID(), Priority.HIGH), AddPolicy.FIFO)
        taskManager.add(Process(UUID.randomUUID(), Priority.MEDIUM), AddPolicy.FIFO)
        val processes = extractProcesses(taskManager.listByPriority())
        assertNotNull(processes)
        assertEquals(2, processes.size)
        assertEquals(extractPriority(processes[0]), Priority.MEDIUM.name)
        assertEquals(extractPriority(processes[1]), Priority.HIGH.name)
    }

    @Test
    fun `oldest lowest priority process is replaced in PRIORITY_FIFO mode`() {
        val taskManager = TaskManager(2)
        taskManager.add(Process(UUID.randomUUID(), Priority.LOW), AddPolicy.PRIORITY_FIFO)
        taskManager.add(Process(UUID.randomUUID(), Priority.HIGH), AddPolicy.PRIORITY_FIFO)
        taskManager.add(Process(UUID.randomUUID(), Priority.MEDIUM), AddPolicy.PRIORITY_FIFO)
        val processes = extractProcesses(taskManager.listByPriority())
        assertNotNull(processes)
        assertEquals(2, processes.size)
        assertEquals(extractPriority(processes[0]), Priority.MEDIUM.name)
        assertEquals(extractPriority(processes[1]), Priority.HIGH.name)
    }

    @Test
    fun `lower priority process is rejected in PRIORITY_FIFO mode`() {
        val taskManager = TaskManager(2)
        taskManager.add(Process(UUID.randomUUID(), Priority.MEDIUM), AddPolicy.PRIORITY_FIFO)
        taskManager.add(Process(UUID.randomUUID(), Priority.HIGH), AddPolicy.PRIORITY_FIFO)
        assertThrows(
                RuntimeException::class.java,
                { taskManager.add(Process(UUID.randomUUID(), Priority.LOW), AddPolicy.PRIORITY_FIFO) },
                "Maximum capacity reached."
        )
    }

    @Test
    fun `lists processes by creation time`() {
        val processes = extractProcesses(createProcesses().listByCreationTime())
        assertNotNull(processes)
        assertEquals(3, processes.size)
        assertEquals(extractPriority(processes[0]), Priority.LOW.name)
        assertEquals(extractPriority(processes[1]), Priority.HIGH.name)
        assertEquals(extractPriority(processes[2]), Priority.MEDIUM.name)
    }

    @Test
    fun `lists processes by priority`() {
        val processes = extractProcesses(createProcesses().listByPriority())
        assertNotNull(processes)
        assertEquals(3, processes.size)
        assertEquals(extractPriority(processes[0]), Priority.LOW.name)
        assertEquals(extractPriority(processes[1]), Priority.MEDIUM.name)
        assertEquals(extractPriority(processes[2]), Priority.HIGH.name)
    }

    @Test
    fun `individual process is killed`() {
        val taskManager = createProcesses()
        var processes = extractProcesses(taskManager.listByPriority())
        taskManager.kill(extractPid(processes[0]))
        processes = extractProcesses(taskManager.listByPriority())
        assertEquals(2, processes.size)
        assertEquals(extractPriority(processes[0]), Priority.MEDIUM.name)
        assertEquals(extractPriority(processes[1]), Priority.HIGH.name)
    }

    @Test
    fun `group of processes is killed`() {
        val taskManager = createProcesses()
        taskManager.add(Process(UUID.randomUUID(), Priority.MEDIUM))
        taskManager.killGroup(Priority.MEDIUM)
        val processes = extractProcesses(taskManager.listByPriority())
        assertEquals(2, processes.size)
        assertEquals(extractPriority(processes[0]), Priority.LOW.name)
        assertEquals(extractPriority(processes[1]), Priority.HIGH.name)
    }

    @Test
    fun `all processes are killed`() {
        val taskManager = createProcesses()
        taskManager.killAll()
        assertEquals(0, extractProcesses(taskManager.listByPriority()).size)
    }

    private fun extractProcesses(processListing: String): List<String> {
        return processListing.split(",").filter { it.isNotBlank() }
    }

    private fun extractPid(processEntry: String): UUID {
        return UUID.fromString(processEntry.split(" :")[0])
    }

    private fun extractPriority(processEntry: String): String {
        return processEntry.split(" : ")[1]
    }

    private fun createProcesses(): ITaskManager {
        val taskManager = TaskManager(10)
        taskManager.add(Process(UUID.randomUUID(), Priority.LOW))
        taskManager.add(Process(UUID.randomUUID(), Priority.HIGH))
        taskManager.add(Process(UUID.randomUUID(), Priority.MEDIUM))
        return taskManager
    }
}