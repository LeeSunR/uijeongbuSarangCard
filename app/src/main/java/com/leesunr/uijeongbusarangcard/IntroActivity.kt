package com.leesunr.uijeongbusarangcard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_intro.*
import org.json.JSONArray
import kotlin.concurrent.thread

class IntroActivity : AppCompatActivity() {

    var dbHandler : DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
        getPermission()
    }

    private fun initAppData(){
        thread {
            runOnUiThread { intro_loading_text.text = "데이터베이스 업데이트중" }
            dbHandler = DatabaseHelper(this)
            dbHandler!!.deleteAll()
            val assetManager = resources.assets
            val inputStream= assetManager.open("data.json")

            runOnUiThread { intro_loading_text.text = "새로운 데이터베이스를 확인중" }
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonArray:JSONArray = JSONArray(jsonString)
            var storeArray:ArrayList<Store> = ArrayList<Store>()
            for (i in 0 until jsonArray.length()){
                var stroe:Store = Store()
                stroe.STORE_ID = i;
                stroe.SIGUN_NM = jsonArray.getJSONObject(i).getString("SIGUN_NM")
                stroe.CMPNM_NM = jsonArray.getJSONObject(i).getString("CMPNM_NM")
                stroe.INDUTYPE_NM = jsonArray.getJSONObject(i).getString("INDUTYPE_NM")
                stroe.REFINE_ROADNM_ADDR = jsonArray.getJSONObject(i).getString("REFINE_ROADNM_ADDR")
                stroe.REFINE_LOTNO_ADDR = jsonArray.getJSONObject(i).getString("REFINE_LOTNO_ADDR")
                stroe.TELNO = jsonArray.getJSONObject(i).getString("TELNO")
                stroe.REFINE_ZIPNO = jsonArray.getJSONObject(i).getString("REFINE_ZIPNO")
                stroe.REFINE_WGS84_LAT = jsonArray.getJSONObject(i).getString("REFINE_WGS84_LAT")
                stroe.REFINE_WGS84_LOGT = jsonArray.getJSONObject(i).getString("REFINE_WGS84_LOGT")
                stroe.DATA_STD_DE = jsonArray.getJSONObject(i).getString("DATA_STD_DE")
                storeArray.add(stroe)
            }

            runOnUiThread { intro_loading_text.text = "거리 데이터 계산중" }
            val sortedStore = createDistance(storeArray)

            runOnUiThread { intro_loading_text.text = "데이터베이스 생성중" }
            dbHandler!!.bulkInsert(sortedStore)

            runOnUiThread { intro_loading_text.text = "앱 실행 준비중" }
            startActivity(Intent(this,MainActivity::class.java))
            finish()
        }
    }

    private fun getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
        } else{
            initAppData()
        }
    }

    private fun getLatLng() : Location? {
        var currentLatLng: Location? = null
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
        } else{
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            currentLatLng = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        return currentLatLng
    }

    private fun createDistance(arrayStore:ArrayList<Store>): ArrayList<Store>{
        var resultArrayStore = arrayStore

        val userLocation = getLatLng()
        val myLat = userLocation?.latitude
        val myLong = userLocation?.longitude
        if(userLocation==null) return resultArrayStore

        for (i in 0 until resultArrayStore.size){
            val lat = resultArrayStore.get(i).REFINE_WGS84_LAT
            val long = resultArrayStore.get(i).REFINE_WGS84_LOGT
            if(lat!="" || long!="")
                resultArrayStore.get(i).DISTANCE = (Math.acos(Math.cos(Math.toRadians( 90- myLat!!)) *
                        Math.cos(Math.toRadians( 90 - lat!!.toDouble())) +
                        Math.sin(Math.toRadians( 90 - myLat!! )) *
                        Math.sin(Math.toRadians( 90- lat!!.toDouble())) * Math.cos(Math.toRadians(myLong!! - long!!.toDouble())))* 6378.137 * 1000).toLong()
        }
        return resultArrayStore
    }

    override fun onRequestPermissionsResult( requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            1000->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) initAppData()
                else finish()
            }
        }
    }
}
