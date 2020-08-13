package net.vannoote.tasker.datamodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.widget.Toast
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import net.vannoote.tasker.R
import java.time.LocalDate
import java.util.*

class TaskAlarmReceiver: BroadcastReceiver() {

    private var CHANNEL_ID: String = "tasker"

    fun setAlarm(context: Context, time: LocalDate?) {
        Log.i("Task", "subscribe to broadcaster")

        val alarmManager: AlarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent: Intent = Intent(context, TaskAlarmReceiver::class.java)
        val pendingIntent: PendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val calendar = Calendar.getInstance()
        calendar.add(Calendar.SECOND, 10)

        alarmManager?.set(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }

    override fun onReceive(context: Context, intent: Intent) {
        var title: String = "Verticuteren"

        Log.i("test", "TaskAlarmReceiver received")
        Toast.makeText(context, title, Toast.LENGTH_LONG).show()

        var builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.common_full_open_on_phone)
            .setContentTitle(title)
            .setContentText("Tijd om te " + title)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(context)) {
            // notificationId is a unique int for each notification that you must define
            notify(0, builder.build())
        }
    }
}