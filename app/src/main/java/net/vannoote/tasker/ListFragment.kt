package net.vannoote.tasker

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
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
import com.google.firebase.database.FirebaseDatabase
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
class ListFragment(taskerInteraction: TaskerInteraction) : Fragment() {

    private var visible: Boolean = false
    private var taskListAdaptor: TaskAdapter? = null
    private var mInteraction: TaskerInteraction = taskerInteraction
    private var removedTask: Task? = null

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
                            removedTask = Task("id", "taskname", "daily", LocalDateTime.now())

//                            FirebaseDatabase.getInstance().reference.child("tasks").

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
                            Toast.makeText(context,
                                "archived",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
//                    noteViewModel.delete(noteAdapter.getNoteAt(viewHolder.adapterPosition))

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