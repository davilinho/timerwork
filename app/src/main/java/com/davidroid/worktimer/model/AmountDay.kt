package com.davidroid.worktimer.model

import io.realm.RealmObject
import java.util.*

/**
* Created by davidmartin on 7/12/17.
*/
open class AmountDay: RealmObject() {
    var amount: Long = 0
    lateinit var date: Date
}