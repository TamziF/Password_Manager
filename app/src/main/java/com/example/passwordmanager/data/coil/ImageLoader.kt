package com.example.passwordmanager.data.coil

import android.content.Context
import coil.ImageLoader
import coil.disk.DiskCache

class ImageLoader(
    context: Context
) {
    val imageLoader = ImageLoader.Builder(context)
        .diskCache {
            DiskCache.Builder()
                .build()
        }
        .build()
}