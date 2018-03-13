package index

import app.app
import kotlinext.js.invoke
import kotlinext.js.require
import kotlinext.js.requireAll
import react.dom.render
import kotlin.browser.document

fun main(args: Array<String>) {
    require("src/lib/toastify/toastify.css")
    require("src/lib/toastify/toastify.js")
    require("bootstrap/dist/css/bootstrap.css")
    require("bootstrap/dist/js/bootstrap.bundle.js")
    require("font-awesome/css/font-awesome.css")
    requireAll(require.context("src", true, js("/\\.css$/")))

    render(document.getElementById("root")) {
        app()
    }
}
