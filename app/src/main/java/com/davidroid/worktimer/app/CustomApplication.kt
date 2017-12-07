package com.davidroid.worktimer.app

import android.app.Application
import io.realm.Realm

/**
* Created by davidmartin on 5/12/17.
*/
class CustomApplication: Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}