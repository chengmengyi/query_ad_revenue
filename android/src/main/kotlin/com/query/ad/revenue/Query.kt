package com.query.ad.revenue

import android.content.Context
import android.util.Log

object Query {
    private val loadedLibraries = mutableSetOf<String>()

    @Synchronized
    fun loadLibrary(libName: String) {
        if (loadedLibraries.contains(libName)) {
            return
        }
        System.loadLibrary(libName)
        loadedLibraries.add(libName)
    }

    external fun query(context: Context,ad: Any,path: String): Long

    fun getRevenueInfo(context: Context, ad: Any?, path: String): Long {
        if (ad == null) return -1
        var value = runCatching {
            query(context, ad, path)
        }.onFailure {

        }.getOrNull() ?: -3
        return value
    }
}
