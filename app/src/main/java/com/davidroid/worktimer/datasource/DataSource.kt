package com.davidroid.worktimer.datasource

import com.davidroid.worktimer.dateUtil.DateUtil
import com.davidroid.worktimer.model.AmountDay
import io.realm.Realm
import java.util.*



/**
* Created by davidmartin on 5/12/17.
*/
class DataSource {

    fun create(start: Long, stop: Long, callback: () -> Unit) {
        val amount = AmountDay()
        val amountLong = stop - start
        amount.amount = amountLong
        amount.date = DateUtil.getSimpleDate(Date())

        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.copyToRealm(amount)
        realm.commitTransaction()

        callback()
    }

    fun readAllForToday(): List<AmountDay> {
        val realm = Realm.getDefaultInstance()
        return realm.where(AmountDay::class.java).findAll().filter { it.date == DateUtil.getSimpleDate(Date()) }
    }

    fun readAll(): List<AmountDay> {
        val finalList = mutableListOf<AmountDay>()
        val realm = Realm.getDefaultInstance()
        val results = realm.where(AmountDay::class.java).distinct("date")
        for (item in results) {
            val amountSum = realm.where(AmountDay::class.java).equalTo("date", item.date)
                    .sum("amount")
            if (amountSum != null) {
                val amount = AmountDay()
                amount.amount = amountSum.toLong()
                amount.date = item.date
                finalList.add(amount)
            }
        }
        return finalList
    }

    fun deleteAll() {
        val realm = Realm.getDefaultInstance()
        realm.beginTransaction()
        realm.deleteAll()
        realm.commitTransaction()
    }
}