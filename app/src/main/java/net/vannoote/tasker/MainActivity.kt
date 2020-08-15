package net.vannoote.tasker

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.getSystemService
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.*
import net.vannoote.tasker.datamodel.Task
import net.vannoote.tasker.datamodel.TaskModel
import net.vannoote.tasker.TaskerInteraction
import java.time.LocalDate
import java.time.LocalDate.now
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(), Observer, TaskerInteraction {
    private lateinit var database: DatabaseReference
    private var manager = supportFragmentManager

    private var CHANNEL_ID: String = "tasker"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        setSupportActionBar(findViewById(R.id.toolbar))

        createNotificationChannel()

        showListScreen()
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

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "tasker" //getString(R.string.channel_name)
            val descriptionText = "description" //getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun showListScreen() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = ListFragment(this)
        transaction.replace(R.id.main_holder, fragment)
        transaction.commit()
    }

    override fun showAddScreen() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = AddFragment()
        transaction.replace(R.id.main_holder, fragment)
        transaction.commit()
    }
}