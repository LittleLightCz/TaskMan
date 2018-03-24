package com.svetylkovo.taskman.controller

import com.svetylkovo.taskman.entity.Fellow
import com.svetylkovo.taskman.entity.Task
import com.svetylkovo.taskman.session.HibernateSessionFactory.obtainHibernateSession
import com.svetylkovo.taskman.session.transaction
import io.ktor.application.call
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.ktor.routing.post
import java.util.*


object FellowsController {

    private val session = obtainHibernateSession()

    fun Routing.fellowsController() {
        get("/api/fellows") {
            val fellows = session.createCriteria(Fellow::class.java).list()
            call.respond(fellows)
        }

        post("/api/fellows/add") {
            val fellow = call.receive<Fellow>()

            session.transaction {
                persist(fellow)
            }

            call.respond(HttpStatusCode.OK)
        }

    }

}