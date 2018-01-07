package app.bean

import kotlin.js.Date

class TaskBean {
    var id: Int = 0
    var name: String = ""
    var detail: String? = null
    var priority: Int = 3
    var createdDate: Int = 0
    var completedDate: Date? = null
    var deleted: Boolean = false
}