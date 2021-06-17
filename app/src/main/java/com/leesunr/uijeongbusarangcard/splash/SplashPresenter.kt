package com.leesunr.uijeongbusarangcard.splash

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.leesunr.uijeongbusarangcard.BuildConfig
import com.leesunr.uijeongbusarangcard.R
import com.leesunr.uijeongbusarangcard.base.AbstractPresenter
import com.leesunr.uijeongbusarangcard.common.GlobalApplication
import com.leesunr.uijeongbusarangcard.data.AppRepository
import com.leesunr.uijeongbusarangcard.network.RetrofitClient
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.functions.Function3
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.SingleSubject
import java.util.concurrent.TimeUnit

class SplashPresenter(
    private val view: SplashConstants.View
) : SplashConstants.Presenter, AbstractPresenter() {
    override fun appInitialization(context: Context) {
        val model = AppRepository(context)
        view.printMessage("실행 준비중")

        val delaySingle = Single.timer(100,TimeUnit.MILLISECONDS)
        val dataCheckSingle = SingleSubject.create<Boolean>()
        val remoteConfigSingle = SingleSubject.create<Boolean>()
        val versionCheck = SingleSubject.create<Boolean>()

        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 1
        }
        remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(context as Activity) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    remoteConfigSingle.onSuccess(true)
                    Log.d(tag, "Config params updated: $updated")
                } else {
                    remoteConfigSingle.onSuccess(false)
                    Log.d(tag, "Config update fail")
                }
            }

        remoteConfigSingle.flatMap {
            if(remoteConfig.getLong("min_version")<=BuildConfig.VERSION_CODE){
                versionCheck.onSuccess(true)
            }else{
                view.appExit("최신 버전으로 업데이트 해야합니다")
                versionCheck.onSuccess(false)
            }

            Single.zip(delaySingle, dataCheckSingle, versionCheck, Function3<Long, Boolean, Boolean, Boolean> { s1, s2, s3 ->
                s2&&s3
            })
        }
        .subscribe({
            if(it) view.gotoMain()
        }, {
            it.printStackTrace()
        })

        RetrofitClient.createService(context).getVersion()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ versionResponse ->
                if(versionResponse.version!=model.dataVersion){
                    RetrofitClient.createService(context).getAllLatestData()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap {
                            view.printMessage("기존 데이터 삭제 중")
                            model.deleteAllStore().toSingle { it }.subscribeOn(Schedulers.io())
                        }
                        .flatMap {
                            view.printMessage("데이터베이스 업데이트 중")
                            model.insertStore(it.data).toSingle { }.subscribeOn(Schedulers.io())
                        }
                        .subscribe({
                            model.dataVersion = versionResponse.version
                            dataCheckSingle.onSuccess(true)
                        }, {
                            it.printStackTrace()
                            view.appExit("앱 초기화에 실패하였습니다")
                        })
                        .addTo(disposables)
                }
                else{
                    dataCheckSingle.onSuccess(true)
                }
            }, {
                dataCheckSingle.onSuccess(false)
            }).addTo(disposables)
    }
}