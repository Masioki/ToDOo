package pwr.mobilne.l3.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import pwr.mobilne.l3.domain.ReminderType.ONCE
import java.io.Serializable
import java.util.*

@Entity
class Reminder(
    val startDate: Date,
    val reminderType: ReminderType = ONCE,
    val repeatCount: Int = 1
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var reminderId: Int = 0
    var active: Boolean = true
    var lastReminded: Date? = null


    fun getDates(): List<Date> {
        if (reminderType == ONCE) return listOf(startDate)

        val result = mutableListOf<Date>()
        val c = Calendar.getInstance()
        c.time = startDate

        var i = repeatCount
        while (i > 0) {
            result.add(c.time)
            c.add(reminderType.calendarType, 1)
            i -= 1
        }
        return result
    }

    fun nextOccurrence(): Date? {
        val now = Date()
        val c = Calendar.getInstance()
        c.time = startDate
        if (reminderType == ONCE) {
            return if (startDate.before(now)) null else startDate
        }

        var count = repeatCount
        while (count > 0) {
            if (c.time.after(now)) return c.time
            count -= 1
            c.add(reminderType.calendarType, 1)
        }
        return null
    }
}