package app.components.taskeditor

import app.bean.TaskBean
import app.components.error.error
import app.wrappers.axios.axios
import kotlinext.js.jsObject
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.onKeyPressFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import org.w3c.dom.events.KeyboardEvent
import react.*
import react.dom.*

interface TaskEditorProps : RProps {
    var task: TaskBean?
    var onClose: () -> Unit
}

interface TaskEditorState : RState {
    var error: String?
    var task: TaskBean
    var submitInProgress: Boolean
}

val prioritiesMap = mapOf(
        "Critical" to 1,
        "Important" to 2,
        "Normal" to 3,
        "Not as important" to 4,
        "Irrelevant" to 5
)

class TaskEditor(props: TaskEditorProps) : RComponent<TaskEditorProps, TaskEditorState>(props) {

    override fun TaskEditorState.init(props: TaskEditorProps) {
        task = props.task ?: TaskBean()
        submitInProgress = false
    }

    private fun existingTask() = (props.task?.id ?: 0) > 0

    private fun isSubmitNewTaskDisabled() = state.submitInProgress || state.task.name.isBlank()

    private fun handleSubmitTaskClicked() {
        if (!isSubmitNewTaskDisabled()) {
            setState { submitInProgress = true }

            val reqUrl = if (existingTask()) "api/tasks/update" else "api/tasks/new"

            axios<String>(jsObject {
                url = reqUrl
                method = "post"
                data = state.task
            })
            .then { props.onClose() }
            .catch { e: dynamic ->
                setState { error = e.response.data }
            }
        }
    }

    private fun handleEnterKeyPress(event: Event) {
        val keyboardEvent = event.unsafeCast<KeyboardEvent>()
        if (keyboardEvent.key == "Enter") {
            handleSubmitTaskClicked()
        }
    }

    private fun handlePriorityChanged(e: Event) {
        val target = e.target as HTMLSelectElement
        setState { task.priority = target.value.toInt() }
    }

    private fun handleTaskNameChanged(e: Event) {
        val target = e.target as HTMLInputElement
        setState { task.name = target.value }
    }

    private fun handleTaskDetailChanged(e: Event) {
        val target = e.target as HTMLTextAreaElement
        setState { task.detail = target.value }
    }

    private fun RBuilder.renderSubmitButton() {
        button(classes = "btn btn-success mr-1") {
            attrs {
                onClickFunction = { handleSubmitTaskClicked() }
                disabled = isSubmitNewTaskDisabled()
            }

            if (state.submitInProgress) {
                i("fa fa-spinner fa-spin") { }
                +" Submitting ..."
            } else {
                +"Submit"
            }
        }
    }

    private fun RBuilder.renderTaskNameInput() {
        input(type = InputType.text, classes = "form-control") {
            attrs {
                placeholder = "Task name"
                value = state.task.name
                autoFocus = true
                onChangeFunction = ::handleTaskNameChanged
                onKeyPressFunction = ::handleEnterKeyPress
            }
        }
    }

    override fun RBuilder.render() {
        val task = state.task

        div {
            error(state.error)

            div("form-group") {
                div("form-row") {
                    div("col") {
                        renderTaskNameInput()
                    }

                    div("col-md-3") {
                        select("form-control") {
                            attrs {
                                set("value", task.priority.toString())
                                onChangeFunction = ::handlePriorityChanged
                                onKeyPressFunction = ::handleEnterKeyPress
                            }

                            options(prioritiesMap)
                        }
                    }
                }
            }

            div("form-group") {
                textArea(classes = "form-control", rows = "5") {
                    attrs {
                        placeholder = "Details ..."
                        value = task.detail ?: ""
                        onChangeFunction = ::handleTaskDetailChanged
                    }
                }
            }

            div("form-group text-right") {
                renderSubmitButton()
                button(classes = "btn btn-light") {
                    attrs.onClickFunction = { props.onClose() }
                    +"Close"
                }
            }
        }
    }

}

private fun RBuilder.options(optionsMap: Map<String, Int>) {
    optionsMap.forEach { (name, value) ->
        option {
            attrs.value = value.toString()
            +name
        }
    }
}

fun RBuilder.taskEditor(task: TaskBean? = null, onClose: () -> Unit) = child(TaskEditor::class) {
    attrs.task = task
    attrs.onClose = onClose
}