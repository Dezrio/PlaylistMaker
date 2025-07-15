package com.example.playlistmaker.utils

import android.content.Context
import androidx.annotation.PluralsRes

class ResourcesProvider(private val context: Context) {
    fun getQuantityString(@PluralsRes resId: Int, count: Int): String {
        return context.resources.getQuantityString(resId, count, count)
    }

    fun getString(stringResId: Int): String {
        return context.getString(stringResId)
    }
}