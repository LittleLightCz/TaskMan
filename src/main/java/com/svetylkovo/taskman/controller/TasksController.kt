package com.svetylkovo.taskman.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.svetylkovo.taskman.entity.Task
import com.svetylkovo.taskman.session.HibernateSessionFactory.obtainHibernateSession
import com.svetylkovo.taskman.session.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode.Companion.OK
import io.ktor.request.receive
import io.ktor.request.receiveText
import io.ktor.response.header
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import java.net.InetAddress
import java.util.*


object TasksController {

    private val session = obtainHibernateSession()

    fun Routing.tasksController() {
        get("/api/hostname") {
            call.respond(InetAddress.getLocalHost().hostName)
        }

        get("/api/tasks") {
            val tasks = session.createCriteria(Task::class.java).list()
            call.respond(tasks)
        }

        get("/api/tasks/unfinished") {
            val tasks = session.createCriteria(Task::class.java)
                .list()
                .filterIsInstance<Task>()
                .filter { it.completedDate == null }

            call.respond(tasks)
        }

        post("/api/tasks/new") {
            val task = call.receive<Task>().apply {
                createdDate = Date()
            }

            session.transaction {
                persist(task)
            }

            call.respond(OK)
        }

        post("/api/tasks/update") {
            val task = call.receive<Task>()
            updateTask(task.id) {
                it.name = task.name
                it.detail = task.detail
                it.priority = task.priority
            }
            call.respond(OK)
        }

        get("/api/tasks/export") {
            val tasks = session.createCriteria(Task::class.java)
                .list()
                .filterIsInstance<Task>()
                .filter { it.completedDate == null }

            with(call) {
                response.header("Content-Disposition", "attachment; filename=Tasks.json")
                respond(tasks)
            }
        }

        post("/api/tasks/import") {
            val json = call.receiveText()
            val tasks = jacksonObjectMapper().readValue<List<Task>>(json)

            session.transaction {
                tasks.forEach {
                    it.id = 0
                    persist(it)
                }
            }

            call.respond(OK)
        }

        post("/api/task/delete/{id}") {
            session.transaction {
                call.parameters["id"]?.let { id ->
                    val task = get(Task::class.java, id.toLong()) as Task
                    delete(task)
                }
            }

            call.respond(OK)
        }

        updateTaskWhen("/api/task/done/{id}") {
            it.completedDate = Date()
        }

        updateTaskWhen("/api/task/escalate/{id}") { task ->
            if (task.priority > 1) task.priority--
        }

        updateTaskWhen("/api/task/deescalate/{id}") { task ->
            if (task.priority < 5) task.priority++
        }

        updateTaskWhen("/api/task/suspend/{id}") { it.suspended = true }

        updateTaskWhen("/api/task/unsuspend/{id}") { it.suspended = false }

        updateTaskWhen("/api/task/restore/{id}") {
            it.createdDate = Date()
            it.completedDate = null
        }

    }

    private fun updateTask(id: Long, action: (Task) -> Unit) {
        session.transaction {
            val task = get(Task::class.java, id) as Task
            action(task)
            update(task)
        }
    }

    fun Routing.updateTaskWhen(postPath: String, taskMutation: (Task) -> Unit) {
        post(postPath) {
            call.parameters["id"]?.let { id -> updateTask(id.toLong(), taskMutation) }
            call.respond(OK)
        }
    }

}