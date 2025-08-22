package edu.bluejack23_1.next.cache

import android.graphics.Bitmap
import android.util.LruCache

object ThumbnailCache {
    private val cache: LruCache<String, Bitmap>

    init {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8
        cache = object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    fun insertCache(key: String, bitmap: Bitmap) {
        cache.put(key, bitmap)
    }

    fun getCache(key: String): Bitmap? {
        return cache.get(key)
    }
}