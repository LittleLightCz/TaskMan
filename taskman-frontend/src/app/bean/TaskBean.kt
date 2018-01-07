package app.bean

class TaskBean {
    var id: Int = 0
    var name: String = ""
    var detail: String? = null
    var priority: Int = 3
    var createdDate: Int = 0
    var completedDate: Int? = null
    var deleted: Boolean = false

}

fun TaskBean.isCompleted() = completedDate != null
fun TaskBean.isNotCompleted() = completedDate == null
