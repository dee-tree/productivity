package edu.app.productivity.data.db

import androidx.room.TypeConverter
import java.util.Date

interface BaseConverters {

    class DateConverter {
        @TypeConverter
        fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

        @TypeConverter
        fun dateToTimestamp(date: Date?): Long? = date?.time
    }

}
