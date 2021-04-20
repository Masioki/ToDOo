package pwr.mobilne.l3

import android.annotation.SuppressLint
import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.Callback
import androidx.recyclerview.widget.ItemTouchHelper.LEFT
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.google.android.material.snackbar.Snackbar
import pwr.mobilne.l3.utils.SortType.DATE
import pwr.mobilne.l3.utils.SortType.DATE_DESC
import pwr.mobilne.l3.utils.SortType.NONE
import pwr.mobilne.l3.utils.SortType.PRIORITY
import pwr.mobilne.l3.utils.SortType.PRIORITY_DESC
import pwr.mobilne.l3.utils.SortType.TYPE
import pwr.mobilne.l3.utils.SortType.TYPE_DESC
import pwr.mobilne.l3.databinding.ActivityMainBinding
import pwr.mobilne.l3.domain.TodoItem
import pwr.mobilne.l3.utils.BounceEdgeEffectFactory
import pwr.mobilne.l3.utils.SortType
import pwr.mobilne.l3.utils.TodoItemsAdapter

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val items: MutableList<TodoItem> = mutableListOf()
    private val itemsToDisplay: MutableList<TodoItem> = mutableListOf()
    private val itemsAdapter: TodoItemsAdapter = TodoItemsAdapter(itemsToDisplay, this)
    private var sortType: SortType = NONE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        createNotificationChannel()

        // recycler view
        binding.rvItems.adapter = this.itemsAdapter
        binding.rvItems.layoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.rvItems.edgeEffectFactory = BounceEdgeEffectFactory()

        // swipe
        val touchHelper = ItemTouchHelper(object : Callback() {
            override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: ViewHolder): Int {
                return makeMovementFlags(0, LEFT)
            }

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: ViewHolder,
                target: ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: ViewHolder, direction: Int) {
                val i = itemsToDisplay[viewHolder.adapterPosition]
                delete(i)
                Snackbar.make(
                    binding.root.rootView,
                    "Deleted item: " + i.title,
                    Snackbar.LENGTH_LONG
                )
                    .setAction("UNDO") {
                        restore(i)
                    }
                    .show()
            }

            override fun getSwipeThreshold(viewHolder: ViewHolder): Float = 0.6f
        })
        touchHelper.attachToRecyclerView(binding.rvItems)

        // sorting
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.page_0 -> {
                    sortType = NONE
                    displayItems()
                    true
                }
                R.id.page_1 -> {
                    sortType = when (sortType) {
                        DATE_DESC -> DATE
                        DATE -> DATE_DESC
                        else -> DATE
                    }
                    displayItems()
                    true
                }
                R.id.page_2 -> {
                    sortType = when (sortType) {
                        PRIORITY_DESC -> PRIORITY
                        PRIORITY -> PRIORITY_DESC
                        else -> PRIORITY
                    }
                    displayItems()
                    true
                }
                R.id.page_3 -> {
                    sortType = when (sortType) {
                        TYPE_DESC -> TYPE
                        TYPE -> TYPE_DESC
                        else -> TYPE
                    }
                    displayItems()
                    true
                }
                else -> false
            }
        }

        // on first call
        if (savedInstanceState == null) {
            TodoItemManager.initializeContext(application.applicationContext as Application)
            reloadItems()
            displayItems()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable("items", this.items.toTypedArray())
        outState.putSerializable("sortType", this.sortType)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        items.addAll(savedInstanceState.getSerializable("items") as Array<TodoItem>)
        sortType = savedInstanceState.getSerializable("sortType") as SortType
        displayItems()
    }


    override fun onResume() {
        super.onResume()
        reloadItems()
        displayItems()  //TODO
//        println("RESUME")
//        println(intent.getStringExtra("reload"))
//        if (intent.getStringExtra("reload").toString() == "true") {
//            println("realod")
//            reloadItems()
//        }
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            0.toString(),
            "Todo notification",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Todo notification"
        }
        // Register the channel with the system
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun reloadItems() {
        items.clear()
        items.addAll(TodoItemManager.getAll().reversed())
        itemsToDisplay.clear()
        itemsToDisplay.addAll(items)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun displayItems() {
        val descending = sortType == DATE_DESC || sortType == PRIORITY_DESC || sortType == TYPE_DESC
        when (sortType) {
            DATE, DATE_DESC -> {
                if (descending) itemsToDisplay.sortByDescending { i -> i.reminder?.nextOccurrence() }
                else itemsToDisplay.sortBy { i -> i.reminder?.nextOccurrence() }
            }
            TYPE, TYPE_DESC -> {
                if (descending) itemsToDisplay.sortByDescending { i -> i.type }
                else itemsToDisplay.sortBy { i -> i.type }
            }
            PRIORITY, PRIORITY_DESC -> {
                if (descending) itemsToDisplay.sortByDescending { i -> i.priority }
                else itemsToDisplay.sortBy { i -> i.priority }
            }
            NONE -> {
                itemsToDisplay.clear()
                itemsToDisplay.addAll(items)
            }
        }
        itemsAdapter.notifyDataSetChanged()
    }

    fun restore(i: TodoItem) {
        TodoItemManager.save(i)
        reloadItems()
        displayItems()
    }

    fun delete(i: TodoItem) {
        items.remove(i)
        val ind = itemsToDisplay.indexOf(i)
        itemsToDisplay.removeAt(ind)
        TodoItemManager.delete(i.id)
        itemsAdapter.notifyItemRemoved(ind)
    }

    fun open(item: TodoItem) {
        val intent = Intent(this, ItemCreationActivity::class.java)
        intent.putExtra("item", item)
        startActivity(intent)
    }

    fun newItem(view: View) {
        val item = TodoItem(TodoItemManager.getFreeID())
        val intent = Intent(this, ItemCreationActivity::class.java)
        intent.putExtra("item", item)
        startActivity(intent)
    }
}