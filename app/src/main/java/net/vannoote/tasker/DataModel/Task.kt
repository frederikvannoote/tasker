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
class Task(p_id: String, p_name: String, p_period: String, p_lastExecuted: LocalDateTime?) {
    var id: String = p_id
    var name: String = p_name
    var period: String = p_period
    var lastExecuted: LocalDateTime? = p_lastExecuted
    var nextExecution: LocalDate? = LocalDate.now()

    private var alarmReceiver: TaskAlarmReceiver = TaskAlarmReceiver()

    init {
        if (lastExecuted != null) {
            var le = lastExecuted
            when {
                period == "daily" -> nextExecution = le!!.toLocalDate().plusDays(1)
                period == "weekly" -> nextExecution = le!!.toLocalDate().plusWeeks(1)
                period == "monthly" -> nextExecution = le!!.toLocalDate().plusMonths(1)
                period == "quarterly" -> nextExecution = le!!.toLocalDate().plusMonths(3)
                period == "halfyearly" -> nextExecution = le!!.toLocalDate().plusMonths(6)
                period == "yearly" -> nextExecution = le!!.toLocalDate().plusYears(1)
            }
            Log.i("task", "$name will be next executed on $nextExecution")
        }
    }
}