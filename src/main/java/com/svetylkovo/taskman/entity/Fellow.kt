package com.svetylkovo.taskman.entity

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Fellow {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id = 0L

    var name = ""
    var url = ""
    var tasks = emptyList<Task>()
}