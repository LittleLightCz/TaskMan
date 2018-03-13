package app.components.headermenu

import app.wrappers.axios.axios
import kotlinext.js.jsObject
import kotlinx.html.ButtonType
import kotlinx.html.InputType
import kotlinx.html.id
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import org.w3c.files.FileReader
import org.w3c.files.get
import react.RBuilder
import react.dom.*
import kotlin.browser.document
import kotlin.browser.window

fun RBuilder.headerMenu() {
    div("header-menu") {
        div("dropdown dropleft") {
            button(type = ButtonType.button, classes = "btn btn-outline-light" ) {
                attrs["data-toggle"] = "dropdown"
                i("fa fa-bars fa-lg") {}
            }
            div("dropdown-menu") {
                a(href = "#", classes = "dropdown-item") {
                    attrs.onClickFunction = ::handleImportClick
                    +"Import"
                }
                a(href = "/api/tasks/export", classes = "dropdown-item") {
                    +"Export"
                }
            }
        }

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
