package com.svetylkovo.taskman.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.svetylkovo.taskman.entity.Task
import com.svetylkovo.taskman.session.HibernateSessionFactory.obtainHibernateSession
import spark.Spark.get
import spark.Spark.post
import java.net.InetAddress
import java.util.*


class MainController {

    private val session = obtainHibernateSession()
    private val mapper = ObjectMapper()

    init {
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

            session.beginTransaction()
            session.persist(task)
            session.transaction.commit()
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

        post("/api/task/delete/:id") { req, _ ->
            session.beginTransaction()
            val id = req.params(":id")
            val task = session.get(Task::class.java, id.toLong()) as Task
            session.delete(task)
            session.transaction.commit()
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
        with(session) {
            beginTransaction()
            val task = get(Task::class.java, id) as Task
            action(task)
            update(task)
            transaction.commit()
        }
    }
}