package net.vannoote.tasker

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.FirebaseError
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import net.vannoote.tasker.datamodel.User
import java.util.*


class MainActivity : AppCompatActivity(), Observer, TaskerInteraction {
    private val database = FirebaseDatabase.getInstance()
    private var manager = supportFragmentManager
    private val RC_SIGN_IN = 1307 //the request code could be any Integer
    private val auth = FirebaseAuth.getInstance()
    private var CHANNEL_ID: String = "tasker"
    private var LOGTAG = "Main"
    private var user: FirebaseUser? = null
    private var groupId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        createNotificationChannel()

        if(auth.currentUser != null){ //If user is signed in
            user = FirebaseAuth.getInstance().currentUser
            Log.d(LOGTAG, "User " + user?.displayName + " with ID " + user?.uid + " is signed in")

            readUserDetails(user?.uid)
        }
        else {
            showLogin()
        }
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

    override fun showListScreen(groupId: String) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = ListFragment(this, groupId)
        transaction.replace(R.id.main_holder, fragment)
        transaction.commit()
    }

    override fun showAddScreen(groupId: String) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = AddFragment(this, groupId)
        transaction.replace(R.id.main_holder, fragment)
        transaction.addToBackStack("test")
        transaction.commit()
    }

    private fun showAddTaskListScreen() {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = AddTaskListFragment(this)
        transaction.replace(R.id.main_holder, fragment)
        transaction.addToBackStack("test")
        transaction.commit()
    }

    private fun showLogin() {
        Log.d(LOGTAG, "User is not signed in. Switch to login activity...")
        val providers = arrayListOf(
                AuthUI.IdpConfig.EmailBuilder().build(),
                AuthUI.IdpConfig.GoogleBuilder().build()
        )

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN
        )
    }

    fun showShareScreen(groupId: String) {
        val transaction = supportFragmentManager.beginTransaction()
        val fragment = ShareListFragment(groupId)
        transaction.replace(R.id.main_holder, fragment)
        transaction.addToBackStack("test")
        transaction.commit()
    }

    private fun readUserDetails(userId: String?) {
        val usersRef = database.reference.child("users/$userId")
        usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.e(LOGTAG, "Error while querying user details: " + error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue<User>(User::class.java)
                if (user != null) {
                    Log.d(LOGTAG, "User ID " + userId + "belongs to group" + user.group)
                    groupId = user.group

                    showListScreen(user.group)
                }
                else {
                    showAddTaskListScreen()
                }
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == 0) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().currentUser

                // Read user details
                readUserDetails(user?.uid)

            } else {
                //showListScreen()
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {

        R.id.action_share -> {
            // User chose the "Share" item, show the list share UI...
            groupId?.let { showShareScreen(it) }
            true
        }

        R.id.logout -> {
            // User chose the "Logout" action

            Log.d(LOGTAG, "User is logging out")
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener {
                        user = null
                        showLogin()
                    }
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }
}
