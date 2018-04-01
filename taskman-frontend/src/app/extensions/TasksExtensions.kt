package app.extensions

import app.bean.TaskBean
import app.bean.isCompleted
import app.bean.isNotCompleted

fun Array<TaskBean>.getActiveTasks() = filter { !it.suspended && it.isNotCompleted() }
    .sortedWith(
        compareBy({ it.priority }, { it.createdDate })
    )

fun Array<TaskBean>.getFinishedTasks() = filter { it.isCompleted() }
    .sortedByDescending { it.completedDate }

fun Array<TaskBean>.getSuspendedTasks() = filter { it.suspended }
    .sortedByDescending { it.createdDate }