package com.leesunr.uijeongbusarangcard.base

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import io.reactivex.rxjava3.disposables.CompositeDisposable


abstract class AbstractView: AppCompatActivity(),BaseView {
    val tag = this::class.java.simpleName

    override fun showToast(message:String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    override fun showProgress(){
    }
    override fun hideProgress(){
    }
}