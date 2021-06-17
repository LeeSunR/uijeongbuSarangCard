package com.leesunr.uijeongbusarangcard.network

import android.content.Context
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    fun createService(context: Context): APIInterface{
        return createService()
    }

    private fun createService(): APIInterface{
        val remoteConfig = Firebase.remoteConfig

        val client = OkHttpClient()
            .newBuilder()
            .build()

        return Retrofit.Builder()
            .baseUrl(remoteConfig.getString("base_url"))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
            .client(client)
            .build()
            .create(APIInterface::class.java)
    }
}