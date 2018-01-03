package com.svetylkovo.taskman

import com.fasterxml.jackson.databind.ObjectMapper
import com.svetylkovo.taskman.entity.Task
import org.hibernate.Session
import org.hibernate.cfg.Configuration
import org.hibernate.service.ServiceRegistryBuilder
import spark.Spark.*
import java.net.InetAddress


object TaskManMain {

    private val mapper = ObjectMapper()

    @JvmStatic
    fun main(args: Array<String>) {
        obtainHibernateSession()?.let { session ->
            staticFiles.location("/public")
            port(7900)

            get("/api/hostname") { request, response ->
                InetAddress.getLocalHost().getHostName()
            }

            get("/api/tasks") { request, response ->
                val tasks = session.createCriteria(Task::class.java).list()
                mapper.writeValueAsString(tasks)
            }
        }
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