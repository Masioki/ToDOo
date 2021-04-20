package pwr.mobilne.l3.repository

import androidx.room.TypeConverter
import pwr.mobilne.l3.domain.Priority
import pwr.mobilne.l3.domain.ReminderType
import pwr.mobilne.l3.domain.TodoItemType
import java.util.*

class Converters {

    @TypeConverter
    fun toReminderType(value: Int) = enumValues<ReminderType>()[value]

    @TypeConverter
    fun fromReminderType(value: ReminderType) = value.ordinal


    @TypeConverter
    fun toTodoItemType(value: Int) = enumValues<TodoItemType>()[value]

    @TypeConverter
    fun fromTodoItemType(value: TodoItemType) = value.ordinal


    @TypeConverter
    fun toPriority(value: Int) = enumValues<Priority>()[value]

    @TypeConverter
    fun fromPriority(value: Priority) = value.ordinal


    @TypeConverter
    fun toDate(dateLong: Long?): Date? {
        if (dateLong == null) return null;
        return Date(dateLong)
    }

    @TypeConverter
    fun fromDate(date: Date?) = date?.time
}