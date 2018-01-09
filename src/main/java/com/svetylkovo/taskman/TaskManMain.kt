package com.svetylkovo.taskman

import com.fasterxml.jackson.databind.ObjectMapper
import com.svetylkovo.taskman.entity.Task
import org.hibernate.Session
import org.hibernate.cfg.Configuration
import org.hibernate.service.ServiceRegistryBuilder
import spark.Spark.*
import java.awt.Desktop
import java.net.InetAddress
import java.net.URI
import java.util.*


object TaskManMain {

    private val mapper = ObjectMapper()

    @JvmStatic
    fun main(args: Array<String>) {
        obtainHibernateSession()?.let { session ->
            val serverPort = 7900
            staticFiles.location("/build")

            port(serverPort)

            get("/api/hostname") { _, _ ->
                InetAddress.getLocalHost().hostName
            }

            get("/api/tasks") { _, _ ->
                val tasks = session.createCriteria(Task::class.java).list()
                mapper.writeValueAsString(tasks)
            }

            post("/api/tasks/new") { request, _ ->
                with(request) {
                    val task = mapper.readValue(body(), Task::class.java).apply {
                        createdDate = Date()
                    }

                    session.beginTransaction()
                    session.persist(task)
                    session.transaction.commit()
                }

                "OK"
            }

            /**
             * Update routes
             */

            fun updateTaskWhen(postPath: String, taskMutation: (Task) -> Unit) {
                post(postPath) { req, _ ->
                    val id = req.params(":id")
                    session.updateTask(id.toLong(), taskMutation)
                    "OK"
                }
            }

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

            //Launch browser
            launchDefaultBrowser(serverPort)
        }
    }

    private fun launchDefaultBrowser(port: Int) {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI("http://localhost:$port"))
        }
    }

    private fun Session.updateTask(id: Long, action: (Task) -> Unit) {
        beginTransaction()
        val task = get(Task::class.java, id) as Task
        action(task)
        update(task)
        transaction.commit()
    }

    private fun obtainHibernateSession(): Session? {
        Class.forName("org.h2.Driver")

        val configuration = Configuration().apply {
            configure()
        }

        val serviceRegistry = ServiceRegistryBuilder().applySettings(configuration.properties).buildServiceRegistry()
        val sessionFactory = configuration.buildSessionFactory(serviceRegistry)

        return sessionFactory.openSession()
    }
}