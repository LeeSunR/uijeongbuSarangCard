package com.leesunr.uijeongbusarangcard

class Store{
    var STORE_ID:Int?=null
    var SIGUN_NM:String?=null
    var CMPNM_NM:String?=null
    var INDUTYPE_NM:String?=null
    var REFINE_ROADNM_ADDR:String?=null
    var REFINE_LOTNO_ADDR:String?=null
    var TELNO:String?=null
    var REFINE_ZIPNO:String?=null
    var REFINE_WGS84_LAT:String?=null
    var REFINE_WGS84_LOGT:String?=null
    var DATA_STD_DE:String?=null
    var DISTANCE:Long?=null

    constructor(){}

    constructor(STORE_ID:Int){
        this.STORE_ID = STORE_ID
    }
}