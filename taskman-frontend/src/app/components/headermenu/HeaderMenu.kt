package app.components.headermenu

import app.wrappers.axios.axios
import kotlinext.js.jsObject
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.FileReader
import org.w3c.files.get
import react.RBuilder
import react.dom.a
import react.dom.div
import react.dom.input
import kotlin.browser.document
import kotlin.browser.window

fun RBuilder.headerMenu() {
    div("header-menu") {
        a("#") {
            attrs.onClickFunction = ::handleImportClick
            +"Import"
        }
        +" / "
        a("/api/tasks/export") { +"Export" }

        input(type = InputType.file) {
            attrs.id = "importFile"
            attrs.onChangeFunction = ::importSelectedFile
        }
    }
}

private fun handleImportClick(e: Event) {
    e.preventDefault()
    document.getElementById("importFile").asDynamic().click()
}

private fun importSelectedFile(e: Event) {
    val target = e.target as HTMLInputElement

    target.files?.get(0)?.let { file ->
        val reader = FileReader()

        reader.onload = {
            val fileContent = reader.result

            axios<String>(jsObject {
                url = "api/tasks/import"
                method = "post"
                data = fileContent
            })
            .then { window.location.reload() }
            .catch { window.alert("Tasks import failed!") }

            true
        }

        reader.readAsText(file)
    }
}
