package com.leesunr.uijeongbusarangcard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.requestPermissions

class CustomLocation(mContext:Context){

    private var mContext:Context? = null

    companion object{
        val REQUEST_CODE_MAP_LOCATION_UPDATE:Int = 0x332312
    }

    init{
        this.mContext = mContext
    }

    public fun getLatLng(code: Int): Location?{
        var location:Location? = null
        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(mContext as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),code)
        } else{
            val locationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        return location
    }


}