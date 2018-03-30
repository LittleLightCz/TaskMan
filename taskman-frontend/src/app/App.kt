package app

import app.components.headermenu.headerMenu
import app.components.hostname
import app.components.tasklist.tasksView
import app.views.FellowsView.fellowsView
import kotlinext.js.js
import react.RBuilder
import react.RComponent
import react.RProps
import react.RState
import react.dom.div
import react.dom.h1
import react.dom.i
import react.dom.jsStyle
import react.router.dom.RouteResultProps
import react.router.dom.browserRouter
import react.router.dom.route
import react.router.dom.switch

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
                        attrs.history = routeResult.history
                    }
                }
                route("/fellows") { routeResult: RouteResultProps<dynamic> ->
                    fellowsView {
                        attrs.history = routeResult.history
                    }
                }
            }
        }
    }
}

fun RBuilder.app() = child(App::class) {}
