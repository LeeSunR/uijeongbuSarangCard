package com.leesunr.uijeongbusarangcard.network

import com.google.gson.annotations.SerializedName
import com.leesunr.uijeongbusarangcard.data.entity.Store

object Response {
    open class Base(
        @SerializedName("result") val result:Boolean = false
    )

    class AllLatestData(
        @SerializedName("data") val data:List<Store>
    ):Base()

    class Version(
        @SerializedName("version") val version:Int
    ):Base()

}