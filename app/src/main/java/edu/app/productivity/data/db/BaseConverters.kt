package edu.app.productivity.data.db

import androidx.room.TypeConverter
import edu.app.productivity.di.AppModule
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
        fun fromString(actionStr: String) =
            AppModule.provideGson().fromJson(actionStr, Action::class.java)

        @TypeConverter
        @JvmStatic
        fun actionToGson(action: Action) = AppModule.provideGson().toJson(action)
    }

    object ActionListConverter {

        @TypeConverter
        @JvmStatic
        fun fromString(actionsStr: String) =
            AppModule.provideGson().fromJson(actionsStr, Array<Action>::class.java).toList()

        @TypeConverter
        @JvmStatic
        fun actionsToGson(actions: List<Action>) = AppModule.provideGson().toJson(actions)
    }

}
