package com.leesunr.uijeongbusarangcard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction().replace(R.id.fragmentView, ListFragment()).commit()
        bottomNavi.setOnNavigationItemSelectedListener { item: MenuItem ->
            when(item.itemId){
                R.id.navigation_map -> supportFragmentManager.beginTransaction().replace(R.id.fragmentView, MapFragment()).commit()
                R.id.navigation_list -> supportFragmentManager.beginTransaction().replace(R.id.fragmentView, ListFragment()).commit()
            }
            true
        }

        var layoutParams = WindowManager.LayoutParams()
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.6f
        window.attributes=layoutParams

        frame_loading.setOnTouchListener { view, motionEvent -> return@setOnTouchListener true }
    }

    public fun loadingStart(){
        frame_loading.visibility=View.VISIBLE
    }
    public fun loadingDone(){
        frame_loading.visibility=View.GONE
    }
}
