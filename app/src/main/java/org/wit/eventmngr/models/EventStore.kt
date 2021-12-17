package org.wit.eventmngr.models

interface EventStore {
    fun findAll(): List<EventModel>
    fun create(event: EventModel)
    fun update(event: EventModel)
    fun delete(event: EventModel)
}