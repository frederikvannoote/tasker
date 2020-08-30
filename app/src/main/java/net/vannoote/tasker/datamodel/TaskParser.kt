package net.vannoote.tasker.datamodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.firebase.ui.database.SnapshotParser
import com.google.firebase.database.DataSnapshot
import java.time.LocalDate
import java.time.LocalDateTime

class TaskParser: SnapshotParser<Task> {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun parseSnapshot(snapshot: DataSnapshot): Task {
        val data: HashMap<String, Any> = snapshot.value as HashMap<String, Any>

        var id = snapshot.key ?: ""
        var name = data["name"] as String
        var period = data["period"] as String
        var lastExecuted: LocalDateTime? = null as LocalDateTime?

        try {
            if (data["lastExecuted"] != null)
            {
                lastExecuted = LocalDateTime.parse(data["lastExecuted"] as String)
                Log.i("task", "$name was last executed on $lastExecuted")
            }
        }
        catch (e: java.time.format.DateTimeParseException) {
            e.printStackTrace()
        }

        return Task(id, name, period, lastExecuted)
    }
}