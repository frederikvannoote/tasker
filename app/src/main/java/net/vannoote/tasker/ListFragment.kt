package net.vannoote.tasker

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.database.FirebaseRecyclerOptions
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.*
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator
import kotlinx.android.synthetic.main.fragment_list.view.*
import net.vannoote.tasker.datamodel.Task
import net.vannoote.tasker.datamodel.TaskAdapter
import net.vannoote.tasker.datamodel.TaskParser
import java.time.LocalDateTime


/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class ListFragment(taskerInteraction: TaskerInteraction, p_groupId: String) : Fragment() {

    private var visible: Boolean = false
    private var taskListAdaptor: TaskAdapter? = null
    private var mInteraction: TaskerInteraction = taskerInteraction
    private var removedTask: Task? = null
    private var groupId: String = p_groupId
    private val LOGTAG = "List"

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

        val tasks: FirebaseRecyclerOptions<Task> = FirebaseRecyclerOptions.Builder<Task>()
            .setQuery(FirebaseDatabase.getInstance().reference.child("groups/$groupId/tasks"), TaskParser())
            .build()

        taskListAdaptor = TaskAdapter(tasks)
        list.adapter = taskListAdaptor

        val itemTouchHelperCallback =
            object :
                ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                @RequiresApi(Build.VERSION_CODES.O)
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position: Int = viewHolder.adapterPosition
                    when {
                        direction == ItemTouchHelper.LEFT -> {
                            // Remove task
                            val taskId = viewHolder.itemView.tag.toString()
                            Log.i(LOGTAG, "User wants to remove task with ID $taskId")

                            // Take snapshot for Undo functionality
                            removedTask = Task("id", "taskname", "daily", LocalDateTime.now())

                            // Remove from database
                            val tasks = FirebaseDatabase.getInstance().reference?.child("groups/$groupId/tasks")
                            if (tasks != null) {
                                tasks.child(taskId).removeValue()
                            }

                            // Remove from UI
                            taskListAdaptor!!.notifyItemRemoved(viewHolder.adapterPosition)

                            Toast.makeText(context,
                                    "deleted",
                                    Toast.LENGTH_SHORT
                            ).show()

                            Snackbar.make(list, "Undo", Snackbar.LENGTH_LONG)
                                .setAction("Undo", View.OnClickListener {
                                    fun onClick(view: View) {
                                        taskListAdaptor!!.notifyItemInserted(viewHolder.adapterPosition)
                                    }
                                }).show()
                        }
                        direction == ItemTouchHelper.RIGHT -> {
                            // Task was done
                            val taskId = viewHolder.itemView.tag.toString()
                            Log.i(LOGTAG, "User has executed task with ID $taskId")

                            // Update last-executed field in database
                            FirebaseDatabase.getInstance().reference?.child("groups/$groupId/tasks/$taskId/lastExecuted").setValue(LocalDateTime.now().toString())

                            // Show notification
                            Toast.makeText(context,
                                    "archived",
                                    Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onChildDraw(
                        c: Canvas,
                        recyclerView: RecyclerView,
                        viewHolder: RecyclerView.ViewHolder,
                        dX: Float,
                        dY: Float,
                        actionState: Int,
                        isCurrentlyActive: Boolean
                ) {
                    RecyclerViewSwipeDecorator.Builder(context, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(context!!, R.color.removeTask))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete_task)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(context!!, R.color.executeTask))
                        .addSwipeRightActionIcon(R.drawable.ic_executed_task)
                        .create()
                        .decorate()

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }

            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(list)

        // To 'Add' screen
        view.fab.setOnClickListener { mInteraction.showAddScreen(groupId) }

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