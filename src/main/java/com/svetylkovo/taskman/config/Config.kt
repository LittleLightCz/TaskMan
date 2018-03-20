package com.svetylkovo.taskman.config

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import java.io.File


object Config {
    private val configFileName = "TaskMan.json"
    private val mapper = ObjectMapper().apply {
        enable(SerializationFeature.INDENT_OUTPUT)
    }

    val taskmanConfiguration by lazy {
        val configFile = File(configFileName)

        if (configFile.exists()) mapper.readValue(configFile, object : TypeReference<TaskManConfiguration>() {})
        else TaskManConfiguration().also {
            mapper.writeValue(configFile, it)
        }
    }
}

