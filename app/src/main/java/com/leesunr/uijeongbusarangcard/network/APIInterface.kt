package com.leesunr.uijeongbusarangcard.network

import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET

interface APIInterface {
    @GET("card/uijeongbu/download")
    fun getAllLatestData(): Single<Response.AllLatestData>

    @GET("card/uijeongbu/version")
    fun getVersion(): Single<Response.Version>
}