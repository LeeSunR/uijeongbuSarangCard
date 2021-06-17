package com.leesunr.uijeongbusarangcard.splash

import android.content.Context
import com.leesunr.uijeongbusarangcard.base.BasePresenter
import com.leesunr.uijeongbusarangcard.base.BaseView

interface SplashConstants {
    interface View:BaseView{
        fun gotoMain()
        fun appExit(reason:String)
        fun printMessage(text:String)
    }

    interface Presenter:BasePresenter {
        fun appInitialization(context: Context)
    }
}


