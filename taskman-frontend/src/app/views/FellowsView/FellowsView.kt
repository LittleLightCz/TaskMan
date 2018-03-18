package app.views.FellowsView

import app.bean.FellowBean
import app.wrappers.axios.axios
import kotlinext.js.jsObject
import react.*
import react.dom.div
import kotlin.browser.window
import kotlin.js.Promise

interface FellowsViewState: RState {
    var fellows: Array<FellowBean>
}

class FellowsView : RComponent<RProps, FellowsViewState>() {

    private var tasksRefreshIntervalID = 0

    override fun componentDidMount() {
        fetchFellows()

        tasksRefreshIntervalID = window.setInterval({
            fetchFellows()
        }, 15000)
    }

    override fun componentWillUnmount() {
        window.clearInterval(tasksRefreshIntervalID)
    }

    private fun fetchFellows(): Promise<Unit> {
        return axios<Array<FellowBean>>(jsObject {
            url = "api/fellows"
        }).then {
            setState { fellows = it.data }
        }
    }

    override fun RBuilder.render() {
        div("fellows") {
            +"Fellas .."
        }
    }

}

fun RBuilder.fellowsView() = child(FellowsView::class) {}

