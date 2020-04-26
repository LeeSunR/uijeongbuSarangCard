package com.leesunr.uijeongbusarangcard

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


class DatabaseHelper(context: Context): SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION){

    companion object{
        private val DB_VERSION =6
        private val DB_NAME = "DB"
        private val TABLE_NAME = "Store"
        private val STORE_ID = "STORE_ID"
        private val SIGUN_NM = "SIGUN_NM"
        private val CMPNM_NM = "CMPNM_NM"
        private val INDUTYPE_NM = "INDUTYPE_NM"
        private val REFINE_ROADNM_ADDR = "REFINE_ROADNM_ADDR"
        private val REFINE_LOTNO_ADDR = "REFINE_LOTNO_ADDR"
        private val TELNO = "TELNO"
        private val REFINE_ZIPNO ="REFINE_ZIPNO"
        private val REFINE_WGS84_LAT ="REFINE_WGS84_LAT"
        private val REFINE_WGS84_LOGT ="REFINE_WGS84_LOGT"
        private val DATA_STD_DE ="DATA_STD_DE"
        private val DISTANCE ="DISTANCE"
    }

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_TABLE_QUERY =("CREATE TABLE $TABLE_NAME($STORE_ID TEXT PRIMARY KEY," +
                "$CMPNM_NM TEXT," +
                "$SIGUN_NM TEXT," +
                "$INDUTYPE_NM TEXT," +
                "$REFINE_ROADNM_ADDR TEXT," +
                "$REFINE_LOTNO_ADDR TEXT," +
                "$TELNO TEXT," +
                "$REFINE_ZIPNO TEXT," +
                "$REFINE_WGS84_LAT TEXT," +
                "$REFINE_WGS84_LOGT TEXT," +
                "$DATA_STD_DE TEXT," +
                "$DISTANCE TEXT)")
        db!!.execSQL(CREATE_TABLE_QUERY)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db!!)
    }

    val allStore:ArrayList<Store>
    get(){
        val storeArray = ArrayList<Store>()
        val selectQueryHandler = "SELECT * FROM $TABLE_NAME ORDER BY CAST($DISTANCE AS INTEGER)"
        val db = writableDatabase
        val cursor = db.rawQuery(selectQueryHandler,null)
        if (cursor.moveToFirst()){
            do{
                val store = Store()
                store.STORE_ID = cursor.getInt(cursor.getColumnIndex(STORE_ID))
                store.SIGUN_NM = cursor.getString(cursor.getColumnIndex(SIGUN_NM))
                store.CMPNM_NM = cursor.getString(cursor.getColumnIndex(CMPNM_NM))
                store.INDUTYPE_NM = cursor.getString(cursor.getColumnIndex(INDUTYPE_NM))
                store.REFINE_ROADNM_ADDR = cursor.getString(cursor.getColumnIndex(REFINE_ROADNM_ADDR))
                store.REFINE_LOTNO_ADDR = cursor.getString(cursor.getColumnIndex(REFINE_LOTNO_ADDR))
                store.TELNO = cursor.getString(cursor.getColumnIndex(TELNO))
                store.REFINE_ZIPNO = cursor.getString(cursor.getColumnIndex(REFINE_ZIPNO))
                store.REFINE_WGS84_LAT = cursor.getString(cursor.getColumnIndex(REFINE_WGS84_LAT))
                store.REFINE_WGS84_LOGT = cursor.getString(cursor.getColumnIndex(REFINE_WGS84_LOGT))
                store.DATA_STD_DE = cursor.getString(cursor.getColumnIndex(DATA_STD_DE))
                store.DISTANCE = cursor.getLong(cursor.getColumnIndex(DISTANCE))
                storeArray.add(store)
            }while (cursor.moveToNext())
        }
        db.close()
        return storeArray
    }

    fun getStore(query: String): ArrayList<Store>{
        val storeArray = ArrayList<Store>()
        val selectQueryHandler = "SELECT * FROM $TABLE_NAME WHERE $query ORDER BY CAST($DISTANCE AS INTEGER)"
        val db = writableDatabase
        val cursor = db.rawQuery(selectQueryHandler,null)
        if (cursor.moveToFirst()){
            do{
                val store = Store()
                store.STORE_ID = cursor.getInt(cursor.getColumnIndex(STORE_ID))
                store.SIGUN_NM = cursor.getString(cursor.getColumnIndex(SIGUN_NM))
                store.CMPNM_NM = cursor.getString(cursor.getColumnIndex(CMPNM_NM))
                store.INDUTYPE_NM = cursor.getString(cursor.getColumnIndex(INDUTYPE_NM))
                store.REFINE_ROADNM_ADDR = cursor.getString(cursor.getColumnIndex(REFINE_ROADNM_ADDR))
                store.REFINE_LOTNO_ADDR = cursor.getString(cursor.getColumnIndex(REFINE_LOTNO_ADDR))
                store.TELNO = cursor.getString(cursor.getColumnIndex(TELNO))
                store.REFINE_ZIPNO = cursor.getString(cursor.getColumnIndex(REFINE_ZIPNO))
                store.REFINE_WGS84_LAT = cursor.getString(cursor.getColumnIndex(REFINE_WGS84_LAT))
                store.REFINE_WGS84_LOGT = cursor.getString(cursor.getColumnIndex(REFINE_WGS84_LOGT))
                store.DATA_STD_DE = cursor.getString(cursor.getColumnIndex(DATA_STD_DE))
                store.DISTANCE = cursor.getLong(cursor.getColumnIndex(DISTANCE))
                storeArray.add(store)
            }while (cursor.moveToNext())
        }
        db.close()
        return storeArray
    }

    fun addStore(store : Store){
        val db = this.writableDatabase
        val values = ContentValues()
        values.put(STORE_ID,store.STORE_ID)
        values.put(SIGUN_NM,store.SIGUN_NM)
        values.put(CMPNM_NM,store.CMPNM_NM)
        values.put(INDUTYPE_NM,store.INDUTYPE_NM)
        values.put(REFINE_ROADNM_ADDR,store.REFINE_ROADNM_ADDR)
        values.put(REFINE_LOTNO_ADDR,store.REFINE_LOTNO_ADDR)
        values.put(TELNO,store.TELNO)
        values.put(REFINE_ZIPNO,store.REFINE_ZIPNO)
        values.put(REFINE_WGS84_LAT,store.REFINE_WGS84_LAT)
        values.put(REFINE_WGS84_LOGT,store.REFINE_WGS84_LOGT)
        values.put(DATA_STD_DE,store.DATA_STD_DE)
        values.put(DISTANCE,store.DISTANCE)
        db.insert(TABLE_NAME,null,values)
        db.close()
    }

    fun bulkInsert(store : ArrayList<Store>) {
        val db = this.writableDatabase
        db.beginTransaction()
        try {
            for (i in 0 until store.size) {
                val values = ContentValues()
                values.put(STORE_ID,store[i].STORE_ID)
                values.put(SIGUN_NM,store[i].SIGUN_NM)
                values.put(CMPNM_NM,store[i].CMPNM_NM)
                values.put(INDUTYPE_NM,store[i].INDUTYPE_NM)
                values.put(REFINE_ROADNM_ADDR,store[i].REFINE_ROADNM_ADDR)
                values.put(REFINE_LOTNO_ADDR,store[i].REFINE_LOTNO_ADDR)
                values.put(TELNO,store[i].TELNO)
                values.put(REFINE_ZIPNO,store[i].REFINE_ZIPNO)
                values.put(REFINE_WGS84_LAT,store[i].REFINE_WGS84_LAT)
                values.put(REFINE_WGS84_LOGT,store[i].REFINE_WGS84_LOGT)
                values.put(DATA_STD_DE,store[i].DATA_STD_DE)
                values.put(DISTANCE,store[i].DISTANCE)
                db.insert(TABLE_NAME, null, values)
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    fun deleteAll() {
        val db = this.writableDatabase
        db.execSQL("DELETE FROM $TABLE_NAME");
    }
}