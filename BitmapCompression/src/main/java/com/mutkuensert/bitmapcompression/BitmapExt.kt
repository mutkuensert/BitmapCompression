package com.mutkuensert.bitmapcompression

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream

internal val Bitmap.aspectRatio: Float get() = width.toFloat() / height.toFloat()

internal fun Bitmap.getHeightByWidth(width: Int): Int {
    return (width / aspectRatio).toInt()
}

internal fun Bitmap.getWidthByHeight(height: Int): Int {
    return (height * aspectRatio).toInt()
}

internal fun Bitmap.createOutputStream(): ByteArrayOutputStream {
    val byteArrayOutputStream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)

    return byteArrayOutputStream
}