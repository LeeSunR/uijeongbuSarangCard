package com.leesunr.uijeongbusarangcard.main

class MainModel {

    var mainCategory:String? = null
    var subCategory:String? = null
    var searchWord:String = ""

    companion object{
        private var instance:MainModel? = null
        fun getInstance():MainModel{
            if(instance==null){
                instance = MainModel()
            }
            return instance!!
        }
    }
}