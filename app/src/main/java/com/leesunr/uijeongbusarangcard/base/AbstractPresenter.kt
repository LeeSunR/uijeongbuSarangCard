package com.leesunr.uijeongbusarangcard.base

import io.reactivex.rxjava3.disposables.CompositeDisposable


abstract class AbstractPresenter:BasePresenter {

    val disposables:CompositeDisposable = CompositeDisposable()
    val tag = this::class.java.simpleName

    override fun detach() {
        disposables.clear()
    }
}