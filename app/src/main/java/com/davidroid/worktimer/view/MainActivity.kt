package com.davidroid.worktimer.view

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.app.NotificationCompat
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
    private var currentTimer: String = ""
    private var currentDay: String = ""
    private var currentState: ActionType = ActionType.STOP

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        bindActions()

        recycler.adapter = adapter

        if (savedInstanceState != null) {
            currentTimer = savedInstanceState.getString("currentTimer")
            currentDay = savedInstanceState.getString("currentDay")

            if (ActionType.START.name == savedInstanceState.getString("actionType")) {
                startAction(false)
            } else {
                stopAction(false)
            }
        } else {
            currentTimer = DateUtil.getCurrentTime()
            currentDay = DateUtil.getCurrentDay()
        }
        setCurrentDay()
    }

    override fun onResume() {
        super.onResume()
        presenter.showAllForToday()
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putString("currentTimer", currentTimer)
        outState?.putString("currentDay", currentDay)
        outState?.putString("actionType", currentState.name)
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

    private fun bindActions() {
        startButton.setOnClickListener { startAction(true) }
        stopButton.setOnClickListener { stopAction(true) }
    }

    private fun setCurrentDay() {
        summaryTitle.text = getString(R.string.summary, currentDay)
    }

    private fun getSharedPreferences(): SharedPreferences
            = getSharedPreferences("startTimer", Context.MODE_PRIVATE)

    private fun getNotificationManager(): NotificationManager
            = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun startAction(forceStart: Boolean) {
        currentState = ActionType.START
        supportActionBar?.title = getString(R.string.starting, currentTimer)

        status.visibility = View.VISIBLE
        status.text = getString(R.string.calculating)
        startButton.visibility = View.GONE
        stopButton.visibility = View.VISIBLE

        status.blinkAnimation(2500)

        if (forceStart) {
            getSharedPreferences().edit().putLong(ActionType.START.name, Date().time).apply()
        }

        showNotification()
    }

    private fun stopAction(forceStop: Boolean) {
        currentState = ActionType.STOP
        supportActionBar?.title = getString(R.string.stopped, currentTimer)

        status.visibility = View.GONE
        startButton.visibility = View.VISIBLE
        stopButton.visibility = View.GONE

        status.stopAnimation()

        if (forceStop) {
            val start = getSharedPreferences().getLong(ActionType.START.name, 0)
            presenter.createTimer(start, Date().time)
        }

        hideNotification()
    }

    private fun showNotification() {
        val channelId = "channel_01"
        val importance = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationManager.IMPORTANCE_HIGH
        } else { TODO("VERSION.SDK_INT < N") }
        val channel = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(channelId, channelId, importance)
        } else { TODO("VERSION.SDK_INT < O") }

        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)

        val notification = NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_watch_later_white_24dp)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(getString(R.string.calculating))
                .setOngoing(true)
                .setChannelId(channelId)
                .setContentIntent(intent)
                .build()

        val notificationManager = getNotificationManager()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(channel)
        }

        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR

        notificationManager.notify(1, notification)
    }

    private fun hideNotification() {
        getNotificationManager().cancelAll()
    }
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
