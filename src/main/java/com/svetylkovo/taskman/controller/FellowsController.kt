package com.svetylkovo.taskman.controller

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.svetylkovo.taskman.entity.Fellow
import com.svetylkovo.taskman.entity.Task
import com.svetylkovo.taskman.session.HibernateSessionFactory.obtainHibernateSession
import com.svetylkovo.taskman.session.transaction
import io.ktor.application.call
import io.ktor.client.HttpClient
import io.ktor.client.engine.apache.Apache
import io.ktor.client.features.json.GsonSerializer
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.request.get
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post


object FellowsController {

    private val session = obtainHibernateSession()
    private val mapper = jacksonObjectMapper()

    private val client = HttpClient(Apache) {
        install(JsonFeature) {
            serializer = GsonSerializer {
                serializeNulls()
                disableHtmlEscaping()
            }
        }
    }

    fun Routing.fellowsController() {
        get("/api/fellows") {
            val fellows = session.createCriteria(Fellow::class.java).list()
            call.respond(fellows)
        }

        post("/api/fellows/unfinished") {
            val fellow = call.receive<Fellow>()
            val unfinishedTasks = client.get<List<Task>>("${fellow.url}/api/tasks/unfinished")
            call.respond(unfinishedTasks)
        }

        post("/api/fellows/add") {
            val fellow = call.receive<Fellow>()

            session.transaction {
                persist(fellow)
            }

            call.respond(HttpStatusCode.OK)
        }

        post("/api/fellows/remove") {
            val fellow = call.receive<Fellow>()

            session.transaction {
                clear()
                evict(fellow)
                delete(fellow)
            }

            call.respond(HttpStatusCode.OK)
        }

    }

}