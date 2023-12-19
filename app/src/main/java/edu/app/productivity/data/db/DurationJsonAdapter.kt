package edu.app.productivity.data.db

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

class DurationJsonAdapter : JsonSerializer<Duration>, JsonDeserializer<Duration> {
    override fun serialize(
        src: Duration,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.inWholeMilliseconds)
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): Duration {
        return try {
            val src = json.asLong
            src.milliseconds
        } catch (e: UnsupportedOperationException) {
            Duration.ZERO
        }
    }
}
