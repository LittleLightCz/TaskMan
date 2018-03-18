package com.svetylkovo.taskman

import com.fasterxml.jackson.databind.SerializationFeature
import com.svetylkovo.taskman.controller.FellowsController.fellowsController
import com.svetylkovo.taskman.controller.TasksController.tasksController
import com.svetylkovo.taskman.scheduled.ScheduledMaintanenceTask
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.defaultResource
import io.ktor.content.resources
import io.ktor.content.static
import io.ktor.features.ContentNegotiation
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode
import io.ktor.jackson.jackson
import io.ktor.response.respond
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import org.apache.commons.lang3.exception.ExceptionUtils
import org.jboss.logging.Logger
import java.awt.Desktop
import java.net.URI


object TaskMan {

    val log: Logger = Logger.getLogger(TaskMan::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        val serverPort = 7900

        ScheduledMaintanenceTask()

        val server = embeddedServer(Jetty, serverPort) {
            install(ContentNegotiation) {
                jackson {
                    configure(SerializationFeature.INDENT_OUTPUT, true)
                }
            }

            install(StatusPages) {
                exception<Throwable> { cause ->
                    log.error("Error occured!", cause)
                    call.respond(HttpStatusCode.InternalServerError, ExceptionUtils.getStackTrace(cause))
                }
            }

            routing {
                static {
                    resources("build")
                    defaultResource("build/index.html")
                }

                tasksController()
                fellowsController()
            }
        }

        server.start(wait = false)

        launchDefaultBrowser(serverPort)
    }

    private fun launchDefaultBrowser(port: Int) {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI("http://localhost:$port"))
        }
    }

}

