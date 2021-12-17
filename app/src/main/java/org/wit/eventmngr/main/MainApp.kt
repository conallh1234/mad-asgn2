package org.wit.eventmngr.main

import android.app.Application
import org.wit.eventmngr.models.EventStore
import org.wit.eventmngr.models.EventMemStore
import org.wit.eventmngr.models.EventJSONStore
import timber.log.Timber
import timber.log.Timber.i

class MainApp : Application() {

    lateinit var events: EventStore

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        events = EventJSONStore(applicationContext)
        i("Event Manager started")
    }
}