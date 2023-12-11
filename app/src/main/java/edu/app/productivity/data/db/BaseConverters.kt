package edu.app.productivity.data.db

import androidx.room.TypeConverter
import com.google.gson.Gson
import edu.app.productivity.domain.Action
import java.util.Date

interface BaseConverters {

    object DateConverter {
        @TypeConverter
        @JvmStatic
        fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

        @TypeConverter
        @JvmStatic
        fun dateToTimestamp(date: Date?): Long? = date?.time
    }

    object ActionConverter {

        @TypeConverter
        @JvmStatic
        fun fromString(actionStr: String) = Gson().fromJson(actionStr, Action::class.java)

        @TypeConverter
        @JvmStatic
        fun actionToGson(action: Action) = Gson().toJson(action)
    }

    object ActionListConverter {

        @TypeConverter
        @JvmStatic
        fun fromString(actionsStr: String) =
            Gson().fromJson(actionsStr, Array<Action>::class.java).toList()

        @TypeConverter
        @JvmStatic
        fun actionToGson(actions: List<Action>) = Gson().toJson(actions)
    }

}
