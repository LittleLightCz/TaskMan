package com.svetylkovo.taskman.controller

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.svetylkovo.taskman.entity.Task
import com.svetylkovo.taskman.session.HibernateSessionFactory.obtainHibernateSession
import com.svetylkovo.taskman.session.transaction
import org.apache.commons.lang3.exception.ExceptionUtils
import spark.Spark.*
import java.net.InetAddress
import java.util.*


class TasksController {

    private val session = obtainHibernateSession()
    private val mapper = ObjectMapper()

    init {
        exception(Exception::class.java, { exception, _, response ->
            exception.printStackTrace()

            with(response) {
                status(500)
                type("application/json")
                body(mapper.writeValueAsString(ExceptionUtils.getStackTrace(exception)))
            }
        })

        get("/api/hostname") { _, _ ->
            InetAddress.getLocalHost().hostName
        }

        get("/api/tasks") { _, _ ->
            val tasks = session.createCriteria(Task::class.java).list()
            mapper.writeValueAsString(tasks)
        }

        post("/api/tasks/new") { req, _ ->
            val task = mapper.readValue(req.body(), Task::class.java).apply {
                createdDate = Date()
            }

            session.transaction {
                persist(task)
            }
            "OK"
        }

        post("/api/tasks/update") { req, _ ->
            val task = mapper.readValue(req.body(), Task::class.java)
            updateTask(task.id) {
                it.name = task.name
                it.detail = task.detail
                it.priority = task.priority
            }
            "OK"
        }

        get("/api/tasks/export") { req, response ->
            val tasks = session.createCriteria(Task::class.java)
                .list()
                .filterIsInstance<Task>()
                .filter { it.completedDate == null }

            response.raw().apply {
                contentType = "application/octet-stream"
                setHeader("Content-Disposition", "attachment; filename=Tasks.json")

                outputStream.writer().use {
                    it.write(mapper.writeValueAsString(tasks))
                }
            }
        }

        post("/api/tasks/import") { req, _ ->
            val tasks: List<Task> = mapper.readValue(req.body(), object : TypeReference<List<Task>>() {})

            session.transaction {
                tasks.forEach {
                    it.id = 0
                    persist(it)
                }
            }

            "OK"
        }

        post("/api/task/delete/:id") { req, _ ->
            session.transaction {
                val id = req.params(":id")
                val task = get(Task::class.java, id.toLong()) as Task
                delete(task)
            }
            "OK"
        }

        /**
         * Update routes
         */

        updateTaskWhen("/api/task/done/:id") {
            it.completedDate = Date()
        }

        updateTaskWhen("/api/task/escalate/:id") { task ->
            if (task.priority > 1) task.priority--
        }

        updateTaskWhen("/api/task/deescalate/:id") { task ->
            if (task.priority < 5) task.priority++
        }

        updateTaskWhen("/api/task/suspend/:id") { it.suspended = true }

        updateTaskWhen("/api/task/unsuspend/:id") { it.suspended = false }

        updateTaskWhen("/api/task/restore/:id") {
            it.createdDate = Date()
            it.completedDate = null
        }

    }

    private fun updateTaskWhen(postPath: String, taskMutation: (Task) -> Unit) {
        post(postPath) { req, _ ->
            val id = req.params(":id")
            updateTask(id.toLong(), taskMutation)
            "OK"
        }
    }

    private fun updateTask(id: Long, action: (Task) -> Unit) {
        session.transaction {
            val task = get(Task::class.java, id) as Task
            action(task)
            update(task)
        }
    }
}