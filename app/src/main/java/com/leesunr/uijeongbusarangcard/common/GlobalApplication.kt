package com.leesunr.uijeongbusarangcard.common

import android.app.Application
import com.naver.maps.map.NaverMapSdk

class GlobalApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        NaverMapSdk.getInstance(this).setClient(
            NaverMapSdk.NaverCloudPlatformClient("ucf1ihgqmr")
        )
    }
}