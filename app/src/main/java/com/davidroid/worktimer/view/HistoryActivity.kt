package com.davidroid.worktimer.view

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.davidroid.worktimer.R
import com.davidroid.worktimer.model.ActionType
import com.davidroid.worktimer.model.AmountDay
import com.davidroid.worktimer.presenter.HistoryPresenter
import kotlinx.android.synthetic.main.activity_main.*

/**
* Created by davidmartin on 7/12/17.
*/
interface IHistoryView {
    fun showAll(timers: List<AmountDay>)
}

class HistoryActivity : AppCompatActivity(), IHistoryView {

    private var presenter = HistoryPresenter(this)
    private val adapter = HistoryAdapter(mutableListOf())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.history)
        setSupportActionBar(toolbar)
        supportActionBar?.title = getString(R.string.history)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recycler.adapter = adapter

        presenter.showAll()
    }

    override fun showAll(timers: List<AmountDay>) {
        with(adapter) {
            data = timers.toMutableList()
            notifyDataSetChanged()
            getSharedPreferences("startTimer", Context.MODE_PRIVATE)
                    .edit().putLong(ActionType.START.name, 0).apply()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_history, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            R.id.action_settings -> {
                with(AlertDialog.Builder(this)) {
                    setMessage(getString(R.string.delete_his_question))
                    setNegativeButton(getString(R.string.cancel), { dialog, _ -> dialog.dismiss() })
                    setPositiveButton(getString(R.string.confirm), { _, _ -> presenter.deleteAll() })
                    show()
                }
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}