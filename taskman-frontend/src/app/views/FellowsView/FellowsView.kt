package app.views.FellowsView

import app.bean.FellowBean
import app.components.bootstrap.spinner
import app.components.error.error
import app.components.fellow.fellow
import app.wrappers.axios.axios
import kotlinext.js.jsObject
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import react.router.dom.RouteResultHistory

interface FellowsViewProps : RProps {
    var history: RouteResultHistory
}

interface FellowsViewState: RState {
    var fellows: Array<FellowBean>
    var addingFellow: Boolean
    var error: Any?
    var addFellowUrl: String
}

class FellowsView : RComponent<FellowsViewProps, FellowsViewState>() {

    override fun FellowsViewState.init() {
        fellows = emptyArray()
        addingFellow = false
        addFellowUrl = ""
    }

    override fun componentDidMount() {
        fetchFellows()
    }

    private fun handleAddFellowUrlChanged(e: Event) {
        val target = e.target as HTMLInputElement
        setState { addFellowUrl = target.value }
    }

    private fun handleAddFellowClicked(event: Event) {
        setState { addingFellow = true }

        axios<Array<FellowBean>>(jsObject {
            url = "api/fellows/add"
            method = "post"
            data = FellowBean(state.addFellowUrl)
        }).catch {
            setState { error = it }
        }
        .then {
            setState {
                addingFellow = false
                addFellowUrl = ""
            }
        }
        .then { fetchFellows() }
    }

    private fun fetchFellows() {
        axios<Array<FellowBean>>(jsObject {
            url = "api/fellows"
        }).then {
            setState { fellows = it.data }
        }.catch {
            setState { error = it }
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
                    renderAddFellowUrlInput()
                    renderAddFellowButton()
                    renderBackButton()
                }
            }
        }
    }

    private fun RBuilder.renderBackButton() {
        div("btn btn-dark ml-1") {
            attrs.onClickFunction = { props.history.push("/") }
            i("fa fa-reply") {}
            +" Back"
        }
    }

    private fun RBuilder.renderAddFellowButton() {
        button (classes = "btn btn-success") {
            attrs {
                disabled = state.addFellowUrl.isBlank()
                onClickFunction = ::handleAddFellowClicked
            }
            i("fa fa-plus") {}
            +" Add Fellow"
        }
    }

    private fun RBuilder.renderAddFellowUrlInput() {
        input(InputType.text, classes = "form-control mr-1") {
            attrs {
                placeholder = "http://AnotherTaskManHostName:7900"
                value = state.addFellowUrl
                onChangeFunction = ::handleAddFellowUrlChanged
            }
        }
    }

    override fun RBuilder.render() {
        error(state.error)

        div("fellows-view m-1") {

            h2 { +"Fellows" }

            renderAddFellowBar()

            div("fellows") {
                state.fellows.forEach {
                    fellow {
                        attrs {
                            key = it.id.toString()
                            fellow = it
                            refresh = { fetchFellows() }
                        }
                    }
                }
            }
        }
    }

}


fun RBuilder.fellowsView(handler: RHandler<FellowsViewProps>) = child(FellowsView::class, handler)

