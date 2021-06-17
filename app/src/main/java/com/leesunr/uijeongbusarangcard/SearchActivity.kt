package com.leesunr.uijeongbusarangcard

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import com.leesunr.uijeongbusarangcard.base.AbstractView
import com.leesunr.uijeongbusarangcard.common.Constant
import kotlinx.android.synthetic.main.activity_search.*

class SearchActivity : AbstractView() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        initUI()
    }

    fun initUI(){
        edit_search.editText?.isFocusableInTouchMode = true
        edit_search.editText?.requestFocus()
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)

        edit_search.editText?.setOnKeyListener { v, keyCode, event ->
            if(event.action==KeyEvent.ACTION_UP && keyCode==KeyEvent.KEYCODE_ENTER){
                search(edit_search.editText!!.text.toString())
                return@setOnKeyListener true
            }else{
                return@setOnKeyListener false
            }
        }
    }

    fun search(word:String){
        Log.e(tag,"done")
        val intent = Intent()
        intent.putExtra("word",word)
        setResult(Constant.RESULT_CODE.OK,intent)
        finish()
    }
}