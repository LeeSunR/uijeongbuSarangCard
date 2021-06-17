package com.leesunr.uijeongbusarangcard.main

import android.content.Context
import com.leesunr.uijeongbusarangcard.base.BasePresenter
import com.leesunr.uijeongbusarangcard.base.BaseView
import com.leesunr.uijeongbusarangcard.data.entity.Store
import com.naver.maps.geometry.LatLng
import com.naver.maps.geometry.LatLngBounds
import io.reactivex.rxjava3.core.Single
import org.json.JSONArray
import org.json.JSONObject

interface MainConstants : BaseView {
    interface View:BaseView{
        fun addMaker(store: Store)
        fun clearMaker()
        fun showStoreList(store:List<Store>)
        fun getZoom():Double
        fun getBounds():LatLngBounds
        fun showMessage(text:String)
        fun hideMessage()
        fun printSearchWord(text:String)
        fun showResetButton()
        fun hideResetButton()
        fun showHighlightMarker(store: Store)
        fun hideHighlightMarker()
        fun showMyLocationOverlay()
        fun isShowHighLightMaker():Boolean
        fun showUI()
        fun showHighLightUI()
        fun exitApp()
        fun getHighlightMakerPosition():LatLng
        fun showMapAppDialog()
        fun changeHighlightStoreName(text:String)
        fun changeHighlightAddress(text:String)
        fun changeHighlightCategory(text:String)
    }

    interface Presenter : BasePresenter {
        fun changeCategory(main:String?,sub:String?)
        fun initCategory()
        fun getCategoryList(): Single<JSONObject>
        fun changeSearchWord(word:String)
        fun loadStore()
        fun reset()
        fun clickedStoreListItem(store:Store)
        fun pressedBackButton()
        fun clickNavigation()
    }
}


