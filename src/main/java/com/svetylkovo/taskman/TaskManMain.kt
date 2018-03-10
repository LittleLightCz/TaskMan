package com.svetylkovo.taskman

import com.svetylkovo.taskman.controller.TasksController
import com.svetylkovo.taskman.scheduled.ScheduledMaintanenceTask
import spark.Spark.port
import spark.Spark.staticFiles
import java.awt.Desktop
import java.net.URI


object TaskManMain {

    @JvmStatic
    fun main(args: Array<String>) {
        val serverPort = 7900
        staticFiles.location("/build")

        port(serverPort)

        TasksController()

        ScheduledMaintanenceTask()

        launchDefaultBrowser(serverPort)
    }

    private fun launchDefaultBrowser(port: Int) {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI("http://localhost:$port"))
        }
    }

}

