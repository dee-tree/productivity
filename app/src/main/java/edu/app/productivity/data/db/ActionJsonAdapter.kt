package edu.app.productivity.data.db

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import edu.app.productivity.domain.Action
import java.lang.reflect.Type
import kotlin.time.Duration.Companion.milliseconds

class ActionJsonAdapter<T : Action> : JsonSerializer<T>, JsonDeserializer<T> {
    override fun serialize(
        src: T,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement = JsonObject().apply {
        addProperty("isWork", src.isWork)
        addProperty("duration", src.duration.inWholeMilliseconds)
        if (src is Action.Work) {
            addProperty("activityName", src.activityName)
        }
    }

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext
    ): T {
        return try {
            val src = json.asJsonObject
            if (!src.has("isWork")) return Action.NotInitiatedAction as T
            val isWork = src.getAsJsonPrimitive("isWork").asBoolean
            val duration = src.getAsJsonPrimitive("duration").asLong.milliseconds

            when (isWork) {
                true -> Action.Work(duration, src.getAsJsonPrimitive("activityName").asString)
                false -> Action.Rest(duration)
            }
        } catch (e: UnsupportedOperationException) {
            Action.NotInitiatedAction
        } as T
    }
}
