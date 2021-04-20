package pwr.mobilne.l3

import android.app.Application
import androidx.room.Room
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import pwr.mobilne.l3.domain.TodoItem
import pwr.mobilne.l3.repository.TodoItemsDAO
import pwr.mobilne.l3.repository.TodoItemsDB
import pwr.mobilne.l3.utils.OneTimeScheduleWorker
import java.util.*
import java.util.concurrent.TimeUnit

object TodoItemManager {
    private var availableID: Int = 0
    private lateinit var db: TodoItemsDAO
    private lateinit var application: Application

    fun initializeContext(app: Application) {
        db = Room
            .databaseBuilder(app, TodoItemsDB::class.java, "TodoItems")
            .allowMainThreadQueries()
            .build()
            .itemsDao()
        availableID = db.getHighestId() + 1
        application = app
    }

    @Synchronized
    fun getFreeID(): Int {
        availableID += 1
        return availableID - 1
    }

    @Synchronized
    fun save(item: TodoItem) {
        if (item.hasReminder()) {
            val tag = item.id.toString()
            val data = Data.Builder()
                .putString("title", item.title)
                .putString("text", item.text)
                .putString("type", item.type.toString())
                .build()
            val now = Date()
            var dates = item.reminder?.getDates() ?: emptyList()
            dates = dates.filter { d -> d.after(now) }

            WorkManager.getInstance(application).cancelAllWorkByTag(tag)
            for (date in dates) {
                val delayInMinutes = (date.time - now.time) / 60000
                val notificationWork = OneTimeWorkRequest.Builder(OneTimeScheduleWorker::class.java)
                    .setInitialDelay(delayInMinutes, TimeUnit.MINUTES)
                    .setInputData(data)
                    .addTag(tag)
                    .build()
                WorkManager.getInstance(application).enqueue(notificationWork)
            }
        }
        db.saveOrUpdate(item)
    }

    @Synchronized
    fun delete(id: Int) {
        val tag = get(id).id.toString()
        WorkManager.getInstance(application).cancelAllWorkByTag(tag)
        db.deleteById(id)
    }

    fun get(id: Int): TodoItem {
        return db.getById(id)
    }

    fun getAll(): List<TodoItem> {
        return db.getAll()
    }
}