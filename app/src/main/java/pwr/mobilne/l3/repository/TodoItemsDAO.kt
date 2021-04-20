package pwr.mobilne.l3.repository

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import pwr.mobilne.l3.domain.TodoItem

@Dao
interface TodoItemsDAO {
    @Transaction
    @Query("SELECT * FROM todoitem")
    fun getAll(): List<TodoItem>

    @Transaction
    @Query("SELECT * FROM todoitem WHERE id = :id")
    fun getById(id: Int): TodoItem

    @Query("DELETE FROM todoitem WHERE id = :id")
    fun deleteById(id: Int)

    @Query("SELECT id FROM todoitem ORDER BY id DESC LIMIT 1")
    fun getHighestId(): Int

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveOrUpdate(item: TodoItem)
}