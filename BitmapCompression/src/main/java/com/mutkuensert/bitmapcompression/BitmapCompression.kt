package com.mutkuensert.bitmapcompression

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.core.graphics.scale
import java.io.ByteArrayOutputStream
import java.io.File

private const val TAG = "BitmapCompression"

/**
 * @property file The file to be reduced in size. It will be overwritten with the size reduction processes.
 * @property sizeLimitBytes Max size the file can be after compression.
 * @property compressPriority Start reducing file size by scaling down or compressing.
 * @property lowerWidthLimit Stop scaling down before dropping down below this value.
 * @property lowerHeightLimit Stop scaling down before dropping down below this value.
 * @property compressionQualityDownTo Lower value means lower quality and smaller size.
 * @property scaleDownFactor Scale factor to divide width and height of image in every loop.
 */
class BitmapCompression(
    private val file: File,
    val sizeLimitBytes: Int,
    val compressPriority: CompressPriority = CompressPriority.STARTBYCOMPRESS,
    val lowerWidthLimit: Int? = null,
    val lowerHeightLimit: Int? = null,
    @IntRange(from = 1, to = 90)
    val compressionQualityDownTo: Int = 10,
    @FloatRange(from = 0.1, to = 0.9)
    val scaleDownFactor: Float = 0.5f
) {
    private var _currentCompressionQuality = 90
    val currentCompressionQuality: Int get() = _currentCompressionQuality

    private var bitmap = BitmapFactory.decodeFile(file.absolutePath)

    companion object {

        /**
         * @param file The file will be overwritten with the size reduction processes.
         */
        fun compress(
            file: File,
            @IntRange(from = 0, to = 100)
            quality: Int,
        ) {
            val newBitmap = compress(BitmapFactory.decodeFile(file.absolutePath), quality)

            val byteArrayOutputStream = newBitmap.createOutputStream()

            file.overwriteByStream(byteArrayOutputStream)
            byteArrayOutputStream.close()
        }

        fun compress(
            bitmap: Bitmap,
            @IntRange(from = 0, to = 100)
            quality: Int
        ): Bitmap {
            val byteArrayOutputStream = ByteArrayOutputStream()

            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, byteArrayOutputStream)

            val newBitmap = BitmapFactory.decodeByteArray(
                byteArrayOutputStream.toByteArray(),
                0,
                byteArrayOutputStream.size()
            )

            byteArrayOutputStream.close()

            return newBitmap
        }

        /**
         * @throws IllegalArgumentException If width argument is bigger than image width.
         */
        fun scaleDownToWidth(
            file: File,
            width: Int
        ) {
            var bitmap = BitmapFactory.decodeFile(file.absolutePath)

            bitmap = scaleDownToWidth(bitmap, width)

            val outputStream = bitmap.createOutputStream()
            file.overwriteByStream(outputStream)
            outputStream.close()
        }

        /**
         * @throws IllegalArgumentException If width argument is bigger than bitmap width.
         */
        fun scaleDownToWidth(
            bitmap: Bitmap,
            width: Int
        ): Bitmap {
            if (width > bitmap.width) {
                throw IllegalArgumentException("Argument $width is bigger than ${bitmap.width}")
            }

            return bitmap.scale(
                width = width,
                height = bitmap.getHeightByWidth(width)
            )
        }

        /**
         * @throws IllegalArgumentException If height argument is bigger than image height.
         */
        fun scaleDownToHeight(
            file: File,
            height: Int
        ) {
            var bitmap = BitmapFactory.decodeFile(file.absolutePath)

            bitmap = scaleDownToHeight(bitmap, height)

            val outputStream = bitmap.createOutputStream()
            file.overwriteByStream(outputStream)
            outputStream.close()
        }

        /**
         * @throws IllegalArgumentException If height argument is bigger than bitmap height.
         */
        fun scaleDownToHeight(
            bitmap: Bitmap,
            height: Int
        ): Bitmap {
            if (height > bitmap.height) {
                throw IllegalArgumentException("Argument $height is bigger than ${bitmap.height}")
            }

            return bitmap.scale(
                width = bitmap.getWidthByHeight(height),
                height = height
            )
        }
    }

    /**
     * @throws RuntimeException If compression and scaling down processes can't reduce the file size
     * under the limit with current configuration.
     */
    fun compressAndScaleDown() {
        if (file.length() < sizeLimitBytes) {
            Log.i(TAG, "$file: File is already under the size limit.")
            return
        }

        compressAndScaleDownByPriority()
    }

    private fun compress() {
        var factor = 90
        val byteArrayOutputStream = ByteArrayOutputStream()

        do {
            byteArrayOutputStream.reset()

            _currentCompressionQuality = factor
            bitmap.compress(Bitmap.CompressFormat.JPEG, factor, byteArrayOutputStream)
            factor -= 10

            file.overwriteByStream(byteArrayOutputStream)

            if (factor <= compressionQualityDownTo) break
        } while (file.length() >= sizeLimitBytes)

        byteArrayOutputStream.close()
    }

    private fun scaleDown() {
        while ((file.length() >= sizeLimitBytes)) {
            val scaledDownWidth = bitmap.width * scaleDownFactor
            val scaledDownHeight = bitmap.height * scaleDownFactor

            if ((lowerWidthLimit != null && scaledDownWidth < lowerWidthLimit)
                || (lowerHeightLimit != null && scaledDownHeight < lowerHeightLimit)
            ) {
                if (compressPriority == CompressPriority.STARTBYSCALEDOWN) {
                    break
                } else {
                    throw RuntimeException(
                        "File is too big for specified upper limits. " +
                                "Try with lower size limits or change compress priority to " +
                                "CompressPriority.STARTBYCOMPRESS"
                    )
                }
            }

            bitmap = bitmap.scale(
                width = scaledDownWidth.toInt(),
                height = scaledDownHeight.toInt()
            )

            file.overwriteByBitmap(bitmap, currentCompressionQuality)
        }
    }

    enum class CompressPriority {
        STARTBYSCALEDOWN, STARTBYCOMPRESS
    }

    private fun compressAndScaleDownByPriority() {
        if (compressPriority == CompressPriority.STARTBYSCALEDOWN) {
            scaleDown()
            compress()
        } else {
            compress()
            scaleDown()
        }
    }
}