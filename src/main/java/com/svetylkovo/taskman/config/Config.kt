package com.svetylkovo.taskman.config

import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File


object Config {
    private val configFileName = "TaskMan.json"
    private val mapper = jacksonObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    val taskmanConfiguration by lazy {
        val configFile = File(configFileName)

        when {
            configFile.exists() -> mapper.readValue(configFile)
            else -> TaskManConfiguration()
        }.also {
            mapper.writeValue(configFile, it)
        }
    }
}

