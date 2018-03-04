package com.svetylkovo.taskman.entity

import java.util.*
import javax.persistence.*

@Entity
class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Long = 0
    var name: String = ""

    @Column(length = 4000)
    var detail: String? = null

    var priority: Int = 0
    var createdDate: Date? = null
    var completedDate: Date? = null
    var suspended: Boolean = false
}