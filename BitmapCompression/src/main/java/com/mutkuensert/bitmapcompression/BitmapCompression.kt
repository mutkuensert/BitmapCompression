package com.mutkuensert.bitmapcompression

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream
import java.io.File

/**
 * @property sizeLimitBytes Max size the file can be after compression.
 * @property compressPriority Start reducing file size by scaling down or compressing.
 * @property lowerWidthLimit Stop scaling down before dropping down below this value.
 * @property lowerHeightLimit Stop scaling down before dropping down below this value.
 * @property compressionQualityDownTo Lower value means lower quality and smaller size.
 * @property scaleDownFactor Scale factor to divide width and height of image in every loop.
 */
class BitmapCompression(
    val sizeLimitBytes: Int,
    val compressPriority: CompressPriority = CompressPriority.STARTBYCOMPRESS,
    val lowerWidthLimit: Int? = null,
    val lowerHeightLimit: Int? = null,
    @IntRange(from = 1, to = 90)
    val compressionQualityDownTo: Int = 10,
    @FloatRange(from = 0.1, to = 0.9)
    val scaleDownFactor: Float = 0.5f
) {
    fun compress(file: File) {
        if (file.length() < sizeLimitBytes) return

        val byteArrayOutputStream = ByteArrayOutputStream()

        var bitmap = BitmapFactory.decodeFile(file.absolutePath)

        bitmap = compressByPriority(bitmap, byteArrayOutputStream)

        bitmap.recycle()

        file.delete()
        file.createNewFile()
        file.outputStream().use {
            byteArrayOutputStream.writeTo(it)
        }

        byteArrayOutputStream.close()
    }

    private fun compressBitmap(
        bitmap: Bitmap,
        byteArrayOutputStream: ByteArrayOutputStream,
    ) {
        var factor = 90

        do {
            byteArrayOutputStream.reset()
            bitmap.compress(Bitmap.CompressFormat.JPEG, factor, byteArrayOutputStream)
            factor -= 10
            if (factor <= compressionQualityDownTo) break
        } while (byteArrayOutputStream.size() >= sizeLimitBytes)
    }

    private fun getScaledDownBitmap(
        bitmap: Bitmap,
        byteArrayOutputStream: ByteArrayOutputStream,
    ): Bitmap {
        var scaledDownBitmap = bitmap

        do {
            val scaledDownWidth = scaledDownBitmap.width * scaleDownFactor
            val scaledDownHeight = scaledDownBitmap.height * scaleDownFactor

            if ((lowerWidthLimit != null && scaledDownWidth < lowerWidthLimit)
                || (lowerHeightLimit != null && scaledDownHeight < lowerHeightLimit)
            ) {
                if (compressPriority == CompressPriority.STARTBYSCALEDOWN) {
                    break
                } else {
                    throw RuntimeException(
                        "File is too big for specified upper limits. " +
                                "Try with lower limits."
                    )
                }
            }

            scaledDownBitmap = scaledDownBitmap.scale(
                width = scaledDownWidth.toInt(),
                height = scaledDownHeight.toInt()
            )
            byteArrayOutputStream.reset()
            scaledDownBitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream)
        } while ((byteArrayOutputStream.size() >= sizeLimitBytes))

        return scaledDownBitmap
    }

    enum class CompressPriority {
        STARTBYSCALEDOWN, STARTBYCOMPRESS
    }

    private fun compressByPriority(
        bitmap: Bitmap,
        byteArrayOutputStream: ByteArrayOutputStream,
    ): Bitmap {
        var newBitmap = bitmap

        if (compressPriority == CompressPriority.STARTBYSCALEDOWN) {
            newBitmap = getScaledDownBitmap(newBitmap, byteArrayOutputStream)
            compressBitmap(newBitmap, byteArrayOutputStream)
        } else {
            compressBitmap(newBitmap, byteArrayOutputStream)
            newBitmap = getScaledDownBitmap(newBitmap, byteArrayOutputStream)
        }
        return newBitmap
    }
}