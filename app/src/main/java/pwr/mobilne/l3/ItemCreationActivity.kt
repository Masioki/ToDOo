package pwr.mobilne.l3

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView.GONE
import android.widget.AdapterView.VISIBLE
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pwr.mobilne.l3.R.layout
import pwr.mobilne.l3.databinding.ActivityItemCreationBinding
import pwr.mobilne.l3.domain.Priority
import pwr.mobilne.l3.domain.Reminder
import pwr.mobilne.l3.domain.ReminderType
import pwr.mobilne.l3.domain.ReminderType.ONCE
import pwr.mobilne.l3.domain.TodoItem
import pwr.mobilne.l3.domain.TodoItemType
import pwr.mobilne.l3.utils.IconTextAdapter
import java.text.SimpleDateFormat
import java.util.*


class ItemCreationActivity : AppCompatActivity() {
    private lateinit var binding: ActivityItemCreationBinding
    private lateinit var item: TodoItem
    private var reminder: Reminder? = null
    private var withReminder: Boolean = false

    companion object {
        val format: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ROOT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityItemCreationBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Priority.values())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.priority.adapter = adapter

//        val adapter1 =
//            ArrayAdapter(this, android.R.layout.simple_spinner_item, TodoItemType.values())
//        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.itemType.adapter = adapter1

        val adapter1 = IconTextAdapter(this, layout.icon_spinner_item, TodoItemType.values())
        binding.itemType.adapter = adapter1

        val adapter2 =
            ArrayAdapter(this, android.R.layout.simple_spinner_item, ReminderType.values())
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.reminderType.adapter = adapter2

        // inicjalizacja przy pierwszym uruchomieniu
        if (savedInstanceState == null) {
            item = intent.getSerializableExtra("item") as TodoItem
            reminder = (intent.getSerializableExtra("item") as? TodoItem)?.reminder
            withReminder = reminder != null
            if (!withReminder)
                binding.reminderLayout.visibility = GONE
            else if (reminder != null) {
                binding.reminderType.setSelection(
                    ReminderType.values().indexOfFirst { i -> i == reminder?.reminderType ?: ONCE })
                binding.repeatCount.setText((reminder?.repeatCount ?: 1).toString())
                binding.firstOccurrenceDate.setText(format.format(this.reminder!!.startDate))
            }

            binding.titleArea.setText(item.title)
            binding.textArea.setText(item.text)
            binding.priority.setSelection(
                Priority.values().indexOfFirst { i -> i == item.priority })
            binding.itemType.setSelection(
                TodoItemType.values().indexOfFirst { i -> i == item.type })
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean("withReminder", this.withReminder)
        outState.putSerializable("item", this.item)
        outState.putSerializable("reminder", this.reminder)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        this.withReminder = savedInstanceState.getBoolean("withReminder")
        this.item = savedInstanceState.getSerializable("item") as TodoItem
        this.reminder = savedInstanceState.getSerializable("reminder") as Reminder?
        setReminderVisible(this.withReminder)
    }


    @Throws(Exception::class)
    private fun getReminder(): Reminder {
        val date: Date
        try {
            date = format.parse(binding.firstOccurrenceDate.text.toString()) ?: throw Exception()
        } catch (e: Exception) {
            throw Exception("Date not filled!")
        }
        val reminderType = binding.reminderType.selectedItem as ReminderType
        val repeatCount = binding.repeatCount.text.toString().toIntOrNull() ?: 1
        val rem = Reminder(date, reminderType, repeatCount)
        rem.reminderId = this.reminder?.reminderId ?: 0
        return rem
    }

    @Throws(Exception::class)
    private fun getTodoItem(): TodoItem {
        if (withReminder) item.reminder = getReminder()
        else item.reminder = null
        item.title = binding.titleArea.text.toString()
        item.text = binding.textArea.text.toString()
        item.type = binding.itemType.selectedItem as TodoItemType
        item.priority = binding.priority.selectedItem as Priority

        if (item.title.isEmpty() && item.text.isEmpty()) throw Exception("Title and text are empty!")
        return item
    }

    private fun setReminderVisible(value: Boolean) {
        this.withReminder = value
        var visibility = VISIBLE
        if (!withReminder) visibility = GONE
        binding.reminderLayout.visibility = visibility
    }

    fun chooseDate(view: View) {
        val currentDate = Calendar.getInstance()
        val date = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, monthOfYear, dayOfMonth ->
                date.set(year, monthOfYear, dayOfMonth)
                TimePickerDialog(
                    this, { _, hourOfDay, minute ->
                        date.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        date.set(Calendar.MINUTE, minute)
                        binding.firstOccurrenceDate.text = format.format(date.time).toString()
                        println(binding.firstOccurrenceDate.text)
                    },
                    currentDate[Calendar.HOUR_OF_DAY],
                    currentDate[Calendar.MINUTE],
                    true
                ).show()
            },
            currentDate[Calendar.YEAR],
            currentDate[Calendar.MONTH],
            currentDate[Calendar.DATE]
        ).show()
    }

    fun withReminder(view: View) {
        setReminderVisible(!this.withReminder)
    }

    fun save(view: View) {
        try {
            TodoItemManager.save(getTodoItem())
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            return
        }

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("reload", "true")
        startActivity(intent)
    }

}