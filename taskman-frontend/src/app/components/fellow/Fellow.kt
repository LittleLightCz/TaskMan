package app.components.fellow

import app.bean.FellowBean
import app.bean.TaskBean
import app.components.bootstrap.spinner
import app.components.error.error
import app.components.fellowTaskThumbnail.fellowTaskThumbnail
import app.wrappers.axios.axios
import kotlinext.js.jsObject
import react.*
import react.dom.*
import kotlin.browser.window

interface FellowProps : RProps {
    var fellow: FellowBean
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

    private fun RBuilder.renderFellowTasks() {
        div("fellow-content") {
            state.fellowTasks.forEachIndexed { index, task ->
                fellowTaskThumbnail(index+1, task)
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
            h5("bg-black text-white m-0 p-2") { +props.fellow.url }

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