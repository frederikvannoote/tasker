package net.vannoote.tasker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add_task_list.*
import kotlinx.android.synthetic.main.fragment_add_task_list.view.*

class AddTaskListFragment(taskerInteraction: TaskerInteraction) : Fragment() {

    private var mInteraction: TaskerInteraction = taskerInteraction

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_add_task_list, container, false)

        view.add_group.setOnClickListener {
            val groupId = addEntry()
            if (groupId != null) {
                mInteraction.showListScreen(groupId)
            }
        }
        return view
    }

    private fun addEntry(): String? {
        val groups = getDatabaseRef()?.child("groups")
        if (groups != null) {
            var group = HashMap<String, String>()
            group.put("displayname", tasklist_name.text.toString())

            val key = groups.push().key
            if (key == null) {
                Log.w("Database", "Couldn't get push key for posts")
                return null
            }

            val childUpdates = hashMapOf<String, Any>(
                "/$key" to group
            )

            groups.updateChildren(childUpdates)
            return key
        }
        return null
    }

    private fun getDatabaseRef(): DatabaseReference? {
        return FirebaseDatabase.getInstance().reference
    }
}