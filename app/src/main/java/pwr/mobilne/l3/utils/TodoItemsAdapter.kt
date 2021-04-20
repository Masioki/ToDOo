package pwr.mobilne.l3.utils

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import pwr.mobilne.l3.ItemCreationActivity
import pwr.mobilne.l3.MainActivity
import pwr.mobilne.l3.R.id
import pwr.mobilne.l3.R.layout
import pwr.mobilne.l3.domain.TodoItem
import pwr.mobilne.l3.utils.TodoItemsAdapter.ViewHolder
import java.util.*

class TodoItemsAdapter(private val items: List<TodoItem>, private val mainActivity: MainActivity) :
    RecyclerView.Adapter<ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val title: TextView = itemView.findViewById(id.itemTitle)
        val priority: TextView = itemView.findViewById(id.itemPriority)
        val icon: ImageView = itemView.findViewById(id.itemIcon)
        val reminderDate: TextView = itemView.findViewById(id.itemReminderDate)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(layout.todo_item, parent, false)
        return ViewHolder(contactView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.title.text = item.title
        holder.priority.text = item.priority.toString()
        holder.icon.setImageResource(
            mainActivity.applicationContext.resources.getIdentifier(
                "icon_" + item.type.toString().toLowerCase(Locale.ROOT),
                "drawable",
                mainActivity.applicationContext.applicationInfo.packageName
            )
        )
        holder.itemView.setOnClickListener { mainActivity.open(item) }

        val d = item.reminder?.nextOccurrence()
        if (d != null) {
            holder.reminderDate.visibility = VISIBLE
            holder.reminderDate.text = ItemCreationActivity.format.format(d)
        } else {
            holder.reminderDate.visibility = GONE
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }
}