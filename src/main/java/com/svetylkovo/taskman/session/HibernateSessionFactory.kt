package com.svetylkovo.taskman.session

import org.hibernate.Session
import org.hibernate.cfg.Configuration
import org.hibernate.service.ServiceRegistryBuilder


object HibernateSessionFactory {

    fun obtainHibernateSession(): Session {
        Class.forName("org.h2.Driver")

        val configuration = Configuration().apply {
            configure()
        }

        val serviceRegistry = ServiceRegistryBuilder().applySettings(configuration.properties).buildServiceRegistry()
        val sessionFactory = configuration.buildSessionFactory(serviceRegistry)

        return sessionFactory.openSession()
    }
}