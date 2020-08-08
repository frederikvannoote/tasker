package net.vannoote.tasker

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.database.*
import net.vannoote.tasker.DataModel.Task
import net.vannoote.tasker.DataModel.TaskModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), Observer {
    private lateinit var database: DatabaseReference
    private var mTaskListAdaptor: TaskListAdaptor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(findViewById(R.id.toolbar))

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
        private val names = arrayListOf<String>()

        init {
            mContext = context
            database = getDatabaseRef()

            // Read from database
            val tasks = getDatabaseRef()?.child("tasks")
            if (tasks != null) {
                tasks.addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        val value = dataSnapshot.value
                        Log.d("Main", "Value is: $value")

                        names.clear()
                        for (dataValues in dataSnapshot.children) {
                            names.add(dataValues.child("name").value.toString())
                        }

                        notifyDataSetChanged()
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Failed to read value
                        Log.w("Main", "Failed to read value.", error.toException())
                    }
                })
            }
//            Log.i("MainActivity", "Tasks: " + tasks) //.child(userId).child("username").setValue(name)
        }

        // Render each row
        override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
//            val textView = TextView(mContext)
//            textView.text = "Some task"
//            return textView

            val layoutInflater = LayoutInflater.from(mContext)
            val row = layoutInflater.inflate(R.layout.task_main, viewGroup, false)

            val name = row.findViewById<TextView>(R.id.name_textView)
            name.text = names.get(position)
            val due = row.findViewById<TextView>(R.id.due_textView)
            due.text = "Morgen"

            return row
        }

        override fun getItem(p0: Int): Any {
            return "test task"
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getCount(): Int {
            return names.size
        }

        public fun getDatabaseRef(): DatabaseReference? {
            return FirebaseDatabase.getInstance().reference
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
}