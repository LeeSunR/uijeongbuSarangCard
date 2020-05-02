package com.leesunr.uijeongbusarangcard

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.BaseAdapter
import android.widget.RadioGroup
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_list.*
import org.json.JSONArray
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.thread

class ListFragment : Fragment() {

    val REQUESTCODE_LOCATION_UPDATE = 1003
    val REQUESTCODE_LOCATION_CALL = 1001

    var mContext: Context? = null
    var dbHandler: DatabaseHelper? = null
    var arrayStore: ArrayList<Store>? = null
    var myLat: Double? = null
    var myLong: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dbHandler = DatabaseHelper(mContext!!)
        getLatLng(REQUESTCODE_LOCATION_CALL)
        myLocationUpdate()
        getAllStore()

        radio_grp_list_kind.setOnCheckedChangeListener(myRadioItemChangeListener(this))

        edit_text_list_search.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(textView: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                search()
                return true
            }
        })

        btn_list_search.setOnClickListener { search() }
        btn_location.setOnClickListener {
            var builder = AlertDialog.Builder(mContext)
            builder.setTitle("현재 위치를\n갱신하시겠습니까?")
            builder.setPositiveButton("예", object : DialogInterface.OnClickListener {
                override fun onClick(dialog: DialogInterface?, id: Int) {
                    getLatLng(REQUESTCODE_LOCATION_UPDATE)
                }
            })
            builder.setNegativeButton("아니요") { dialog, id -> }
            builder.create().show()
        }

        listview_store.setOnItemClickListener { adapterView, view, i, l ->
//            var menu = arrayOf("전화하기","지보보기(KAKAO)","길찾기(KAKAO)","지보보기(NAVER)","길찾기(NAVER)")
            var menu = arrayOf("전화하기","지보보기","길찾기")

            var builder = AlertDialog.Builder(mContext)
            builder.setTitle(arrayStore?.get(i)?.CMPNM_NM)
            builder.setItems(menu, object :DialogInterface.OnClickListener{
                override fun onClick(dialog: DialogInterface?, pos: Int) {
                    getLatLng(REQUESTCODE_LOCATION_CALL)
                    val name = arrayStore?.get(i)?.CMPNM_NM
                    val dLat = arrayStore?.get(i)?.REFINE_WGS84_LAT
                    val dLong = arrayStore?.get(i)?.REFINE_WGS84_LOGT
                    val telno = arrayStore?.get(i)?.TELNO

                    when(pos){
                        0->{
                            val intent = Intent(Intent.ACTION_DIAL,Uri.parse("tel:$telno"))
                            startActivity(intent)
                        }
                        1->{
                            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://search?q=$name&p=$dLat,$dLong"))
                            val list = mContext?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                            if (list == null || list.isEmpty()) {
                                var builder = AlertDialog.Builder(mContext).setTitle("안내").setMessage("이 기능은 카카오맵이 필요합니다\n앱 설치 화면으로 이동하시겠습니까?")
                                builder.setPositiveButton("동의", DialogInterface.OnClickListener { dialogInterface, i -> startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=net.daum.android.map"))) })
                                builder.setNegativeButton("거부", DialogInterface.OnClickListener { dialogInterface, i ->})
                                builder.show()
                            }
                            else startActivity(intent)
                        }
                        2->{
                            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://route?sp=$myLat,$myLong&ep=$dLat,$dLong&by=FOOT"))
                            val list = mContext?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
                            if (list == null || list.isEmpty()) {
                                var builder = AlertDialog.Builder(mContext).setTitle("안내").setMessage("이 기능은 카카오맵이 필요합니다\n앱 설치 화면으로 이동하시겠습니까?")
                                builder.setPositiveButton("동의", DialogInterface.OnClickListener { dialogInterface, i -> startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=net.daum.android.map"))) })
                                builder.setNegativeButton("거부", DialogInterface.OnClickListener { dialogInterface, i ->})
                                builder.show()
                            }
                            else startActivity(intent)
                        }
//                        3->{
//                            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("nmap://search?query=$name"))
//                            val list = mContext?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//                            if (list == null || list.isEmpty()) startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.nhn.android.nmap")))
//                            else startActivity(intent)
//                        }
//                        4->{
//                            var intent = Intent(Intent.ACTION_VIEW, Uri.parse("nmap://route/walk?slat=$myLat&slng=$myLong&sname=현재위치&dlat=$dLat&dlng=$dLong&dname=$name"))
//                            val list = mContext?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//                            if (list == null || list.isEmpty()) startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("market://details?id=com.nhn.android.nmap")))
//                            else startActivity(intent)
//                         }
                    }
                }
            })
            builder.create().show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    private fun initDatabase(){
        thread {
            var mainActivity = activity as MainActivity
            mainActivity.runOnUiThread { mainActivity?.loadingStart() }
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
            dbHandler!!.bulkInsert(createDistance(storeArray))
            mainActivity.runOnUiThread {
                radio_btn_all.performClick()
                mainActivity?.loadingDone()
            }
        }
    }

    private fun search(){
        radio_grp_list_kind.setOnCheckedChangeListener(null)
        radio_grp_list_kind.clearCheck()
        radio_grp_list_kind.setOnCheckedChangeListener(myRadioItemChangeListener(this))
        edit_text_list_search?.clearFocus()
        val imm = mContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken,0)
        getSelectStore("CMPNM_NM LIKE '%${edit_text_list_search?.text.toString()}%'")
        edit_text_list_search?.setText("")
    }

    private fun getAllStore(){
        thread {
            var mainActivity = activity as MainActivity
            mainActivity.runOnUiThread {
                mainActivity?.loadingStart()
            }
            arrayStore = dbHandler!!.allStore
            mainActivity.runOnUiThread {
                tv_list_result.text = "검색결과 : ${arrayStore?.size}건"
                listview_store.adapter = StoreListAdapter(mContext!!,arrayStore!!)
                mainActivity.loadingDone()
            }
        }
    }

    private fun getSelectStore(query: String){
        arrayStore = dbHandler!!.getStore("$query ORDER BY CAST(DISTANCE AS INTEGER)")
        tv_list_result.text = "검색결과 : ${arrayStore?.size}건"
        listview_store.adapter = StoreListAdapter(mContext!!,arrayStore!!)
    }

    private fun myLocationUpdate(){
        edit_text_list_search.setText("")
        edit_text_list_search.clearFocus()
        val imm = mContext?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken,0)
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



    private fun getLatLng(code: Int) {
        if (ActivityCompat.checkSelfPermission(mContext!!, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),code) // 권한 요청
        } else{
            val locationManager = mContext!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val currentLatLng = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            this.myLat = currentLatLng.latitude
            this.myLong = currentLatLng.longitude
            if(code==REQUESTCODE_LOCATION_UPDATE){
                var mainActivity = activity as MainActivity
                mainActivity?.runOnUiThread {
                    myLocationUpdate()
                    initDatabase()
                }
            }
        }
    }

    private fun createDistance(arrayStore:ArrayList<Store>): ArrayList<Store>{
        var resultArrayStore = arrayStore

        val myLat = this.myLat
        val myLong = this.myLong
        if(myLat==null || myLong==null) return resultArrayStore

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

        Log.e("rr","set")
        when(requestCode){
            REQUESTCODE_LOCATION_CALL->{
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    getLatLng(REQUESTCODE_LOCATION_CALL)
            }
            REQUESTCODE_LOCATION_UPDATE->{
                Log.e("rr","ready")
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.e("rr","run")
                    getLatLng(REQUESTCODE_LOCATION_CALL)
                    var mainActivity = activity as MainActivity
                    mainActivity?.runOnUiThread {
                        myLocationUpdate()
                        initDatabase()
                    }
                }
            }
        }
    }

    class myRadioItemChangeListener(listFragment: ListFragment?):RadioGroup.OnCheckedChangeListener{
        var listFragment: ListFragment? = null
        init {
            this.listFragment = listFragment
        }

        override fun onCheckedChanged(radioGroup: RadioGroup?, i: Int) {
            when(radioGroup?.checkedRadioButtonId){
                R.id.radio_btn_all-> listFragment?.getAllStore()
                R.id.radio_btn_restaurant-> listFragment?.getSelectStore("INDUTYPE_NM LIKE '%음식%'")
                R.id.radio_btn_mart-> listFragment?.getSelectStore("INDUTYPE_NM LIKE '%슈퍼마켓%'")
                R.id.radio_btn_convenience-> listFragment?.getSelectStore("INDUTYPE_NM LIKE '%편 의 점%'")
                R.id.radio_btn_hospital-> listFragment?.getSelectStore("INDUTYPE_NM LIKE '%의원%'")
                R.id.radio_btn_drugstore-> listFragment?.getSelectStore("INDUTYPE_NM LIKE '%약국%'")
                R.id.radio_btn_gas_station-> listFragment?.getSelectStore("INDUTYPE_NM LIKE '%연료판매점%'")
                R.id.radio_btn_hotel-> listFragment?.getSelectStore("INDUTYPE_NM LIKE '%숙박%'")
                R.id.radio_btn_culture-> listFragment?.getSelectStore("INDUTYPE_NM LIKE '%문화.취미%'")
                R.id.radio_btn_beauty-> listFragment?.getSelectStore("INDUTYPE_NM LIKE '%보건위생%'")
                R.id.radio_btn_leisure-> listFragment?.getSelectStore("INDUTYPE_NM LIKE '%레저%'")
            }
        }
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
            viewHolder.lat = view.findViewById(R.id.list_item_lat) as TextView
            viewHolder.long = view.findViewById(R.id.list_item_long) as TextView


            view.tag = viewHolder

            viewHolder.name.text = mItem.get(position).CMPNM_NM
            viewHolder.old_addr.text = mItem.get(position).REFINE_ROADNM_ADDR
            viewHolder.new_addr.text = mItem.get(position).REFINE_LOTNO_ADDR
            viewHolder.kind.text = mItem.get(position).INDUTYPE_NM
            viewHolder.lat.text = mItem.get(position).REFINE_WGS84_LAT
            viewHolder.long.text = mItem.get(position).REFINE_WGS84_LOGT
            if(mItem.get(position).DISTANCE!!>=99999) viewHolder.distance.text = ""
            else viewHolder.distance.text = mItem.get(position).DISTANCE.toString()+"M"
            return view
        }else{
            viewHolder = view.tag as ViewHolder
        }
        viewHolder.name.text = mItem.get(position).CMPNM_NM
        viewHolder.old_addr.text = mItem.get(position).REFINE_ROADNM_ADDR
        viewHolder.new_addr.text = mItem.get(position).REFINE_LOTNO_ADDR
        viewHolder.kind.text = mItem.get(position).INDUTYPE_NM
        viewHolder.lat.text = mItem.get(position).REFINE_WGS84_LAT
        viewHolder.long.text = mItem.get(position).REFINE_WGS84_LOGT
        if(mItem.get(position).DISTANCE!!>=99999) viewHolder.distance.text = ""
        else viewHolder.distance.text = mItem.get(position).DISTANCE.toString()+"M"
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
        lateinit var lat : TextView
        lateinit var long : TextView
    }

}