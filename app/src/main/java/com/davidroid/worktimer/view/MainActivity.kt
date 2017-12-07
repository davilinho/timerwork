package com.davidroid.worktimer.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.davidroid.worktimer.R
import com.davidroid.worktimer.dateUtil.DateUtil
import com.davidroid.worktimer.model.ActionType
import com.davidroid.worktimer.model.AmountDay
import com.davidroid.worktimer.presenter.Presenter
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

interface IView {
    fun createTimerFeedback()
    fun showAllForToday(timers: List<AmountDay>)
}

class MainActivity : AppCompatActivity(), IView {

    private var presenter = Presenter(this)
    private val adapter = ItemAdapter(mutableListOf())

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        summaryTitle.text = getString(R.string.summary, DateUtil.getCurrentDay())

        recycler.adapter = adapter

        val sharedPref = getSharedPreferences()

        startButton.setOnClickListener {
            supportActionBar?.title = getString(R.string.starting, DateUtil.getCurrentTime())
            status.visibility = View.VISIBLE
            status.text = getString(R.string.calculating)
            startButton.visibility = View.GONE
            stopButton.visibility = View.VISIBLE

            status.blinkAnimation(2500)

            sharedPref.edit().putLong(ActionType.START.name, Date().time).apply()
        }
        stopButton.setOnClickListener {
            supportActionBar?.title = getString(R.string.stopped, DateUtil.getCurrentTime())
            status.visibility = View.GONE
            startButton.visibility = View.VISIBLE
            stopButton.visibility = View.GONE

            status.stopAnimation()

            val start = sharedPref.getLong(ActionType.START.name, 0)
            presenter.createTimer(start, Date().time)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.showAllForToday()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this, HistoryActivity::class.java))
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun createTimerFeedback() {
        presenter.showAllForToday()
    }

    override fun showAllForToday(timers: List<AmountDay>) {
        with(adapter) {
            data = timers.toMutableList()
            notifyDataSetChanged()
            if (!timers.isEmpty()) { recycler.smoothScrollToPosition(timers.size - 1) }
        }
    }

    private fun getSharedPreferences(): SharedPreferences
            = getSharedPreferences("startTimer", Context.MODE_PRIVATE)
}

fun View.blinkAnimation(duration: Long) {
    val anim = AlphaAnimation(0.0f, 0.75f)
    anim.duration = duration
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE
    startAnimation(anim)
}

fun View.stopAnimation() {
    clearAnimation()
}
