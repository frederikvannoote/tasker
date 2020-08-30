package net.vannoote.tasker.datamodel

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.firebase.ui.database.FirebaseRecyclerOptions
import net.vannoote.tasker.R
import java.time.LocalDate


class TaskAdapter(options: FirebaseRecyclerOptions<Task>) : FirebaseRecyclerAdapter<Task, TaskAdapter.TaskHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskHolder {
        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item

        // Create a new instance of the ViewHolder, in this case we are using a custom
        // layout called R.layout.message for each item
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_main, parent, false)

        return TaskHolder(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskHolder, position: Int, model: Task) {
        holder.name.text = model.name
        holder.due.text = beautifyDate(model.nextExecution)
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

    class TaskHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var name = itemView.findViewById<TextView>(R.id.name_textView)!!
        var due = itemView.findViewById<TextView>(R.id.due_textView)!!
    }
}