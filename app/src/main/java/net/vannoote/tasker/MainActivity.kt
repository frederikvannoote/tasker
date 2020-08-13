package net.vannoote.tasker

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import com.google.firebase.database.*
import net.vannoote.tasker.datamodel.Task
import net.vannoote.tasker.datamodel.TaskModel
import java.time.LocalDate
import java.time.LocalDate.now
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), Observer {
    private lateinit var database: DatabaseReference
    private var mTaskListAdaptor: TaskListAdaptor? = null
    private var CHANNEL_ID: String = "tasker"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(findViewById(R.id.toolbar))

        createNotificationChannel()

        TaskModel
        TaskModel.addObserver(this)

        val data: ArrayList<Task> = ArrayList()
        val listview = findViewById<ListView>(R.id.main_tasklist)
        mTaskListAdaptor = TaskListAdaptor(this)
        listview.adapter = mTaskListAdaptor

        // Reference to database (google example)
//        database = Firebase.database.reference

        findViewById<FloatingActionButton>(R.id.fab).setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }


    private class TaskListAdaptor(context: Context): BaseAdapter() {

        private val mContext: Context
        private var database: DatabaseReference?
        private val mTasks: ArrayList<Task> = arrayListOf<Task>()
        private val alarmManager: AlarmManager

        init {
            mContext = context
            database = getDatabaseRef()
            alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            // Read from database
            val tasks = getDatabaseRef()?.child("tasks")
            if (tasks != null) {
                tasks.addValueEventListener(object : ValueEventListener {
                    @RequiresApi(Build.VERSION_CODES.O)
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        mTasks.clear()
                        for (child in dataSnapshot.children) {
                            mTasks.add(Task(child, alarmManager, context))
                        }

                        notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w("Main", "Failed to read value.", error.toException())
                    }
                })
            }
        }

        // Render each row
        @RequiresApi(Build.VERSION_CODES.O)
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.task_main, viewGroup, false)

            val name = row.findViewById<TextView>(R.id.name_textView)
            name.text = mTasks[position].name
            val due = row.findViewById<TextView>(R.id.due_textView)
            due.text = beautifyDate(mTasks[position].nextExecution)

            return row
        }

        override fun getItem(p0: Int): Any {
            return "test task"
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return mTasks.size
        }

        public fun getDatabaseRef(): DatabaseReference? {
            return FirebaseDatabase.getInstance().reference
        }

        @RequiresApi(Build.VERSION_CODES.O)
        private fun beautifyDate(moment: LocalDate?): String {
            return when {
                moment == null -> { "" }
                moment.isBefore(now()) -> { "Needs to be executed urgently" }
                moment == now() -> { "Needs to be executed today" }
                moment == now().plusDays(1) -> { "Tomorrow" }
                else -> { moment.toString() }
            }
        }
    }

    override fun update(p0: Observable?, p1: Any?) {
//        mTaskListAdaptor?.clear()
//
//        val data = TaskModel.getData()
//        if (data != null) {
//            mTaskListAdaptor?.clear()
//            mTaskListAdaptor?.add
//        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "tasker" //getString(R.string.channel_name)
            val descriptionText = "description" //getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}