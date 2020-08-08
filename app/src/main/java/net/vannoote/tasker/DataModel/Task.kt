package net.vannoote.tasker.DataModel

import java.lang.Exception
import com.google.firebase.database.DataSnapshot

class Task(snapshot: DataSnapshot) {
    lateinit var id: String
    lateinit var name: String
    lateinit var period: String

    init {
        try {
            val data: HashMap<String, Any> = snapshot.value as HashMap<String, Any>
            id = snapshot.key ?: ""
            name = data["name"] as String
            period = data["period"] as String
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }
}