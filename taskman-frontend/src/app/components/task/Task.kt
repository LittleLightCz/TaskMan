package app.components.task

import app.bean.TaskBean
import app.wrappers.axios.axios
import app.wrappers.moment.moment
import kotlinext.js.jsObject
import kotlinx.html.DIV
import kotlinx.html.js.onClickFunction
import kotlinx.html.onClick
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import kotlin.js.Promise

interface TaskState : RState {
    var updatingTask: Boolean
    var showDetails: Boolean
}

interface TaskProps : RProps {
    var task: TaskBean
    var order: Int
    var onTaskChanged: () -> Unit
}


class Task(props: TaskProps) : RComponent<TaskProps, TaskState>(props) {

    override fun TaskState.init(props: TaskProps) {
        updatingTask = false
        showDetails = false
    }

    fun getAlertClassType() = when (props.task.priority) {
        1 -> "danger"
        2 -> "warning"
        3 -> "success"
        4 -> "primary"
        else -> "light"
    }

    private fun taskUpdateAction(action: () -> Promise<Unit>) {
        setState { updatingTask = true }
        action().then {
            setState { updatingTask = false }
        }
    }

    private fun handleTaskDoneClick(event: Event) {
        taskUpdateAction {
            axios<Unit>(jsObject {
                url = "api/task/done/${props.task.id}"
                method = "post"
            }).then { props.onTaskChanged() }
        }
    }

    private fun handleDeescalateClick(event: Event) {
        taskUpdateAction {
            axios<Unit>(jsObject {
                url = "api/task/deescalate/${props.task.id}"
                method = "post"
            }).then { props.onTaskChanged() }
        }
    }

    private fun handleEscalateClick(event: Event) {
        taskUpdateAction {
            axios<Unit>(jsObject {
                url = "api/task/escalate/${props.task.id}"
                method = "post"
            }).then { props.onTaskChanged() }
        }
    }

    private fun RBuilder.renderTaskDetails() {
        if (state.showDetails) {
            +"${props.task.detail}"
        }
    }

    private fun RDOMBuilder<DIV>.renderTaskButtons() {
        if (state.updatingTask) {
            strong {
                +"Updating ... "
                i("fa fa-spinner fa-spin") {}
            }
        } else {
            button(classes = "btn btn-outline-dark mr-1") {
                attrs.onClickFunction = ::handleDeescalateClick
                i("fa fa-caret-square-o-down") {}
            }
            button(classes = "btn btn-outline-dark mr-1") {
                attrs.onClickFunction = ::handleEscalateClick
                i("fa fa-caret-square-o-up") {}
            }
            button(classes = "btn btn-outline-success") {
                attrs.onClickFunction = ::handleTaskDoneClick
                i("fa fa-check") {}
            }
        }
    }

    override fun RBuilder.render() {
        with(props.task) {
            div("alert alert-${getAlertClassType()} m-0 d-flex flex-column") {
                div("d-flex flex-row") {
                    h4("clickable m-0") {
                        attrs.onClickFunction = { setState { showDetails = !showDetails } }
                        strong { +"${props.order}." }
                        +" $name"
                    }
                    div("ml-auto") {
                        renderTaskButtons()
                    }
                }
                div("d-flex flex-row mt-1") {
                    span("ml-auto") { +"${moment(createdDate).fromNow()}" }
                }
                div("d-flex flex-row") {
                    renderTaskDetails()
                }
            }
        }
    }

}

fun RBuilder.task(task: TaskBean, order: Int, onChanged: () -> Unit) = child(Task::class) {
    attrs.task = task
    attrs.key = task.id.toString()
    attrs.order = order
    attrs.onTaskChanged = onChanged
}
