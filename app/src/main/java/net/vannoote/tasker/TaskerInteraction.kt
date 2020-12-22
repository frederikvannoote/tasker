package net.vannoote.tasker

interface TaskerInteraction {
    fun showListScreen(groupId: String)
    fun showAddScreen(groupId: String)
}