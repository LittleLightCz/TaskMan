package app.components.task

import app.bean.TaskBean
import react.*
import react.dom.div

interface TaskState: RState {

}

interface TaskProps: RProps {
    var task: TaskBean
}


class Task(props: TaskProps): RComponent<TaskProps, TaskState>(props) {

    override fun TaskState.init(props: TaskProps) {

    }

    override fun RBuilder.render() {
        div("alert alert-success") {
            +"This is a task"
        }
    }
}

fun RBuilder.task(task: TaskBean, key: String = "none") = child(Task::class) {
    attrs.task = task
    attrs.key = key
}
