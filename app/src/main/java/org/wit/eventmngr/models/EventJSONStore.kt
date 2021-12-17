package org.wit.eventmngr.models

import android.content.Context
import android.net.Uri
import com.google.gson.*
import com.google.gson.reflect.TypeToken
import org.wit.eventmngr.helpers.*
import timber.log.Timber
import java.lang.reflect.Type
import java.util.*

const val JSON_FILE = "events.json"
val gsonBuilder: Gson = GsonBuilder().setPrettyPrinting()
    .registerTypeAdapter(Uri::class.java, UriParser())
    .create()
val listType: Type = object : TypeToken<ArrayList<EventModel>>() {}.type

fun generateRandomId(): Long {
    return Random().nextLong()
}

class EventJSONStore(private val context: Context) : EventStore {

    var events = mutableListOf<EventModel>()

    init {
        if (exists(context, JSON_FILE)) {
            deserialize()
        }
    }

    override fun findAll(): MutableList<EventModel> {
        logAll()
        return events
    }

    override fun create(event: EventModel) {
        event.id = generateRandomId()
        events.add(event)
        serialize()
    }


    override fun update(event: EventModel) {
        val eventsList = findAll() as ArrayList<EventModel>
        var foundEvent: EventModel? = eventsList.find { e -> e.id == event.id }
        if (foundEvent != null) {
            foundEvent.title = event.title
            foundEvent.description = event.description
            foundEvent.image = event.image
            foundEvent.lat = event.lat
            foundEvent.lng = event.lng
            foundEvent.zoom = event.zoom
        }
        serialize()
    }

    override fun delete(event: EventModel) {
        events.remove(event)
        serialize()
    }

    private fun serialize() {
        val jsonString = gsonBuilder.toJson(events, listType)
        write(context, JSON_FILE, jsonString)
    }

    private fun deserialize() {
        val jsonString = read(context, JSON_FILE)
        events = gsonBuilder.fromJson(jsonString, listType)
    }

    private fun logAll() {
        events.forEach { Timber.i("$it") }
    }
}

class UriParser : JsonDeserializer<Uri>,JsonSerializer<Uri> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Uri {
        return Uri.parse(json?.asString)
    }

    override fun serialize(
        src: Uri?,
        typeOfSrc: Type?,
        context: JsonSerializationContext?
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }
}