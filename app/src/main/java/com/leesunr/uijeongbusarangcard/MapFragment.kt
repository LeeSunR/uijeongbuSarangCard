package com.leesunr.uijeongbusarangcard

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions


class MapFragment : Fragment(), OnMapReadyCallback{

    var mapView: MapView? = null
    var mContext: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        var layout:View = inflater.inflate(R.layout.fragment_map, container, false)
        // Inflate the layout for this fragment
        mapView = layout?.findViewById(R.id.map_view) as MapView
        mapView?.onCreate(savedInstanceState);
        mapView?.onResume();
        mapView?.getMapAsync(this); // 비동기적 방식으로 구글 맵 실행
        return layout
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val customLocation = CustomLocation(mContext!!).getLatLng(CustomLocation.REQUEST_CODE_MAP_LOCATION_UPDATE)
        val mylat = customLocation?.latitude
        val mylong = customLocation?.longitude


        var myLocation1 = LatLng(mylat!!, mylong!!)

        googleMap?.moveCamera(CameraUpdateFactory.zoomTo(17.5f))
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(myLocation1))



        googleMap?.setOnCameraIdleListener(object :GoogleMap.OnCameraIdleListener{
            override fun onCameraIdle() {
                googleMap.clear()
                if(googleMap.cameraPosition.zoom>16){
                    val location = googleMap.cameraPosition.target
                    val arrayStore = getSelectStore("REFINE_WGS84_LAT > ${location.latitude-0.005} AND REFINE_WGS84_LAT < ${location.latitude+0.005} AND REFINE_WGS84_LOGT > ${location.longitude-0.005} AND REFINE_WGS84_LOGT < ${location.longitude+0.005}")
                    for (i in 0 until arrayStore.size){
                        val storeLoation = LatLng(arrayStore[i].REFINE_WGS84_LAT!!.toDouble(),arrayStore[i].REFINE_WGS84_LOGT!!.toDouble())
                        var marker = MarkerOptions().position(storeLoation).title(arrayStore[i].CMPNM_NM).snippet(i.toString())
                        googleMap?.addMarker(marker)

                        googleMap.setOnMarkerClickListener(object :GoogleMap.OnMarkerClickListener{
                            override fun onMarkerClick(marker: Marker?): Boolean {
                                var menu = arrayOf("전화하기","지보보기","길찾기")
                                var builder = AlertDialog.Builder(mContext)
                                builder.setTitle(marker?.title)
                                builder.setItems(menu, object : DialogInterface.OnClickListener{
                                    override fun onClick(dialog: DialogInterface?, pos: Int) {
                                        val customLocation = CustomLocation(mContext!!).getLatLng(CustomLocation.REQUEST_CODE_MAP_LOCATION_UPDATE)
                                        val myLat = customLocation?.latitude
                                        val myLong = customLocation?.longitude
                                        val name = arrayStore?.get(marker?.snippet!!.toInt())?.CMPNM_NM
                                        val dLat = arrayStore?.get(marker?.snippet!!.toInt())?.REFINE_WGS84_LAT
                                        val dLong = arrayStore?.get(marker?.snippet!!.toInt())?.REFINE_WGS84_LOGT
                                        val telno = arrayStore?.get(marker?.snippet!!.toInt())?.TELNO

                                        when(pos){
                                            0->{
                                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$telno"))
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
//                                            3->{
//                                                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("nmap://search?query=$name"))
//                                                val list = mContext?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//                                                if (list == null || list.isEmpty()) startActivity(
//                                                    Intent(
//                                                        Intent.ACTION_VIEW,
//                                                        Uri.parse("market://details?id=com.nhn.android.nmap"))
//                                                )
//                                                else startActivity(intent)
//                                            }
//                                            4->{
//                                                var intent = Intent(Intent.ACTION_VIEW, Uri.parse("nmap://route/walk?slat=$myLat&slng=$myLong&sname=현재위치&dlat=$dLat&dlng=$dLong&dname=$name"))
//                                                val list = mContext?.packageManager?.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
//                                                if (list == null || list.isEmpty()) startActivity(
//                                                    Intent(
//                                                        Intent.ACTION_VIEW,
//                                                        Uri.parse("market://details?id=com.nhn.android.nmap"))
//                                                )
//                                                else startActivity(intent)
//                                            }
                                        }
                                    }
                                })
                                builder.create().show()

                                return true
                            }
                        })
                    }
                }
            }
        })
    }

    private fun getSelectStore(query: String): ArrayList<Store>{
        val dbHandler = DatabaseHelper(mContext!!)
        val arrayStore = dbHandler!!.getStore(query)
        return arrayStore
    }

}
