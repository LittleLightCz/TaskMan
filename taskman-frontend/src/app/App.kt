package app

import app.components.headermenu.headerMenu
import app.components.hostname
import app.components.tasklist.tasksView
import app.views.FellowsView.fellowsView
import app.wrappers.routing.RouteResultProps
import app.wrappers.routing.browserRouter
import app.wrappers.routing.route
import app.wrappers.routing.switch
import kotlinext.js.js
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.h1
import react.dom.i
import react.dom.jsStyle

class App : RComponent<RProps, RState>() {
    override fun RBuilder.render() {
        div("App-header") {
            h1 {
                attrs.jsStyle = js {
                    paddingLeft = "42px"
                }

                i("fa fa-user-secret") {}
                +"TaskMan"
                hostname()
            }

            headerMenu()
        }

        browserRouter {
            switch {
                route("/", exact = true) { routeResult: RouteResultProps<dynamic> ->
                    tasksView {
                        attrs.history = routeResult.asDynamic().history
                    }
                }
                route("/fellows") { fellowsView() }
            }
        }
    }
}

fun RBuilder.app() = child(App::class) {}
