package net.vannoote.tasker.datamodel

import android.app.AlarmManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.lang.Exception
import com.google.firebase.database.DataSnapshot
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Period
import kotlin.collections.HashMap


@RequiresApi(Build.VERSION_CODES.O)
class Task(snapshot: DataSnapshot, alarmManager: AlarmManager, context: Context) {
    var id: String = String()
    var name: String = String()
    var period: String = String()
    var lastExecuted: LocalDateTime? = null
    var nextExecution: LocalDate? = LocalDate.now()

    private var alarmReceiver: TaskAlarmReceiver = TaskAlarmReceiver()

    init {
        try {
            val data: HashMap<String, Any> = snapshot.value as HashMap<String, Any>

            id = snapshot.key ?: ""
            name = data["name"] as String
            period = data["period"] as String
            try {
                if (data["lastExecuted"] != null)
                {
                    var le = LocalDateTime.parse(data["lastExecuted"] as String)
                    lastExecuted = le
                    if (lastExecuted != null) {
                        when {
                            period == "daily" -> nextExecution = le.toLocalDate().plusDays(1)
                            period == "weekly" -> nextExecution = le.toLocalDate().plusWeeks(1)
                            period == "monthly" -> nextExecution = le.toLocalDate().plusMonths(1)
                            period == "quarterly" -> nextExecution = le.toLocalDate().plusMonths(3)
                            period == "halfyearly" -> nextExecution = le.toLocalDate().plusMonths(6)
                            period == "yearly" -> nextExecution = le.toLocalDate().plusYears(1)
                        }
                    }
                }

                Log.i("Tasks",
                    "$name needs to be executed on $nextExecution. (Last: $lastExecuted)"
                )
                alarmReceiver.setAlarm(context, nextExecution)
            }
            catch (e: java.time.format.DateTimeParseException) {
                e.printStackTrace()
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}