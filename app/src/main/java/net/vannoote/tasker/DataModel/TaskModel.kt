package net.vannoote.tasker.DataModel

import android.util.Log
import com.google.firebase.database.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

object TaskModel: Observable() {
    private var mValueDataListener: ValueEventListener? = null
    private var mTaskList: ArrayList<Task>? = ArrayList()

    private fun getDatabaseRef(): DatabaseReference? {
        return FirebaseDatabase.getInstance().reference.child("task")
    }

    init {
        val listener = mValueDataListener
        if (listener != null) {
            getDatabaseRef()?.removeEventListener(listener)
        }

        mValueDataListener = null
        Log.i("TaskModel", "data init line 22")

        mValueDataListener = object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    Log.i("TaskModel", "data updated line 27")
                    val data: ArrayList<Task> = ArrayList()
                    if (snapshot != null) {
                        for (sn: DataSnapshot in snapshot.children) {
                            try {
                                data.add(Task(sn))
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        mTaskList = data
                        Log.i("TaskModel", "data updated, there are " + data.size + " tasks in the cache")
                        setChanged()
                        notifyObservers()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                if (error != null) {
                    Log.i("TaskModel", "data update cancelled. err: ${error.message}")
                }
            }
        }

        getDatabaseRef()?.addValueEventListener(mValueDataListener as ValueEventListener)
    }

    fun getData(): ArrayList<Task>? {
        return mTaskList
    }
}