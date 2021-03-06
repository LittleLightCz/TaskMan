package app.components.fellow

import app.bean.FellowBean
import app.bean.TaskBean
import app.components.bootstrap.spinner
import app.components.error.error
import app.components.fellowTaskThumbnail.fellowTaskThumbnail
import app.components.tasklist.catImage
import app.extensions.getActiveTasks
import app.extensions.getSuspendedTasks
import app.wrappers.axios.axios
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import kotlinx.html.title
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import kotlin.browser.window

interface FellowProps : RProps {
    var fellow: FellowBean
    var refresh: () -> Unit
}

interface FellowState : RState {
    var error: Any?
    var loadingTasks: Boolean
    var fellowOffline: Boolean
    var fellowTasks: Array<TaskBean>
}

class Fellow : RComponent<FellowProps, FellowState>() {

    private var fellowTasksRefreshIntervalID = 0

    override fun FellowState.init() {
        loadingTasks = false
        fellowOffline = false
        fellowTasks = emptyArray()
    }

    override fun componentDidMount() {
        fetchFellowTasks(true)

        fellowTasksRefreshIntervalID = window.setInterval({
            fetchFellowTasks()
        }, 15000)
    }

    override fun componentWillUnmount() {
        window.clearInterval(fellowTasksRefreshIntervalID)
    }

    private fun fetchFellowTasks(firstLoad: Boolean = false) {

        if (firstLoad) {
            setState {
                loadingTasks = true
                fellowOffline = false
            }
        }

        axios<Array<TaskBean>>(jsObject {
            url = "api/fellows/unfinished"
            method = "post"
            data = props.fellow
        }).then {
            setState {
                fellowTasks = it.data
                loadingTasks = false
                fellowOffline = false
            }
        }.catch { errorResponse ->
            setState {
                if (errorResponse.toString() != "Error: Request failed with status code 500") {
                    error = errorResponse
                }

                fellowOffline = true
            }
        }
    }

    private fun handleRemoveFellowClick(event: Event) {
        axios<Unit>(jsObject {
            url = "api/fellows/remove"
            method = "post"
            data = props.fellow
        }).then {
            props.refresh()
        }.catch {
            setState { error = it }
        }
    }

    private fun RBuilder.renderFellowTasks() {
        val fellowTasks = state.fellowTasks

        div("fellow-content") {
            fellowTasks.getActiveTasks().forEachIndexed { index, task ->
                fellowTaskThumbnail(index+1, task)
            }

            fellowTasks.getSuspendedTasks().also {
                if (it.isNotEmpty()) {
                    img(src = catImage, classes = "m-1") {
                        attrs.width = "40px"
                    }
                }
            }.forEach {
                fellowTaskThumbnail(0, it)
            }
        }
    }

    private fun RBuilder.renderLoadingTasks() {
        centeredDiv {
            h3 {
                spinner()
                +" Loading tasks ..."
            }
        }
    }

    private fun RBuilder.renderFellowOffline() {
        centeredDiv {
            h3 {
                i("fa fa-coffee") {  }
                +" Fellow offline"
            }
        }
    }

    override fun RBuilder.render() {
        div("fellow border border-black rounded m-2") {
            div("fellow-title bg-black text-white") {
                h5("m-0") {
                    with(props.fellow) {
                        a(href = url, target = "_blank", classes = "fellow-link") {
                            attrs.title = "Open fellow's TaskMan URL in new tab"
                            +url
                        }
                    }
                }
                i("fa fa-trash m-3 clickable") {
                    attrs {
                        jsStyle {
                            marginLeft = "auto"
                        }
                        onClickFunction = ::handleRemoveFellowClick
                        title = "Remove fellow"
                    }
                }
            }

            with(state) {
                when {
                    error != null -> error(error)
                    fellowOffline -> renderFellowOffline()
                    loadingTasks -> renderLoadingTasks()
                    else -> renderFellowTasks()
                }
            }
        }
    }

}

private fun RBuilder.centeredDiv(children: RBuilder.() -> Unit) {
    div("fellow-content fellow-centered") {
        children()
    }
}


fun RBuilder.fellow(handler: RHandler<FellowProps>) = child(Fellow::class, handler)