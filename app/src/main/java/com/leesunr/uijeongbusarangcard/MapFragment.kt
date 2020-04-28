package com.leesunr.uijeongbusarangcard

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_map.*

class MapFragment : Fragment(), OnMapReadyCallback {

    var mContext: Context? = null
    var mapView: MapView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapView?.onCreate(savedInstanceState)
    }

    override fun onStart() {
        mapView?.onStart()
        super.onStart()
    }

    override fun onResume() {
        mapView?.onResume()
        super.onResume()
    }

    override fun onPause() {
        mapView?.onPause()
        super.onPause()
    }

    override fun onStop() {
        mapView?.onStop()
        super.onStop()
    }

    override fun onDestroy() {
        mapView?.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapView?.onLowMemory()
        super.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        Log.e("dd","ff")
        val seoul = LatLng(37.566, 126.978)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(seoul))
        googleMap?.moveCamera(CameraUpdateFactory.zoomTo(10f))
        val marker =
            MarkerOptions()
                .position(seoul)
                .title("서울")
                .snippet("아름다운 도시")
        googleMap?.addMarker(marker)
    }
}
