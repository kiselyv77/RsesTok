package com.example.rsestok.utilits.media

import com.example.rsestok.utilits.APP_ACTIVITY
import com.google.android.exoplayer2.database.ExoDatabaseProvider

import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor

import com.google.android.exoplayer2.upstream.cache.SimpleCache
import java.io.File


object VideoCache {
    private var sDownloadCache: SimpleCache? = null
    fun getInstance(): SimpleCache? {
        if (sDownloadCache == null) sDownloadCache = SimpleCache(
            File(APP_ACTIVITY.cacheDir, "exoCache"),
            NoOpCacheEvictor(),
            ExoDatabaseProvider(APP_ACTIVITY)
        )
        return sDownloadCache
    }
}