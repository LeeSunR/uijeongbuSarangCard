package com.leesunr.uijeongbusarangcard.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class Store(
    @SerializedName("no") @PrimaryKey val no: Int,
    @SerializedName("city") @ColumnInfo(name = "city") val city: String,
    @SerializedName("name") @ColumnInfo(name = "name") val name: String,
    @SerializedName("category") @ColumnInfo(name = "category") val category: String,
    @SerializedName("address") @ColumnInfo(name = "address") val address: String,
    @SerializedName("oldAddress") @ColumnInfo(name = "old_address") val oldAddress: String,
    @SerializedName("mainCategory") @ColumnInfo(name = "main_category") val mainCategory: String,
    @SerializedName("subCategory") @ColumnInfo(name = "sub_category") val subCategory: String?,
    @SerializedName("postNumber") @ColumnInfo(name = "post_number") val postNumber: String,
    @SerializedName("latitude") @ColumnInfo(name = "latitude") val latitude: Double,
    @SerializedName("longitude") @ColumnInfo(name = "longitude") val longitude: Double,
    @SerializedName("date") @ColumnInfo(name = "date") val date: String
)