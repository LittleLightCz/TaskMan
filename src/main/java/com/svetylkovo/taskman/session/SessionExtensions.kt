package com.svetylkovo.taskman.session;

import org.hibernate.Session

fun Session.transaction(action: Session.() -> Unit) {
    synchronized(this) {
        try {
            beginTransaction()
            action(this)
            transaction.commit()
        } catch (e: Throwable) {
            e.printStackTrace()
            transaction.rollback()
            throw e
        }
    }
}