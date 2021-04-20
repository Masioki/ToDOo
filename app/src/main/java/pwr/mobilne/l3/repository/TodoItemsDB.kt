package pwr.mobilne.l3.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pwr.mobilne.l3.domain.TodoItem

@Database(entities = arrayOf(TodoItem::class), version = 1, exportSchema = true)
@TypeConverters(Converters::class)
abstract class TodoItemsDB : RoomDatabase() {
    abstract fun itemsDao(): TodoItemsDAO
}