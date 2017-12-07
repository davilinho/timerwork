package com.davidroid.worktimer.presenter

import com.davidroid.worktimer.usecase.UseCase
import com.davidroid.worktimer.view.IView

/**
* Created by davidmartin on 5/12/17.
*/
class Presenter(val view: IView) {

    private val useCase = UseCase()

    fun createTimer(start: Long, stop: Long) {
        useCase.create(start, stop) { view.createTimerFeedback() }
    }

    fun showAllForToday() {
        useCase.readAllForToday { view.showAllForToday(it) }
    }
}