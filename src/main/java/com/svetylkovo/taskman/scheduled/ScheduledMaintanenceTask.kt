package com.svetylkovo.taskman.scheduled

import com.svetylkovo.taskman.entity.Task
import com.svetylkovo.taskman.session.HibernateSessionFactory.obtainHibernateSession
import com.svetylkovo.taskman.session.transaction
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.joda.time.DateTime
import java.util.concurrent.TimeUnit


class ScheduledMaintanenceTask {

    private val session = obtainHibernateSession()

    init {
        Observable.interval(0, 4, TimeUnit.HOURS, Schedulers.io())
            .subscribe {
                try {
                    session.transaction {
                        println("Deleting tasks older than 8 days")

                        val eightDaysAgo = DateTime().plusDays(-8)

                        val tasksToDelete = session.createCriteria(Task::class.java).list()
                            .filterIsInstance<Task>()
                            .filter {
                                val completedDate = it.completedDate

                                if (completedDate != null) eightDaysAgo.isAfter(completedDate.time)
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
