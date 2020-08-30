package net.vannoote.tasker

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_list.view.*
import net.vannoote.tasker.datamodel.Task
import net.vannoote.tasker.datamodel.TaskAdapter
import net.vannoote.tasker.datamodel.TaskParser


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ListFragment(taskerInteraction: TaskerInteraction) : Fragment() {

    private var visible: Boolean = false
    private var taskListAdaptor: TaskAdapter? = null
    private var mInteraction: TaskerInteraction = taskerInteraction

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)

        // Task list
        val list = view.findViewById<RecyclerView>(R.id.main_tasklist)
        list.layoutManager = LinearLayoutManager(this.context)

        val options: FirebaseRecyclerOptions<Task> = FirebaseRecyclerOptions.Builder<Task>()
            .setQuery(FirebaseDatabase.getInstance().reference.child("tasks"), TaskParser())
            .build()

        taskListAdaptor = TaskAdapter(options)
        list.adapter = taskListAdaptor

        // To 'Add' screen
        view.fab.setOnClickListener { mInteraction.showAddScreen() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visible = true
    }

    override fun onStart() {
        super.onStart()
        taskListAdaptor?.startListening()
    }

    override fun onStop() {
        super.onStop()
        taskListAdaptor?.stopListening()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun hide() {
    }

    @Suppress("InlinedApi")
    private fun show() {
    }
}