package app.components.fellowTaskThumbnail

import app.bean.TaskBean
import react.RBuilder
import react.dom.div
import react.dom.h5
import react.dom.key


fun RBuilder.fellowTaskThumbnail(order: Int, task: TaskBean) {
    div("fellow-task-thumbnail") {
        attrs.key = task.id.toString()
        h5 {
            +"$order. ${task.name}"
        }
    }
}