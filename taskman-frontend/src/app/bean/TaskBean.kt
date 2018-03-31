package app.bean

import app.wrappers.moment.moment
import kotlin.math.floor

class TaskBean {
    var id: Int = 0
    var name: String = ""
    var detail: String? = null
    var priority: Int = 3
    var createdDate: Int = 0
    var completedDate: Int? = null
    var suspended: Boolean = false
}

fun TaskBean.isCompleted() = completedDate != null
fun TaskBean.isNotCompleted() = completedDate == null

fun TaskBean.getPoosCount(): Int {
    val now = moment()
    val daysAgo = now.diff(createdDate, "days")
    return floor(daysAgo / 7).toInt()
}

fun TaskBean.getAlertClassType() = when (priority) {
    1 -> "danger"
    2 -> "warning"
    3 -> "success"
    4 -> "primary"
    else -> "light"
}