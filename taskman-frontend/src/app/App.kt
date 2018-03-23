package app

import app.components.headermenu.headerMenu
import app.components.hostname
import app.components.tasklist.tasksView
import app.views.FellowsView.fellowsView
import app.wrappers.routing.browserRouter
import app.wrappers.routing.route
import app.wrappers.routing.switch
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

        browserRouter {
            switch {
                route("/", exact = true) { tasksView() }
                route("/fellows") { fellowsView() }
            }
        }
    }
}

fun RBuilder.app() = child(App::class) {}
