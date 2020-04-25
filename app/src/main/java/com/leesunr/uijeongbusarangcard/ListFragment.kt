package com.leesunr.uijeongbusarangcard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_intro.*
import kotlinx.android.synthetic.main.fragment_list.*
import org.json.JSONArray
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList


class ListFragment : Fragment() {

    var mContext: Context? = null
    var dbHandler: DatabaseHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dbHandler = DatabaseHelper(mContext!!)
        myLocationUpdate()
        getAllStore()
        radio_grp_list_kind.setOnCheckedChangeListener { radioGroup, i ->
            when(radioGroup.checkedRadioButtonId){
                R.id.radio_btn_all-> getAllStore()
                R.id.radio_btn_restaurant-> getSelectStore("INDUTYPE_NM LIKE '%음식%'")
                R.id.radio_btn_mart-> getSelectStore("INDUTYPE_NM LIKE '%슈퍼마켓%'")
                R.id.radio_btn_convenience-> getSelectStore("INDUTYPE_NM LIKE '%편 의 점%'")
                R.id.radio_btn_hospital-> getSelectStore("INDUTYPE_NM LIKE '%의원%'")
                R.id.radio_btn_drugstore-> getSelectStore("INDUTYPE_NM LIKE '%약국%'")
                R.id.radio_btn_gas_station-> getSelectStore("INDUTYPE_NM LIKE '%연료판매점%'")
                R.id.radio_btn_hotel-> getSelectStore("INDUTYPE_NM LIKE '%숙박%'")
                R.id.radio_btn_culture-> getSelectStore("INDUTYPE_NM LIKE '%문화.취미%'")
                R.id.radio_btn_beauty-> getSelectStore("INDUTYPE_NM LIKE '%보건위생%'")
                R.id.radio_btn_leisure-> getSelectStore("INDUTYPE_NM LIKE '%레저%'")
            }
        }

        edit_text_list_search.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(textView: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                textView?.clearFocus()
                val imm = mContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(view?.windowToken,0)
                getSelectStore("CMPNM_NM LIKE '%${textView?.text.toString()}%'")
                return true
            }
        })

        btn_list_search.setOnClickListener {
            edit_text_list_search?.clearFocus()
            val imm = mContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view?.windowToken,0)
            getSelectStore("CMPNM_NM LIKE '%${edit_text_list_search?.text.toString()}%'")
        }

        btn_location.setOnClickListener {
            dbHandler = DatabaseHelper(mContext!!)
            dbHandler!!.deleteAll()
            val assetManager = resources.assets
            val inputStream= assetManager.open("data.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonArray: JSONArray = JSONArray(jsonString)
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
            dbHandler!!.bulkInsert(sortByDistance(storeArray))
            myLocationUpdate()
            getAllStore()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun getAllStore(){
        val store = dbHandler!!.allStore
        tv_list_result.text = "검색결과 : ${store.size}건"
        listview_store.adapter = StoreListAdapter(mContext!!,store)
    }

    private fun getSelectStore(query: String){
        val store = dbHandler!!.getStore(query)
        tv_list_result.text = "검색결과 : ${store.size}건"
        listview_store.adapter = StoreListAdapter(mContext!!,store)
    }

    private fun myLocationUpdate(){
        val userLocation = getLatLng()
        val myLat = userLocation?.latitude
        val myLong = userLocation?.longitude

        getAllStore()
        edit_text_list_search.setText("")
        edit_text_list_search.clearFocus()
        val imm = mContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken,0)
        radio_btn_all.performClick()

        val address = convertToAddr(myLat, myLong)
        tv_list_address.text = address
    }

    private fun convertToAddr(lat:Double?, long:Double?): String{
        var result:String ="주소를 찾을수 없습니다"
        if(lat!=null || long!=null){
            var mResultList : List<Address>? = null
            try {
                mResultList = Geocoder(mContext, Locale.KOREAN).getFromLocation(lat!!, long!!, 1)
            } finally {
                if (mResultList!=null) result = mResultList[0].getAddressLine(0)
            }
        }
        return result
    }

    private fun getLatLng() : Location? {
        var currentLatLng: Location? = null
        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(mContext as Activity, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),1000) // 권한 요청
            if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return null
            } else {
                val locationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                currentLatLng = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            }
        } else{
            val locationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            currentLatLng = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        }
        return currentLatLng
    }

    private fun sortByDistance(arrayStore:ArrayList<Store>): ArrayList<Store>{
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

        for (i in 0 until resultArrayStore.size){
            for (j in i+1 until resultArrayStore.size){
                if(resultArrayStore.get(j).DISTANCE!=null){
                    if(resultArrayStore.get(i).DISTANCE==null || resultArrayStore.get(j).DISTANCE!! < resultArrayStore.get(i).DISTANCE!!){
                        var temp:Store = resultArrayStore.get(j)
                        resultArrayStore.set(j,resultArrayStore.get(i))
                        resultArrayStore.set(i,temp)
                    }
                }
            }
        }

        return resultArrayStore
    }
}

class StoreListAdapter(context: Context, item: ArrayList<Store>) : BaseAdapter(){
    private val mContext = context
    private val mItem = item
    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var view = convertView
        lateinit var viewHolder : ViewHolder

        if (view == null){
            viewHolder = ViewHolder()
            view = LayoutInflater.from(mContext).inflate(R.layout.listview_item,parent,false)
            viewHolder.name = view.findViewById(R.id.list_item_store_name) as TextView
            viewHolder.old_addr = view.findViewById(R.id.list_item_old_addr) as TextView
            viewHolder.new_addr = view.findViewById(R.id.list_item_new_addr) as TextView
            viewHolder.kind = view.findViewById(R.id.list_item_kind) as TextView
            viewHolder.distance = view.findViewById(R.id.list_item_distance) as TextView

            view.tag = viewHolder

            viewHolder.name.text = mItem.get(position).CMPNM_NM
            viewHolder.old_addr.text = mItem.get(position).REFINE_ROADNM_ADDR
            viewHolder.new_addr.text = mItem.get(position).REFINE_LOTNO_ADDR
            viewHolder.kind.text = mItem.get(position).INDUTYPE_NM
            viewHolder.distance.text = mItem.get(position).DISTANCE.toString()+"M"
            return view
        }else{
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.name.text = mItem.get(position).CMPNM_NM
        viewHolder.old_addr.text = mItem.get(position).REFINE_ROADNM_ADDR
        viewHolder.new_addr.text = mItem.get(position).REFINE_LOTNO_ADDR
        viewHolder.kind.text = mItem.get(position).INDUTYPE_NM
        viewHolder.distance.text = mItem.get(position).DISTANCE.toString()+"M"
        return  view
    }
    override fun getItem(position: Int) = mItem[position]
    override fun getItemId(position: Int) = position.toLong()
    override fun getCount() = mItem.size
    inner class ViewHolder{
        lateinit var name : TextView
        lateinit var old_addr : TextView
        lateinit var new_addr : TextView
        lateinit var kind : TextView
        lateinit var distance : TextView
    }

}