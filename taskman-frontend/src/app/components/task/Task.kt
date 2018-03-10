package app.components.task

import app.bean.TaskBean
import app.bean.getPoosCount
import app.bean.isCompleted
import app.components.error.error
import app.components.poos.poos
import app.components.taskeditor.prioritiesMap
import app.components.taskeditor.taskEditor
import app.wrappers.axios.axios
import app.wrappers.moment.moment
import kotlinext.js.jsObject
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onContextMenuFunction
import kotlinx.html.title
import org.w3c.dom.events.Event
import react.*
import react.dom.*
import kotlin.js.Promise

@JsModule("src/images/poo.svg")
external val pooImage: dynamic

interface TaskState : RState {
    var error: String?
    var showDetails: Boolean
    var showEditor: Boolean
    var updatingTask: Boolean
}

interface TaskProps : RProps {
    var task: TaskBean
    var order: Int
    var onTaskChanged: () -> Unit
}

class Task(props: TaskProps) : RComponent<TaskProps, TaskState>(props) {

    override fun TaskState.init(props: TaskProps) {
        showDetails = false
        showEditor = false
        updatingTask = false
    }

    private fun getPriorityName() = prioritiesMap.entries
            .find { it.value == props.task.priority }
            ?.key ?: "Unknown"

    private fun getAlertClassType() = when (props.task.priority) {
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
        .catch { e: dynamic ->
            setState { error = e.response.data }
        }
    }

    private fun handleRightMouseClick(event: Event) {
        event.preventDefault()
        setState { showEditor = true }
    }

    private fun handleTaskDoneClick(event: Event) {
        taskUpdateAction {
            axios<Unit>(jsObject {
                url = "api/task/done/${props.task.id}"
                method = "post"
            }).then { props.onTaskChanged() }
        }
    }

    private fun handleSuspendTaskClick(event: Event) {
        taskUpdateAction {
            axios<Unit>(jsObject {
                url = "api/task/suspend/${props.task.id}"
                method = "post"
            }).then { props.onTaskChanged() }
        }
    }

    private fun handleUnsuspendTaskClick(event: Event) {
        taskUpdateAction {
            axios<Unit>(jsObject {
                url = "api/task/unsuspend/${props.task.id}"
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

    private fun handleRestoreTaskClick(event: Event) {
        taskUpdateAction {
            axios<Unit>(jsObject {
                url = "api/task/restore/${props.task.id}"
                method = "post"
            }).then { props.onTaskChanged() }
        }
    }

    private fun handleDeleteTaskClick(event: Event) {
        taskUpdateAction {
            axios<Unit>(jsObject {
                url = "api/task/delete/${props.task.id}"
                method = "post"
            }).then { props.onTaskChanged() }
        }
    }

    private fun RBuilder.renderTaskDetails() {
        if (state.showDetails) {
            div("text-left") {
                hr { }
                if (props.task.detail == null) {
                    i { +"No details were specified." }
                } else {
                    h5 { +"Details:" }
                    div("task-details") {
                        +"${props.task.detail}"
                    }
                }
            }
            div("text-right") {
                button(classes = "btn btn-secondary") {
                    attrs.onClickFunction = { setState { showEditor = true } }
                    i("fa fa-pencil-square-o") {}
                    +" Edit"
                }
            }
        }
    }

    private fun RBuilder.renderTaskButtons() {
        val task = props.task

        when {
            state.updatingTask -> strong {
                +"Updating ... "
                i("fa fa-spinner fa-spin") {}
            }
            task.isCompleted() -> {
                renderRestoreButton()
                renderDeleteButton()
            }
            task.suspended -> {
                renderUnsuspendButton()
                renderDeleteButton()
            }
            else -> {
                renderDeescalateButton()
                renderEscalateButton()
                renderSuspendButton()
                renderFinishTaskButton()
            }
        }
    }

    private fun RBuilder.renderSuspendButton() {
        button(classes = "btn btn-outline-primary btn-sm mr-1") {
            attrs.title = "Suspend task"
            attrs.onClickFunction = ::handleSuspendTaskClick
            i("fa fa-power-off") {}
        }
    }

    private fun RBuilder.renderUnsuspendButton() {
        button(classes = "btn btn-outline-success btn-sm mr-1") {
            attrs.title = "Unsuspend task"
            attrs.onClickFunction = ::handleUnsuspendTaskClick
            i("fa fa-power-off") {}
        }
    }

    private fun RBuilder.renderFinishTaskButton() {
        button(classes = "btn btn-outline-success btn-sm") {
            attrs.title = "Mark as finished!"
            attrs.onClickFunction = ::handleTaskDoneClick
            i("fa fa-check") {}
        }
    }

    private fun RBuilder.renderEscalateButton() {
        button(classes = "btn btn-outline-dark btn-sm mr-1") {
            attrs.title = "Escalate"
            attrs.onClickFunction = ::handleEscalateClick
            i("fa fa-caret-square-o-up") {}
        }
    }

    private fun RBuilder.renderDeescalateButton() {
        button(classes = "btn btn-outline-dark btn-sm mr-1") {
            attrs.title = "Deescalate"
            attrs.onClickFunction = ::handleDeescalateClick
            i("fa fa-caret-square-o-down") {}
        }
    }

    private fun RBuilder.renderDeleteButton() {
        button(classes = "btn btn-outline-danger btn-sm") {
            attrs.title = "Delete permanently"
            attrs.onClickFunction = ::handleDeleteTaskClick
            i("fa fa-trash") {}
        }
    }

    private fun RBuilder.renderRestoreButton() {
        button(classes = "btn btn-outline-dark btn-sm mr-1") {
            attrs.title = "Restore"
            attrs.onClickFunction = ::handleRestoreTaskClick
            i("fa fa-undo") {}
        }
    }

    private fun RBuilder.renderTimeAgo() {
        val task = props.task
        val timestamp = if (task.isCompleted()) task.completedDate else task.createdDate
        span("ml-auto pt-1") { +"${moment(timestamp).fromNow()}" }
    }

    private fun RBuilder.renderPriorityBadge() {
        span("badge badge-${getAlertClassType()} p-2") {
            +getPriorityName()
        }
    }

    override fun RBuilder.render() {
        when {
            state.showEditor -> div("mt-2 mb-2") {
                taskEditor(props.task) {
                    setState { showEditor = false }
                    props.onTaskChanged()
                }
            }
            else -> with(props.task) {
                error(state.error)

                div("alert alert-${getAlertClassType()} m-0 d-flex flex-column") {
                    div("d-flex flex-row text-left") {
                        h4("clickable m-0") {
                            attrs.onClickFunction = { setState { showDetails = !showDetails } }
                            attrs.onContextMenuFunction = ::handleRightMouseClick

                            if (!suspended && !isCompleted()) {
                                strong { +"${props.order}. " }
                            }

                            +name
                        }
                        div("ml-auto flex-no-shrink") {
                            renderTaskButtons()
                        }
                    }
                    div("d-flex flex-row mt-1") {
                        renderPriorityBadge()
                        renderTimeAgo()
                    }

                    renderTaskDetails()

                    poos(props.task.getPoosCount())
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
