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
    val daysAgo = moment(createdDate).diff(now, "days")
    return floor(daysAgo / 7).toInt()
}