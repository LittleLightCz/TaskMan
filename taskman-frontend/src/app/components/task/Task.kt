package app.components.task

import app.bean.TaskBean
import react.*
import react.dom.div

interface TaskState: RState {
    var showDetails: Boolean
}

interface TaskProps: RProps {
    var task: TaskBean
    var order: Int
}


class Task(props: TaskProps): RComponent<TaskProps, TaskState>(props) {

    override fun TaskState.init(props: TaskProps) {
        showDetails = false
    }

    fun getAlertClassType() = when(props.task.priority) {
        1 -> "danger"
        2 -> "warning"
        3 -> "success"
        4 -> "primary"
        else -> "light"
    }

    override fun RBuilder.render() {
        with(props.task) {
            div("alert alert-${getAlertClassType()}") {
                +"${props.order} $name"
            }
        }
    }
}

fun RBuilder.task(task: TaskBean, order: Int) = child(Task::class) {
    attrs.task = task
    attrs.key = task.id.toString()
    attrs.order = order
}
