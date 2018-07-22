package com.dpuntu.editui

import android.content.res.Resources

/**
 * Created  by fangmingxing on 2018/5/18.
 */

object UtilResource {

    private lateinit var sResources: Resources

    fun init(resources: Resources) {
        sResources = resources
    }

    fun getString(resId: Int): String = sResources.getString(resId)

    fun getDimensionPixelSize(resId: Int): Int = sResources.getDimensionPixelSize(resId)

    fun getInteger(resId: Int): Int = sResources.getInteger(resId)

    fun getColor(resId: Int): Int = sResources.getColor(resId)

}
