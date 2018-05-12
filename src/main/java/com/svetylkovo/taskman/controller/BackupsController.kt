package com.svetylkovo.taskman.controller

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.svetylkovo.taskman.config.Config.taskmanConfiguration
import com.svetylkovo.taskman.entity.Backup
import com.svetylkovo.taskman.entity.BackupFile
import com.svetylkovo.taskman.entity.Fellow
import com.svetylkovo.taskman.entity.Task
import com.svetylkovo.taskman.session.HibernateSessionFactory.obtainHibernateSession
import io.ktor.application.call
import io.ktor.response.respond
import io.ktor.routing.Routing
import io.ktor.routing.get
import io.reactivex.Observable
import java.io.File
import java.nio.file.Paths
import java.util.concurrent.TimeUnit
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


object BackupsController {

    private val session = obtainHibernateSession()
    private val mapper = jacksonObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    private val backupsDir = File(taskmanConfiguration.backupsDir).apply {
        if (!exists()) mkdir()
    }

    fun Routing.backupsController() {
        get("/api/backups/list") {
            call.respond(
                when {
                    backupsDir.isDirectory -> backupsDir.listBackupFiles().map { BackupFile(it) }
                    else -> emptyList()
                }
            )
        }
    }

    private fun createBackup() {

        println("Creating backup ...")

        val backupFileName = "Backup_${System.currentTimeMillis()}.zip"
        val backupFile = Paths.get(backupsDir.name, backupFileName).toFile()

        val backup = createBackupBean()

        ZipOutputStream(backupFile.outputStream()).use { zip ->
            val entry = ZipEntry("backup.json")
            zip.putNextEntry(entry)
            mapper.writeValue(zip, backup)
        }

        deleteOldBackups()
    }

    private fun deleteOldBackups() {
        println("Deleting old backup files")

        backupsDir.listBackupFiles()
            .sortedByDescending { it.lastModified() }
            .drop(taskmanConfiguration.keepBackupsCount)
            .forEach {
                if (it.delete()) {
                    println("${it.name} deleted")
                }
            }
    }

    private fun createBackupBean(): Backup {
        val tasks = session.createCriteria(Task::class.java).list().filterIsInstance<Task>()
        val fellows = session.createCriteria(Fellow::class.java).list().filterIsInstance<Fellow>()
        return Backup(tasks, fellows)
    }

    init {
        val period = taskmanConfiguration.backupIntervalMinutes.toLong()

        Observable.interval(period, period, TimeUnit.MINUTES)
            .subscribe {
                try {
                    createBackup()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
    }

}

private fun File.listBackupFiles() = listFiles().filter { it.isFile && it.name.endsWith(".zip") }