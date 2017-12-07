package com.davidroid.worktimer.repository

import com.davidroid.worktimer.datasource.DataSource
import com.davidroid.worktimer.model.AmountDay

/**
* Created by davidmartin on 5/12/17.
*/
class Repository(private val dataSource: DataSource) {

    fun create(start: Long, stop: Long, callback: () -> Unit) {
        dataSource.create(start, stop) { callback() }
    }

    fun readAllForToday(callback: (List<AmountDay>) -> Unit) {
        callback(dataSource.readAllForToday())
    }

    fun readAll(callback: (List<AmountDay>) -> Unit) {
        callback(dataSource.readAll())
    }

    fun deleteAll() {
        dataSource.deleteAll()
    }
}