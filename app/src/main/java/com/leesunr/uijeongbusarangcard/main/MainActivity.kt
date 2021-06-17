package com.leesunr.uijeongbusarangcard.main

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.UiThread
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.leesunr.uijeongbusarangcard.R
import com.leesunr.uijeongbusarangcard.SearchActivity
import com.leesunr.uijeongbusarangcard.base.AbstractView
import com.leesunr.uijeongbusarangcard.common.Constant
import com.leesunr.uijeongbusarangcard.common.Constant.REQUEST_CODE.LOCATION_PERMISSION_REQUEST_CODE
import com.leesunr.uijeongbusarangcard.common.Utils
import com.leesunr.uijeongbusarangcard.data.entity.Store
import com.leesunr.uijeongbusarangcard.main.adapter.StoreAdapter
import com.leesunr.uijeongbusarangcard.main.dialog.CategoryDialog
import com.leesunr.uijeongbusarangcard.main.dialog.SelectMapAppDialog
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import com.naver.maps.map.*
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.util.FusedLocationSource
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AbstractView(), OnMapReadyCallback, MainConstants.View {

    private val mainPresenter by lazy { MainPresenter(this,this) }
    private val markers = ArrayList<Marker>()
    private val highlightMarker = Marker().apply { iconTintColor = Color.BLUE }
    private var naverMap : NaverMap? = null
    private val storeAdapter = StoreAdapter()
    private val topPadding by lazy { Utils.dpToPx(this@MainActivity,120f) }
    private val bottomPadding by lazy { Utils.dpToPx(this@MainActivity,320f) }
    private lateinit var locationSource: FusedLocationSource

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)
        initUI()
        initMap()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (locationSource.onRequestPermissionsResult(requestCode, permissions,
                grantResults)) {
            if (!locationSource.isActivated) { // 권한 거부됨
                naverMap?.locationTrackingMode = LocationTrackingMode.None
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initUI(){
        mainPresenter.initCategory()
        text_category.setOnClickListener(onClickListener)
        text_search.setOnClickListener(onClickListener)
        btn_reset.setOnClickListener(onClickListener)
        layout_navigation.setOnClickListener(onClickListener)

        //리사이클러 뷰 초기화
        rcy_store.layoutManager = LinearLayoutManager(this)
        rcy_store.adapter = storeAdapter
        rcy_store.addItemDecoration(DividerItemDecoration(this,LinearLayoutManager.VERTICAL))
        storeAdapter.notifyDataSetChanged()
        storeAdapter.setItemClickListener { mainPresenter.clickedStoreListItem(it) }

        //
        layout_sliding_panel.addPanelSlideListener(panelSlideListener)
    }

    private val onClickListener = View.OnClickListener {
        when(it.id){
            R.id.text_category -> showCategoryDialog()
            R.id.text_search -> showSearchActivity()
            R.id.btn_reset -> mainPresenter.reset()
            R.id.layout_navigation -> mainPresenter.clickNavigation()
        }
    }

    private val panelSlideListener = object:SlidingUpPanelLayout.PanelSlideListener{
        override fun onPanelSlide(panel: View?, slideOffset: Float) {
        }

        override fun onPanelStateChanged(
            panel: View?,
            previousState: SlidingUpPanelLayout.PanelState?,
            newState: SlidingUpPanelLayout.PanelState?
        ) {
            Log.i(tag,"onPanelStateChanged")
            when(newState){
                SlidingUpPanelLayout.PanelState.EXPANDED -> {
                    naverMap?.setContentPadding(0,topPadding,0,bottomPadding)
                }
                SlidingUpPanelLayout.PanelState.COLLAPSED -> {
                    naverMap?.setContentPadding(0,topPadding,0,layout_sliding_panel.panelHeight)
                }
                SlidingUpPanelLayout.PanelState.HIDDEN -> {
                    naverMap?.setContentPadding(0,topPadding,0,layout_sliding_panel.panelHeight)
                }
                else -> {

                }
            }
        }
    }

    private fun initMap(){
        val fm = supportFragmentManager
        val mapFragment = fm.findFragmentById(R.id.map) as MapFragment?
            ?: MapFragment.newInstance().also {
                fm.beginTransaction().add(R.id.map, it).commit()
            }
        mapFragment.getMapAsync(this)
    }

    @UiThread
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap
        naverMap.locationSource = locationSource
        val cameraUpdate = CameraUpdate.scrollTo(LatLng(37.72, 127.05))
        naverMap.moveCamera(cameraUpdate)
        naverMap.minZoom = 11.5
        naverMap.maxZoom = 19.0
        naverMap.uiSettings.isLocationButtonEnabled = true
        naverMap.setContentPadding(0,topPadding,0,layout_sliding_panel.panelHeight)
        naverMap.extent = LatLngBounds(
            LatLng(37.68, 126.99),
            LatLng(37.78, 127.12)
        )
        naverMap.addOnCameraIdleListener(cameraIdleListener)
    }

    private val cameraIdleListener = NaverMap.OnCameraIdleListener{ mainPresenter.loadStore() }

    private fun showCategoryDialog() {
        mainPresenter.getCategoryList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                CategoryDialog(it).apply {
                    setCategorySelectedListener { s1, s2 ->
                        Log.e(tag,"$s1 / $s2")
                        mainPresenter.changeCategory(s1,s2)
                    }
                    show(supportFragmentManager,"CATEGORY")
                }
            },{

            })
    }

    override fun addMaker(store: Store) {
        val marker = Marker()
        marker.isHideCollidedMarkers = true
        marker.position = LatLng(store.latitude, store.longitude)
        marker.map = naverMap
        marker.width = 96
        marker.height = 128
        markers.add(marker)
    }

    override fun clearMaker() {
        markers.forEach {
            it.map = null
        }
        markers.clear()
    }

    override fun showStoreList(store: List<Store>) {
        if(store.isNullOrEmpty()){
            storeAdapter.items.clear()
            layout_sliding_panel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
        }
        else{
            storeAdapter.items.clear()
            store.forEach {
                storeAdapter.items.add(it)
            }
            storeAdapter.notifyDataSetChanged()
            if(layout_sliding_panel.panelState == SlidingUpPanelLayout.PanelState.HIDDEN){
                layout_sliding_panel.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
            }
        }
    }

    override fun getZoom() = naverMap!!.cameraPosition.zoom
    override fun getBounds() = naverMap!!.contentBounds

    override fun showMessage(text: String) {
        text_message.visibility = View.VISIBLE
        text_message.text = text
    }
    override fun hideMessage() { text_message.visibility = View.INVISIBLE }

    override fun printSearchWord(text: String) {
        if(text.isEmpty()){
            text_search.text = getString(R.string.searchHint)
            text_search.setTextColor(getColor(R.color.gray))
        }
        else{
            text_search.text = text
            text_search.setTextColor(getColor(R.color.black))
        }
    }

    override fun showResetButton() { btn_reset.visibility = View.VISIBLE }
    override fun hideResetButton() { btn_reset.visibility = View.GONE }
    override fun showHighlightMarker(store: Store) {
        naverMap?.removeOnCameraIdleListener(cameraIdleListener)
        naverMap?.moveCamera(CameraUpdate.scrollTo(LatLng(store.latitude,store.longitude)))
        highlightMarker.position = LatLng(store.latitude,store.longitude)
        highlightMarker.width = 96
        highlightMarker.height = 128
        highlightMarker.isHideCollidedMarkers = true
        highlightMarker.isForceShowIcon = true
        highlightMarker.zIndex = 2
        highlightMarker.map = naverMap
        layout_sliding_panel.panelState = SlidingUpPanelLayout.PanelState.HIDDEN
    }

    override fun hideHighlightMarker() {
        highlightMarker.map = null
        naverMap?.addOnCameraIdleListener(cameraIdleListener)
    }

    override fun showMyLocationOverlay() {
        val locationOverlay = naverMap?.locationOverlay
        locationOverlay?.isVisible = true
    }

    override fun isShowHighLightMaker() = highlightMarker.map != null

    override fun showUI() {
        layout_ui.visibility = View.VISIBLE
        layout_highlight_ui.visibility = View.GONE
    }

    override fun showHighLightUI() {
        layout_ui.visibility = View.GONE
        layout_highlight_ui.visibility = View.VISIBLE
    }

    override fun exitApp() = finish()
    override fun getHighlightMakerPosition(): LatLng = highlightMarker.position
    override fun showMapAppDialog() {
        SelectMapAppDialog(highlightMarker.position).show(supportFragmentManager,"MAPDIALOG")
    }

    override fun changeHighlightStoreName(text: String) { text_highlight_name.text = text }
    override fun changeHighlightAddress(text: String) { text_highlight_address.text = text }
    override fun changeHighlightCategory(text: String) { text_highlight_category.text = text }

    private fun showSearchActivity(){
        val intent = Intent(this,SearchActivity::class.java)
        startActivityForResult(intent,Constant.REQUEST_CODE.SEARCH_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode){
            Constant.REQUEST_CODE.SEARCH_REQUEST-> {
                if(resultCode==Constant.RESULT_CODE.OK){
                    val word = data?.getStringExtra("word").toString()
                    Log.d(tag,"activity result search word : $word")
                    mainPresenter.changeSearchWord(word)
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onBackPressed() = mainPresenter.pressedBackButton()


}