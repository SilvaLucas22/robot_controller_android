package com.robot_controller.utils.extensions

import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri

@Throws(IllegalArgumentException::class, IllegalStateException::class)
fun Uri.toBitmap(
    context: Context,
    maxSidePx: Int = 1280,
    config: Bitmap.Config = Bitmap.Config.ARGB_8888
): Bitmap {
    val resolver: ContentResolver = context.contentResolver

    val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
    resolver.openInputStream(this)?.use { input ->
        BitmapFactory.decodeStream(input, null, bounds)
    }

    val width = bounds.outWidth
    val height = bounds.outHeight
    require(width > 0 && height > 0) { "Imagem inválida ou não foi possível ler dimensões." }

    val largestSide = maxOf(width, height)
    var inSampleSize = 1
    while (largestSide / inSampleSize > maxSidePx) {
        inSampleSize *= 2
    }

    val opts = BitmapFactory.Options().apply {
        this.inSampleSize = inSampleSize
        this.inPreferredConfig = config
    }

    val decoded = resolver.openInputStream(this)?.use { input ->
        BitmapFactory.decodeStream(input, null, opts)
    } ?: throw IllegalStateException("Falha ao decodificar Bitmap a partir da Uri.")

    if (decoded.config == config && decoded.isMutable) return decoded
    return decoded.copy(config, true)
}
