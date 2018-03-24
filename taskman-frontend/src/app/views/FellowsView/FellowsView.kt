package app.views.FellowsView

import app.bean.FellowBean
import app.components.bootstrap.spinner
import app.wrappers.axios.axios
import kotlinext.js.jsObject
import kotlinx.html.InputType
import kotlinx.html.js.onClickFunction
import react.*
import react.dom.div
import react.dom.h5
import react.dom.i
import react.dom.input
import kotlin.browser.window
import kotlin.js.Promise

interface FellowsViewState: RState {
    var fellows: Array<FellowBean>
    var addingFellow: Boolean
}

class FellowsView : RComponent<RProps, FellowsViewState>() {

    private var tasksRefreshIntervalID = 0

    override fun FellowsViewState.init() {
        fellows = emptyArray()
        addingFellow = false
    }

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

    private fun RBuilder.renderAddFellowBar() {
        div("add-fellow") {
            if (state.addingFellow) {
                h5("m-0") {
                    spinner()
                    +" Adding fellow ..."
                }
            } else {
                div("input-group") {
                    input(InputType.text, classes = "form-control mr-1") {
                        attrs {
                            placeholder = "http://AnotherTaskManHostName:7900"
                        }
                    }
                    div("btn btn-success") {
                        attrs.onClickFunction = { setState { addingFellow = true } }
                        i("fa fa-plus") {}
                        +" Add Fellow"
                    }
                }
            }
        }
    }

    override fun RBuilder.render() {
        div("fellows m-1") {

            renderAddFellowBar()


            +"${state.fellows.size} fellas ..."
        }
    }

}


fun RBuilder.fellowsView() = child(FellowsView::class) {}

