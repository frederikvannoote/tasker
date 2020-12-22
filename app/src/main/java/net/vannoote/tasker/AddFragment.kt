package net.vannoote.tasker

import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.view.*

/**
 * An example full-screen fragment that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
class AddFragment(taskerInteraction: TaskerInteraction, groupId: String) : Fragment() {

    private var mInteraction: TaskerInteraction = taskerInteraction
    private var mGroupId: String = groupId

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add, container, false)

        // Add entry. Return to list fragment
        view.add.setOnClickListener {
            addEntry()
            mInteraction.showListScreen(mGroupId)
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }

    override fun onPause() {
        super.onPause()
        activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        show()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun addEntry() {
        val tasks = getDatabaseRef()?.child("groups/$mGroupId/tasks")
        if (tasks != null) {
            var task = HashMap<String, String>()
            task.put("name", name.text.toString())
            if (daily.isChecked) { task.put("period", "daily") }
            if (weekly.isChecked) { task.put("period", "weekly") }
            if (monthly.isChecked) { task.put("period", "monthly") }
            if (quarterly.isChecked) { task.put("period", "quarterly") }
            if (halfyearly.isChecked) { task.put("period", "halfyearly") }
            if (yearly.isChecked) { task.put("period", "yearly") }

            val key = tasks.push().key
            if (key == null) {
                Log.w("Database", "Couldn't get push key for posts")
                return
            }

            val childUpdates = hashMapOf<String, Any>(
                "/$key" to task
            )

            tasks.updateChildren(childUpdates)
        }
    }

    public fun getDatabaseRef(): DatabaseReference? {
        return FirebaseDatabase.getInstance().reference
    }

    private fun hide() {
    }

    private fun show() {
    }
}