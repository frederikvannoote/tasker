package net.vannoote.tasker

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_list.view.*
import net.vannoote.tasker.datamodel.Task
import net.vannoote.tasker.TaskListAdaptor
import java.time.LocalDate

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ListFragment(taskerInteraction: TaskerInteraction) : Fragment() {

    private var visible: Boolean = false
    private var mTaskListAdaptor: TaskListAdaptor? = null
    private var mInteraction: TaskerInteraction = taskerInteraction

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mTaskListAdaptor = TaskListAdaptor(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_list, container, false)
        view.main_tasklist.adapter = mTaskListAdaptor

        // To 'Add' screen
        view.fab.setOnClickListener { mInteraction.showAddScreen() }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        visible = true
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