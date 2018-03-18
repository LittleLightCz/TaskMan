package com.svetylkovo.taskman

import io.ktor.content.defaultResource
import io.ktor.content.resources
import io.ktor.content.static
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.jetty.Jetty
import spark.Spark.staticFiles
import java.awt.Desktop
import java.net.URI


object TaskMan {

    @JvmStatic
    fun main(args: Array<String>) {
        val serverPort = 7900
        staticFiles.location("/build")

//        TasksController()
//
//        ScheduledMaintanenceTask()
//
//        launchDefaultBrowser(serverPort)

        val server = embeddedServer(Jetty, serverPort) {
            routing {
                static {
                    resources("build")
                    defaultResource("build/index.html")
                }
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

