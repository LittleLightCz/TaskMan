package com.svetylkovo.taskman.scheduled

import com.svetylkovo.taskman.entity.Task
import com.svetylkovo.taskman.session.HibernateSessionFactory.obtainHibernateSession
import com.svetylkovo.taskman.session.transaction
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit


class ScheduledMaintanenceTask(val finishedTasksDaysLifespan: Int) {

    private val session = obtainHibernateSession()

    init {
        Observable.interval(0, 4, TimeUnit.HOURS, Schedulers.io())
                .subscribe {
                    try {
                        session.transaction {
                            println("Deleting tasks older than $finishedTasksDaysLifespan days")

                            val deleteThresholdDay = DateTime().plusDays(-finishedTasksDaysLifespan)

                            val tasksToDelete = session.createCriteria(Task::class.java).list()
                                    .filterIsInstance<Task>()
                                    .filter {
                                        val completedDate = it.completedDate

                                        if (completedDate != null) deleteThresholdDay.isAfter(completedDate.time)
                                        else false
                                    }

                            tasksToDelete.forEach { delete(it) }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
    }
}
