package app.components.fellowTaskThumbnail

import app.bean.TaskBean
import app.bean.getAlertClassType
import app.components.task.Task
import react.RBuilder
import react.dom.div
import react.dom.h5
import react.dom.key


fun RBuilder.fellowTaskThumbnail(order: Int, task: TaskBean) {
    div("alert alert-${task.getAlertClassType()} fellow-task-thumbnail m-0 p-1") {
        attrs.key = task.id.toString()
        h5 {
            +"$order. ${task.name}"
        }
    }
}