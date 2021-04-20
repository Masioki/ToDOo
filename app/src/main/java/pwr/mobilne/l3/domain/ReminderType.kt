package pwr.mobilne.l3.domain

import java.util.*

enum class ReminderType(val calendarType: Int) {
    ONCE(Calendar.DAY_OF_MONTH),
    DAILY(Calendar.DAY_OF_MONTH),
    WEEKLY(Calendar.WEEK_OF_MONTH),
    MONTHLY(Calendar.MONTH),
    YEARLY(Calendar.YEAR)

}