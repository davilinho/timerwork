package com.davidroid.worktimer.usecase

import com.davidroid.worktimer.datasource.DataSource
import com.davidroid.worktimer.model.AmountDay
import com.davidroid.worktimer.repository.Repository

/**
* Created by davidmartin on 5/12/17.
*/
class UseCase {

    private val repository =  Repository(DataSource())

    fun create(start: Long, stop: Long, callback: () -> Unit) {
        repository.create(start, stop) { callback() }
    }

    fun readAllForToday(callback: (List<AmountDay>) -> Unit) {
        repository.readAllForToday { callback(it) }
    }

    fun readAll(callback: (List<AmountDay>) -> Unit) {
        repository.readAll { callback(it) }
    }

    fun deleteAll() {
        repository.deleteAll()
    }
}