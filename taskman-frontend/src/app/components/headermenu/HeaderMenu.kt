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
import react.RComponent
import react.RProps
import react.RState
import react.dom.*
import kotlin.browser.document
import kotlin.browser.window

class HeaderMenu : RComponent<RProps, RState>() {

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

    private fun handleImportClick(e: Event) {
        e.preventDefault()
        document.getElementById("importFile").asDynamic().click()
    }

    private fun handleShutdownClick(event: Event) {
        axios<Unit>(jsObject {
            url = "/api/shutdown"
            method = "post"
        })
        .catch {} //swallow
        .then { window.location.reload() }
    }

    override fun RBuilder.render() {
        div("header-menu") {
            div("dropdown dropleft") {
                button(type = ButtonType.button, classes = "btn btn-outline-light" ) {
                    attrs["data-toggle"] = "dropdown"
                    i("fa fa-bars fa-lg") {}
                }

                div("dropdown-menu") {
                    a(href = "#", classes = "dropdown-item") {
                        attrs.onClickFunction = ::handleImportClick
                        i("fa fa-fw fa-upload") { }
                        +" Import"
                    }

                    a(href = "/api/tasks/export", classes = "dropdown-item") {
                        i("fa fa-fw fa-download") { }
                        +" Export"
                    }

                    if (window.location.hostname == "localhost") {
                        a(href = "#", classes = "dropdown-item") {
                            attrs.onClickFunction = ::handleShutdownClick
                            i("fa fa-fw fa-power-off") { }
                            +" Shutdown"
                        }
                    }
                }
            }

            input(type = InputType.file) {
                attrs.id = "importFile"
                attrs.onChangeFunction = ::importSelectedFile
            }
        }
    }

}

fun RBuilder.headerMenu() = child(HeaderMenu::class) {}
