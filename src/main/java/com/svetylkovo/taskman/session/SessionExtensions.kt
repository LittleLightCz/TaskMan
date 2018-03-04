package com.svetylkovo.taskman.session;

import org.hibernate.Session

fun Session.transaction(action: Session.() -> Unit) {
    try {
        beginTransaction()
        action(this)
        transaction.commit()
    } catch (e: Throwable) {
        transaction.rollback()
    }
}