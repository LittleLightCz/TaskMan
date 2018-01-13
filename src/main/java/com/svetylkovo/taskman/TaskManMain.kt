package com.svetylkovo.taskman

import com.svetylkovo.taskman.controller.MainController
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

        MainController()

        ScheduledMaintanenceTask()

//
//        for (i in (1..100)) {
//            val task = Task().apply {
//                name = "name $i"
//                detail = "detail $i"
//                createdDate = Date()
//                completedDate = Date(System.currentTimeMillis() - (i * 1000 * 60 * 60 * 5))
//            }
//
//            session.beginTransaction()
//            session.persist(task)
//            session.transaction.commit()
//        }

        launchDefaultBrowser(serverPort)
    }

    private fun launchDefaultBrowser(port: Int) {
        if (Desktop.isDesktopSupported()) {
            Desktop.getDesktop().browse(URI("http://localhost:$port"))
        }
    }



}

