package com.davidroid.worktimer.view

import android.annotation.SuppressLint
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
        setCurrentDay()

        if (savedInstanceState == null) {
            currentTimer = DateUtil.getCurrentTime()
            currentDay = DateUtil.getCurrentDay()
        }

        if (getSharedPreferences().getLong(ActionType.START.name, 0) > 0) {
            currentTimer = DateUtil.getTimeWithFormat(getSharedPreferences().getLong(ActionType.START.name, 0))
            startAction(false)
        }
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

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            currentTimer = savedInstanceState.getString("currentTimer")
            currentDay = savedInstanceState.getString("currentDay")

            if (ActionType.START.name == savedInstanceState.getString("actionType")) {
                startAction(false)
            } else {
                stopAction(false)
            }
        }
        setCurrentDay()
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
            if (data.size > 0) {
                summaryBlock.visibility = View.VISIBLE
            } else {
                summaryBlock.visibility = View.INVISIBLE
            }
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

        if (forceStart) {
            currentTimer = DateUtil.getCurrentTime()
            currentDay = DateUtil.getCurrentDay()
        }

        supportActionBar?.title = getString(R.string.starting, currentTimer)

        startButton.visibility = View.GONE
        stopButton.visibility = View.VISIBLE
        stopButton.blinkAnimation(3500, 0.25f)

        summaryTitle.text = getString(R.string.calculating)
        summaryTitle.blinkAnimation(1500)

        if (forceStart) {
            getSharedPreferences().edit().putLong(ActionType.START.name, Date().time).apply()
            showNotification()
        }
    }

    private fun stopAction(forceStop: Boolean) {
        currentState = ActionType.STOP
        supportActionBar?.title = getString(R.string.stopped, currentTimer)

        startButton.visibility = View.VISIBLE
        stopButton.visibility = View.GONE
        stopButton.stopAnimation()

        setCurrentDay()
        summaryTitle.stopAnimation()

        if (forceStop) {
            presenter.createTimer(getSharedPreferences().getLong(ActionType.START.name, 0), Date().time)
            getSharedPreferences().edit().putLong(ActionType.START.name, 0).apply()
            hideNotification()
        }
    }

    @SuppressLint("NewApi")
    private fun showNotification() {
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val intent = PendingIntent.getActivity(applicationContext, 0, notificationIntent, 0)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelId = "channel_01"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, channelId, importance)

            val notification = NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_watch_later_white_24dp)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.calculating))
                    .setOngoing(true)
                    .setChannelId(channelId)
                    .setContentIntent(intent)
                    .build()

            val notificationManager = getNotificationManager()
            notificationManager.createNotificationChannel(channel)
            notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
            notificationManager.notify(1, notification)
        } else {
            val notification = NotificationCompat.Builder(this)
                    .setSmallIcon(R.mipmap.ic_watch_later_white_24dp)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(getString(R.string.calculating))
                    .setOngoing(true)
                    .setContentIntent(intent)
                    .build()

            val notificationManager = getNotificationManager()
            notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
            notificationManager.notify(1, notification)
        }
    }

    private fun hideNotification() {
        getNotificationManager().cancelAll()
    }
}

fun View.blinkAnimation(duration: Long, minAlpha: Float = 0.0f, maxAlpha: Float = 1.0f) {
    val anim = AlphaAnimation(minAlpha, maxAlpha)
    anim.duration = duration
    anim.startOffset = 20
    anim.repeatMode = Animation.REVERSE
    anim.repeatCount = Animation.INFINITE
    startAnimation(anim)
}

fun View.stopAnimation() {
    clearAnimation()
}
