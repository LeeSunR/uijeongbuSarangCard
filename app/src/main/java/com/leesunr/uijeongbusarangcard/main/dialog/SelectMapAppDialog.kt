package com.leesunr.uijeongbusarangcard.main.dialog

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.leesunr.uijeongbusarangcard.R
import com.leesunr.uijeongbusarangcard.main.dialog.adapter.Category
import com.leesunr.uijeongbusarangcard.main.dialog.adapter.CategoryAdapter
import com.naver.maps.geometry.LatLng
import kotlinx.android.synthetic.main.dialog_category.view.*
import kotlinx.android.synthetic.main.dialog_select_map_app.view.*
import org.json.JSONObject
import java.lang.Exception
import java.lang.reflect.InvocationTargetException

class SelectMapAppDialog(latLng:LatLng) : DialogFragment() {

    var googleMaps: PackageInfo? = null
    var naverMaps: PackageInfo? = null
    var kakaoMaps: PackageInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        isCancelable = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_select_map_app, container)
        val pm = context?.packageManager
        try {
            googleMaps = pm?.getPackageInfo("com.google.android.apps.maps", PackageManager.GET_ACTIVITIES)
            naverMaps = pm?.getPackageInfo("com.nhn.android.nmap", PackageManager.GET_ACTIVITIES)
            kakaoMaps = pm?.getPackageInfo("net.daum.android.map", PackageManager.GET_ACTIVITIES)
        } catch (e:Exception){

        }
        if(googleMaps!=null) {
            view.text_google_maps_package_info.text = context?.getString(R.string.installed)
            view.text_google_maps_package_info.setTextColor(context!!.getColor(R.color.green))
        }
        if(naverMaps!=null) {
            view.text_naver_maps_package_info.text = context?.getString(R.string.installed)
            view.text_naver_maps_package_info.setTextColor(context!!.getColor(R.color.green))
        }
        if(kakaoMaps!=null) {
            view.text_kakao_maps_package_info.text = context?.getString(R.string.installed)
            view.text_kakao_maps_package_info.setTextColor(context!!.getColor(R.color.green))
        }

        view.layout_google_maps.setOnClickListener(startGoogleMap)
        view.layout_naver_maps.setOnClickListener(startNaverMap)
        view.layout_kakao_maps.setOnClickListener(startKakaoMap)

        return view
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }
    private val startGoogleMap = View.OnClickListener {
        //패키지 없음
        if(googleMaps==null){
            AlertDialog.Builder(context).apply {
                setTitle(R.string.information)
                setMessage(R.string.startPlayStore)
                setPositiveButton(R.string.btn_yes) { _,_->
                    val uri = Uri.parse("market://details?id=com.google.android.apps.maps")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                    dismiss()
                }
                setNegativeButton(R.string.btn_no,null)
                show()
            }
        }
        //패키지 있음
        else{
            AlertDialog.Builder(context).apply {
                setTitle(R.string.information)
                setMessage(R.string.startApp)
                setPositiveButton(R.string.btn_yes) { _,_ ->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=${latLng.latitude},${latLng.longitude}"))
                    startActivity(intent)
                    dismiss()
                }
                setNegativeButton(R.string.btn_no,null)
                show()
            }
        }
    }

    private val startNaverMap = View.OnClickListener {
    //패키지 없음
        if(naverMaps==null){
            AlertDialog.Builder(context).apply {
                setTitle(R.string.information)
                setMessage(R.string.startPlayStore)
                setPositiveButton(R.string.btn_yes) { _,_->
                    val uri = Uri.parse("market://details?id=com.nhn.android.nmap")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                    dismiss()
                }
                setNegativeButton(R.string.btn_no,null)
                show()
            }
        }
        //패키지 있음
        else{
            AlertDialog.Builder(context).apply {
                setTitle(R.string.information)
                setMessage(R.string.startApp)
                setPositiveButton(R.string.btn_yes) { _,_->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("navermaps://?menu=location&pinType=place&lat=${latLng.latitude}&lng=${latLng.longitude}"))
                    startActivity(intent)
                    dismiss()
                }
                setNegativeButton(R.string.btn_no,null)
                show()
            }
        }
    }

    private val startKakaoMap = View.OnClickListener {
        if(kakaoMaps==null){
            AlertDialog.Builder(context).apply {
                setTitle(R.string.information)
                setMessage(R.string.startPlayStore)
                setPositiveButton(R.string.btn_yes) { _,_->
                    val uri = Uri.parse("market://details?id=net.daum.android.map")
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                    dismiss()
                }
                setNegativeButton(R.string.btn_no,null)
                show()
            }
        }
        //패키지 있음
        else{
            AlertDialog.Builder(context).apply {
                setTitle(R.string.information)
                setMessage(R.string.startApp)
                setPositiveButton(R.string.btn_yes) { _,_->
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse("kakaomap://route?ep=${latLng.latitude},${latLng.longitude}&by=FOOT"))
                    startActivity(intent)
                    dismiss()
                }
                setNegativeButton(R.string.btn_no,null)
                show()
            }
        }
    }

}