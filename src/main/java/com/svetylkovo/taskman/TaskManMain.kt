package com.svetylkovo.taskman

import spark.Spark.*
import java.net.InetAddress

object TaskManMain {

    data class Test(val name: String, val age: Int)

    @JvmStatic
    fun main(args: Array<String>) {


        staticFiles.location("/public")
        port(7900)

        get("/") { request, response ->
            "Hi"
        }

        get("/hostname") { request, response ->
            InetAddress.getLocalHost().getHostName()
        }


    }
}