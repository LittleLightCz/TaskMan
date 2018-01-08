package com.svetylkovo.taskman.entity

import java.util.*
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var name: String = ""
    var detail: String? = null
    var priority: Int = 0
    var createdDate: Date? = null
    var completedDate: Date? = null
    var suspended: Boolean = false
}