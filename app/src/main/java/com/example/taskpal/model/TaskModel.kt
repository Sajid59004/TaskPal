package com.example.taskpal.model

class TaskModel {
    var taskId: String? = null
    var taskName: String? = null
    var taskStatus: String? = null
    var userId: String? = null

    constructor()
    constructor(taskId: String?, taskName: String?, taskStatus: String?, userId: String?) {
        this.taskId = taskId
        this.taskName = taskName
        this.taskStatus = taskStatus
        this.userId = userId
    }
}