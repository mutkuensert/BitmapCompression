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

internal fun File.overwriteByBitmap(bitmap: Bitmap) {
    val byteArrayOutputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    overwriteByStream(byteArrayOutputStream)
    byteArrayOutputStream.close()
}