package app.components.tasklist

import app.bean.TaskBean
import kotlinx.html.InputType
import kotlinx.html.js.onChangeFunction
import kotlinx.html.js.onClickFunction
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLSelectElement
import org.w3c.dom.HTMLTextAreaElement
import org.w3c.dom.events.Event
import react.*
import react.dom.*

interface AddNewTaskProps : RProps {
    var close: () -> Unit
}

interface AddNewTaskState : RState {
    var task: TaskBean
    var submitInProgress: Boolean
}

class AddNewTask(props: AddNewTaskProps) : RComponent<AddNewTaskProps, AddNewTaskState>(props) {

    override fun AddNewTaskState.init(props: AddNewTaskProps) {
        task = TaskBean()
        submitInProgress = false
    }

    private fun handleNewTaskSubmitClicked(event: Event) {
        setState { submitInProgress = true }
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

    override fun RBuilder.render() {
        val task = state.task

        div {
            h3 { +"Give that boy a task!" }

            div("form-group") {
                div("form-row") {
                    div("col") {
                        input(type = InputType.text, classes = "form-control") {
                            attrs {
                                placeholder = "Task name"
                                value = task.name
                                onChangeFunction = ::handleTaskNameChanged
                            }
                        }
                    }

                    div("col-md-3") {
                        select("form-control") {
                            attrs {
                                set("value", task.priority.toString())
                                onChangeFunction = ::handlePriorityChanged
                            }

                            options(
                                    "Critical" to 1,
                                    "Important" to 2,
                                    "Normal" to 3,
                                    "Not as important" to 4,
                                    "Irrelevant" to 5
                            )
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

            div("form-group") {
                button(classes = "btn btn-success") {
                    val submitInProgress = state.submitInProgress

                    attrs {
                        onClickFunction = ::handleNewTaskSubmitClicked
                        disabled = submitInProgress || state.task.name.isBlank()
                    }

                    if (submitInProgress) {
                        i("fa fa-spinner fa-spin") {  }
                        +" Submitting ..."
                    } else {
                        +"Submit"
                    }
                }
                button(classes = "btn btn-light") {
                    attrs.onClickFunction = { props.close() }
                    +"Close"
                }
            }
        }
    }

}

private fun RBuilder.options(vararg pairs: Pair<String, Int>) {
    pairs.forEach { (name, value) ->
        option {
            attrs.value = value.toString()
            +name
        }
    }
}

fun RBuilder.addNewTask(close: () -> Unit) = child(AddNewTask::class) {
    attrs.close = close
}