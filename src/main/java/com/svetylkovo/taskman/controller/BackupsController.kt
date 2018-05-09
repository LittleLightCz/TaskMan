package com.svetylkovo.taskman.controller

import com.svetylkovo.taskman.config.Config.taskmanConfiguration
import com.svetylkovo.taskman.entity.BackupFile
import com.svetylkovo.taskman.session.HibernateSessionFactory.obtainHibernateSession
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import java.io.File


object BackupsController {

    private val session = obtainHibernateSession()

    fun Routing.backupsController() {
        get("/api/backups/list") {
            val backupsDir = File(taskmanConfiguration.backupsDir).apply {
                if (!exists()) mkdir()
            }

            call.respond(
                when {
                    backupsDir.isDirectory -> backupsDir.listFiles()
                        .filter { it.isFile && it.name.endsWith(".zip") }
                        .map { BackupFile(it) }
                    else -> emptyList()
                }
            )
        }

    }
}