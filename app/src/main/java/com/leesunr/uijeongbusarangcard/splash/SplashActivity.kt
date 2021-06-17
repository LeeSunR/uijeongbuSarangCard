package com.leesunr.uijeongbusarangcard.splash

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import com.leesunr.uijeongbusarangcard.main.MainActivity
import com.leesunr.uijeongbusarangcard.R
import com.leesunr.uijeongbusarangcard.base.AbstractView
import kotlin.system.exitProcess

class SplashActivity : AbstractView(), SplashConstants.View {

    private val splashPresenter:SplashConstants.Presenter = SplashPresenter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        splashPresenter.appInitialization(this)
    }

    override fun gotoMain() {
        val intent = Intent(this,
            MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun appExit(reason: String) {
        AlertDialog.Builder(this)
            .setTitle("안내")
            .setMessage(reason)
            .setPositiveButton(R.string.btn_close){ _: DialogInterface, _: Int ->
                exitProcess(-1)
            }.show()
    }

    override fun printMessage(text: String) {
        findViewById<TextView>(R.id.text_splash_message).text = text
    }
}