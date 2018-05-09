package com.svetylkovo.taskman.entity

import java.io.File


class BackupFile(val file: File) {
    val name: String get() = file.name
    val lastModified get() = file.lastModified()
}