package com.leesunr.uijeongbusarangcard.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.leesunr.uijeongbusarangcard.data.entity.Store
import com.naver.maps.geometry.LatLngBounds
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

@Dao
interface StoreDao {
    @Query("SELECT * FROM store")
    fun selectAll(): Single<List<Store>>

    @Query("SELECT * FROM store GROUP BY main_category, sub_category")
    fun selectCategory(): Single<List<Store>>

    @Query("SELECT * FROM store WHERE (main_category LIKE :main) AND (sub_category LIKE :sub) AND (name LIKE :searchWord) AND (latitude BETWEEN :southWestLatitude AND :northEastLatitude) AND (longitude BETWEEN :southWestLongitude AND :northEastLongitude)")
    fun getStore(southWestLatitude:Double,southWestLongitude :Double,northEastLatitude:Double,northEastLongitude:Double, main:String, sub:String, searchWord:String): Single<List<Store>>

    @Query("SELECT * FROM store WHERE (main_category LIKE :main) AND (name LIKE :searchWord) AND (latitude BETWEEN :southWestLatitude AND :northEastLatitude) AND (longitude BETWEEN :southWestLongitude AND :northEastLongitude)")
    fun getStore(southWestLatitude:Double,southWestLongitude :Double,northEastLatitude:Double,northEastLongitude:Double, main:String, searchWord:String): Single<List<Store>>

    @Query("SELECT * FROM store WHERE (name LIKE :searchWord) AND (latitude BETWEEN :southWestLatitude AND :northEastLatitude) AND (longitude BETWEEN :southWestLongitude AND :northEastLongitude)")
    fun getStore(southWestLatitude:Double,southWestLongitude :Double,northEastLatitude:Double,northEastLongitude:Double,searchWord:String): Single<List<Store>>

    @Insert
    fun insertAll(stores: List<Store>) : Completable

    @Delete
    fun delete(user: Store) : Completable

    @Query("DELETE FROM store")
    fun deleteAll() : Completable
}