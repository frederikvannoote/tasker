package net.vannoote.tasker.DataModel

import java.lang.Exception
import com.google.firebase.database.DataSnapshot

class Task(snapshot: DataSnapshot) {
    var id: String = String()
    var name: String = String()
    var period: String = String()

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