package net.vannoote.tasker

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.google.firebase.database.*
import net.vannoote.tasker.datamodel.Task
import java.time.LocalDate

class TaskListAdaptor(context: Context): BaseAdapter() {

    private val mContext: Context = context
    private var database: DatabaseReference?
    private val mTasks: ArrayList<Task> = arrayListOf<Task>()
    private val alarmManager: AlarmManager

    init {
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
            moment.isBefore(LocalDate.now()) -> { "Needs to be executed urgently" }
            moment == LocalDate.now() -> { "Needs to be executed today" }
            moment == LocalDate.now().plusDays(1) -> { "Tomorrow" }
            else -> { moment.toString() }
        }
    }
}