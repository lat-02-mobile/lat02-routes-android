package com.jalasoft.routesapp.util.helpers

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import androidx.core.graphics.scale
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory

object ImageHelper {
    suspend fun getBitMapFromUrl(context: Context, imageUrl: String): BitmapDescriptor? {
        val loader = ImageLoader(context)
        val request = ImageRequest.Builder(context)
            .data(imageUrl)
            .build()
        val result = (loader.execute(request) as? SuccessResult) ?: return null
        val drawable = result.drawable
        val bitmap = (drawable as BitmapDrawable).bitmap.scale(80, 110, false)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
