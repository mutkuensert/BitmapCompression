package com.mutkuensert.bitmapcompression

import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File

internal fun File.overwriteByStream(outputStream: ByteArrayOutputStream) {
    delete()
    createNewFile()
    outputStream().use {
        outputStream.writeTo(it)
    }
}

internal fun File.overwriteByBitmap(bitmap: Bitmap, quality: Int) {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(
        Bitmap.CompressFormat.JPEG,
        quality,
        byteArrayOutputStream
    )
    overwriteByStream(byteArrayOutputStream)
    byteArrayOutputStream.close()
}