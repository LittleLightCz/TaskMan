package app

import app.components.headermenu.headerMenu
import app.components.hostname
import app.components.tasklist.taskList
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.h1
import react.dom.i

class App : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        div("App-header") {
            h1 {
                i("fa fa-user-secret") {}
                +"TaskMan"
                hostname()
            }

            headerMenu()
        }

        taskList()
    }
}

fun RBuilder.app() = child(App::class) {}
