package pwr.mobilne.l3.domain

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import pwr.mobilne.l3.domain.Priority.LOW
import pwr.mobilne.l3.domain.TodoItemType.WORK
import java.io.Serializable

@Entity
data class TodoItem(@PrimaryKey(autoGenerate = false) val id: Int) : Serializable {
    @Embedded
    var reminder: Reminder? = null
    var type: TodoItemType = WORK
    var priority: Priority = LOW
    var text: String = ""
    var title: String = ""
        get() {
            if (field.isNotEmpty())
                return field
            if (this.text.length > 15) return this.text.substring(0, 15)
            return this.text
        }

    fun hasReminder(): Boolean {
        return reminder != null
    }
}