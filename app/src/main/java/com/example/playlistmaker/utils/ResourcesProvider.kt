package com.example.playlistmaker.utils

import android.content.Context
import android.content.res.Resources
import androidx.annotation.PluralsRes

class ResourcesProvider(private val resources: Resources, private val context: Context) {
    fun getQuantityString(@PluralsRes resId: Int, count: Int): String {
        return resources.getQuantityString(resId, count, count)
    }

    fun getString(stringResId: Int): String {
        return context.getString(stringResId)
    }
}