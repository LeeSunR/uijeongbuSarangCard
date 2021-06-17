package com.leesunr.uijeongbusarangcard.data

import android.app.Application
import android.content.Context
import com.leesunr.uijeongbusarangcard.data.dao.StoreDao
import com.leesunr.uijeongbusarangcard.data.entity.Store
import com.naver.maps.geometry.LatLngBounds
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

class AppRepository(context: Context) {
    private var storeDao: StoreDao
    private var sharedPreferences: SharedPreferenceManager

    init {
        val database = AppDatabase.getInstance(context)!!
        sharedPreferences = SharedPreferenceManager(context)
        storeDao = database.storeDao()
    }

    var dataVersion: Int
        get() = sharedPreferences.dataVersion
        set(value) {
            sharedPreferences.dataVersion = value
        }

    fun getStore(
        latLngBounds: LatLngBounds, main:String?, sub:String?, searchWord:String
    ): Single<List<Store>> {
        if(main.isNullOrEmpty() && sub.isNullOrEmpty())
            return storeDao.getStore(latLngBounds.southLatitude,latLngBounds.westLongitude,latLngBounds.northLatitude,latLngBounds.eastLongitude,"%$searchWord%")
        else if(main!!.isNotEmpty()&&sub.isNullOrEmpty())
            return storeDao.getStore(latLngBounds.southLatitude,latLngBounds.westLongitude,latLngBounds.northLatitude,latLngBounds.eastLongitude, main,"%$searchWord%")
       else
            return storeDao.getStore(latLngBounds.southLatitude,latLngBounds.westLongitude,latLngBounds.northLatitude,latLngBounds.eastLongitude, main, sub!!,"%$searchWord%")
    }

    fun selectCategory(): Single<List<Store>> {
        return storeDao.selectCategory()
    }

    fun getAllStore(): Single<List<Store>> {
        return storeDao.selectAll()
    }

    fun insertStore(entity: List<Store>): Completable {
        return storeDao.insertAll(entity)
    }

    fun deleteStore(entity: Store): Completable {
        return storeDao.delete(entity)
    }

    fun deleteAllStore(): Completable {
        return storeDao.deleteAll()
    }
}