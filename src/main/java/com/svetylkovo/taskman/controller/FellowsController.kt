package com.svetylkovo.taskman.controller

import com.svetylkovo.taskman.entity.Fellow
import com.svetylkovo.taskman.session.HibernateSessionFactory.obtainHibernateSession
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get


object FellowsController {

    private val session = obtainHibernateSession()

    fun Routing.fellowsController() {
        get("/api/fellows") {
            val fellows = session.createCriteria(Fellow::class.java).list()
            call.respond(fellows)
        }

    }

}