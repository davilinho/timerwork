package com.davidroid.worktimer.presenter

import com.davidroid.worktimer.usecase.UseCase
import com.davidroid.worktimer.view.IHistoryView

/**
* Created by davidmartin on 5/12/17.
*/
class HistoryPresenter(val view: IHistoryView) {

    private val useCase = UseCase()

    fun showAll() {
        useCase.readAll { view.showAll(it) }
    }

    fun deleteAll() {
        useCase.deleteAll()
        view.showAll(emptyList())
    }
}