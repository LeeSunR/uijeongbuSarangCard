package com.leesunr.uijeongbusarangcard.common

import android.content.Context
import android.util.TypedValue

object Utils {
    fun dpToPx(context: Context, dp:Float):Int{
        val px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.resources.displayMetrics)
        return px.toInt()
    }
}