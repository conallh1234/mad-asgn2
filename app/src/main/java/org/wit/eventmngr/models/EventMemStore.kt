package org.wit.eventmngr.models

import timber.log.Timber.i

var lastId = 0L

internal fun getId(): Long {
    return lastId++
}

class EventMemStore : EventStore {

    val events = ArrayList<EventModel>()

    override fun findAll(): List<EventModel> {
        return events
    }

    override fun create(event: EventModel) {
        event.id = getId()
        events.add(event)
        logAll()
    }

    override fun update(event: EventModel) {
        val foundEvent: EventModel? = events.find { p -> p.id == event.id }
        if (foundEvent != null) {
            foundEvent.title = event.title
            foundEvent.description = event.description
            foundEvent.image = event.image
            foundEvent.lat = event.lat
            foundEvent.lng = event.lng
            foundEvent.zoom = event.zoom
            logAll()
        }
    }

    override fun delete(event: EventModel) {
        events.remove(event)
    }

    private fun logAll() {
        events.forEach { i("$it") }
    }
}
