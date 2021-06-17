package com.leesunr.uijeongbusarangcard.main

import android.content.Context
import com.leesunr.uijeongbusarangcard.base.AbstractPresenter
import com.leesunr.uijeongbusarangcard.data.AppRepository
import com.leesunr.uijeongbusarangcard.data.entity.Store
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.SingleSubject
import org.json.JSONArray
import org.json.JSONObject

class MainPresenter(
    private val view: MainConstants.View, context: Context
) : MainConstants.Presenter, AbstractPresenter() {

    private val model = MainModel.getInstance()
    private val repository = AppRepository(context)

    override fun changeCategory(main: String?, sub: String?) {
        model.mainCategory = main
        model.subCategory = sub
        loadStore()
    }

    override fun initCategory() {
        model.mainCategory = null
        model.subCategory = null
    }

    override fun getCategoryList(): Single<JSONObject> {
        val singleSubject = SingleSubject.create<JSONObject>()
        repository.selectCategory()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                val json = JSONObject()
                json.put("전체",JSONArray())
                it.forEach {
                    if(it.mainCategory.isNotEmpty()){
                        if(json.isNull(it.mainCategory)){
                            json.put(it.mainCategory,JSONArray())
                        }
                        json.getJSONArray(it.mainCategory).put(it.subCategory)
                    }
                }
                singleSubject.onSuccess(json)
            },{
                singleSubject.onError(it)
                it.printStackTrace()
            })

        return singleSubject
    }

    override fun changeSearchWord(word: String) {
        model.searchWord=word
        view.printSearchWord(word)
        loadStore()
    }

    override fun loadStore() {
        if(view.getZoom()>12.5){
            val latLngBounds = view.getBounds()
            repository.getStore(latLngBounds, model.mainCategory, model.subCategory, model.searchWord)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view.clearMaker()
                    it.forEach { store ->
                        view.addMaker(store)
                    }
                    view.showStoreList(it)

                    if(it.isEmpty()) view.showMessage("검색 결과가 없습니다")
                    else view.hideMessage()

                    resetCheck()
                },{

                }).addTo(disposables)
        }
        else {
            view.clearMaker()
            view.showStoreList(listOf())
            view.showMessage("지도를 축소해 주십시오")
        }
    }

    override fun reset() {
        model.searchWord = ""
        model.subCategory = null
        model.mainCategory = null
        view.printSearchWord(model.searchWord)
        loadStore()
    }

    override fun clickedStoreListItem(store: Store) {
        view.clearMaker()
        view.hideMessage()
        view.changeHighlightStoreName(store.name)
        view.changeHighlightAddress(store.address)
        view.changeHighlightCategory(store.category)
        view.showHighlightMarker(store)
        view.showHighLightUI()
    }

    override fun pressedBackButton() {
        if(view.isShowHighLightMaker()){
            view.showUI()
            view.hideHighlightMarker()
            this.loadStore()
        }
        else
            view.exitApp()
    }

    override fun clickNavigation() {
        view.showMapAppDialog()
    }

    private fun resetCheck(){
        if(model.mainCategory==null && model.subCategory==null && model.searchWord=="")
            view.hideResetButton()
        else
            view.showResetButton()
    }

}