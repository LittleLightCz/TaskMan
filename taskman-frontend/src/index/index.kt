package index

import app.*
import kotlinext.js.*
import react.dom.*
import kotlin.browser.*

fun main(args: Array<String>) {
    require("src/lib/toastify/toastify.css")
    require("src/lib/toastify/toastify.js")
    require("bootstrap/dist/css/bootstrap.css")
    require("font-awesome/css/font-awesome.css")
    requireAll(require.context("src", true, js("/\\.css$/")))

    render(document.getElementById("root")) {
        app()
    }
}
