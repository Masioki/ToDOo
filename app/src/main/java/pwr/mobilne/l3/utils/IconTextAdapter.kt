package pwr.mobilne.l3.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import pwr.mobilne.l3.R.id
import pwr.mobilne.l3.R.layout
import pwr.mobilne.l3.domain.TodoItemType
import java.util.*

class IconTextAdapter(context: Context, textViewResourceId: Int, values: Array<TodoItemType>) :
    ArrayAdapter<TodoItemType>(context, textViewResourceId, values) {

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View =
        getCustomView(position, convertView, parent)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View =
        getCustomView(position, convertView, parent)


    private fun getCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val row = LayoutInflater.from(context).inflate(layout.icon_spinner_item, parent, false)
        val text: TextView = row.findViewById(id.isi_text)
        val icon: ImageView = row.findViewById(id.isi_icon)
        val item = getItem(position)
        text.text = item.toString()
        icon.setImageResource(
            context.resources.getIdentifier(
                "icon_" + item.toString().toLowerCase(Locale.ROOT),
                "drawable",
                context.applicationInfo.packageName
            )
        )


        return row
    }
}