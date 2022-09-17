package com.kotlin.android.publish.component.widget.selector

import android.text.TextUtils
import com.kotlin.android.publish.component.widget.selector.PictureMimeType
import com.kotlin.android.publish.component.widget.selector.SelectMimeType
import java.lang.Exception

object PictureMimeType {
    /**
     * isGif
     *
     * @param mimeType
     * @return
     */
    @JvmStatic
    fun isHasGif(mimeType: String?): Boolean {
        return mimeType != null && (mimeType == "image/gif" || mimeType == "image/GIF")
    }

    /**
     * isGif
     *
     * @param url
     * @return
     */
    fun isUrlHasGif(url: String): Boolean {
        return url.toLowerCase().endsWith(".gif")
    }

    /**
     * is has image
     *
     * @param url
     * @return
     */
    fun isUrlHasImage(url: String): Boolean {
        return (url.toLowerCase().endsWith(".jpg")
                || url.toLowerCase().endsWith(".jpeg")
                || url.toLowerCase().endsWith(".png")
                || url.toLowerCase().endsWith(".heic"))
    }

    /**
     * isWebp
     *
     * @param mimeType
     * @return
     */
    fun isHasWebp(mimeType: String?): Boolean {
        return mimeType != null && mimeType.equals("image/webp", ignoreCase = true)
    }

    /**
     * isWebp
     *
     * @param url
     * @return
     */
    fun isUrlHasWebp(url: String): Boolean {
        return url.toLowerCase().endsWith(".webp")
    }

    /**
     * isVideo
     *
     * @param mimeType
     * @return
     */
    @JvmStatic
    fun isHasVideo(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO)
    }

    /**
     * isVideo
     *
     * @param url
     * @return
     */
    fun isUrlHasVideo(url: String): Boolean {
        return url.toLowerCase().endsWith(".mp4")
    }

    /**
     * isAudio
     *
     * @param mimeType
     * @return
     */
    @JvmStatic
    fun isHasAudio(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_AUDIO)
    }

    /**
     * isAudio
     *
     * @param url
     * @return
     */
    fun isUrlHasAudio(url: String): Boolean {
        return url.toLowerCase().endsWith(".amr") || url.toLowerCase().endsWith(".mp3")
    }

    /**
     * isImage
     *
     * @param mimeType
     * @return
     */
    @JvmStatic
    fun isHasImage(mimeType: String?): Boolean {
        return mimeType != null && mimeType.startsWith(MIME_TYPE_PREFIX_IMAGE)
    }

    /**
     * Determine if it is JPG.
     *
     * @param is image file mimeType
     */
    fun isJPEG(mimeType: String): Boolean {
        return if (TextUtils.isEmpty(mimeType)) {
            false
        } else mimeType.startsWith(MIME_TYPE_JPEG) || mimeType.startsWith(
            MIME_TYPE_JPG
        )
    }

    /**
     * Determine if it is JPG.
     *
     * @param is image file mimeType
     */
    fun isJPG(mimeType: String): Boolean {
        return if (TextUtils.isEmpty(mimeType)) {
            false
        } else mimeType.startsWith(MIME_TYPE_JPG)
    }

    /**
     * is Network image
     *
     * @param path
     * @return
     */
    fun isHasHttp(path: String): Boolean {
        return if (TextUtils.isEmpty(path)) {
            false
        } else path.startsWith("http") || path.startsWith("https")
    }

    /**
     * Is it the same type
     *
     * @param oldMimeType 已选的资源类型
     * @param newMimeType 当次选中的资源类型
     * @return
     */
    fun isMimeTypeSame(oldMimeType: String, newMimeType: String): Boolean {
        return if (TextUtils.isEmpty(oldMimeType)) {
            true
        } else getMimeType(
            oldMimeType
        ) == getMimeType(newMimeType)
    }

    /**
     * Picture or video
     *
     * @return
     */
    fun getMimeType(mimeType: String): Int {
        if (TextUtils.isEmpty(mimeType)) {
            return SelectMimeType.TYPE_IMAGE
        }
        return if (mimeType.startsWith(MIME_TYPE_PREFIX_VIDEO)) {
            SelectMimeType.TYPE_VIDEO
        } else if (mimeType.startsWith(MIME_TYPE_PREFIX_AUDIO)) {
            SelectMimeType.TYPE_AUDIO
        } else {
            SelectMimeType.TYPE_IMAGE
        }
    }

    /**
     * Get image suffix
     *
     * @param mineType
     * @return
     */
    fun getLastImgSuffix(mineType: String): String {
        return try {
            mineType.substring(mineType.lastIndexOf("/")).replace("/", ".")
        } catch (e: Exception) {
            e.printStackTrace()
            JPG
        }
    }

    /**
     * is content://
     *
     * @param url
     * @return
     */
    fun isContent(url: String): Boolean {
        return if (TextUtils.isEmpty(url)) {
            false
        } else url.startsWith("content://")
    }

    fun ofPNG(): String {
        return MIME_TYPE_PNG
    }

    fun ofJPEG(): String {
        return MIME_TYPE_JPEG
    }

    fun ofBMP(): String {
        return MIME_TYPE_BMP
    }

    fun ofGIF(): String {
        return MIME_TYPE_GIF
    }

    fun ofWEBP(): String {
        return MIME_TYPE_WEBP
    }

    fun of3GP(): String {
        return MIME_TYPE_3GP
    }

    fun ofMP4(): String {
        return MIME_TYPE_MP4
    }

    fun ofMPEG(): String {
        return MIME_TYPE_MPEG
    }

    fun ofAVI(): String {
        return MIME_TYPE_AVI
    }

    const val MIME_TYPE_IMAGE = "image/jpeg"
    const val MIME_TYPE_VIDEO = "video/mp4"
    const val MIME_TYPE_AUDIO = "audio/mpeg"
    const val MIME_TYPE_AUDIO_AMR = "audio/amr"
    const val MIME_TYPE_PREFIX_IMAGE = "image"
    const val MIME_TYPE_PREFIX_VIDEO = "video"
    const val MIME_TYPE_PREFIX_AUDIO = "audio"
    private const val MIME_TYPE_PNG = "image/png"
    const val MIME_TYPE_JPEG = "image/jpeg"
    private const val MIME_TYPE_JPG = "image/jpg"
    private const val MIME_TYPE_BMP = "image/bmp"
    private const val MIME_TYPE_GIF = "image/gif"
    private const val MIME_TYPE_WEBP = "image/webp"
    private const val MIME_TYPE_3GP = "video/3gp"
    private const val MIME_TYPE_MP4 = "video/mp4"
    private const val MIME_TYPE_MPEG = "video/mpeg"
    private const val MIME_TYPE_AVI = "video/avi"
    const val JPEG = ".jpeg"
    const val JPG = ".jpg"
    const val PNG = ".png"
    const val WEBP = ".webp"
    const val GIF = ".gif"
    const val BMP = ".bmp"
    const val AMR = ".amr"
    const val WAV = ".wav"
    const val MP3 = ".mp3"
    const val MP4 = ".mp4"
    const val AVI = ".avi"
    const val JPEG_Q = "image/jpeg"
    const val PNG_Q = "image/png"
    const val MP4_Q = "video/mp4"
    const val AVI_Q = "video/avi"
    const val AMR_Q = "audio/amr"
    const val WAV_Q = "audio/x-wav"
    const val MP3_Q = "audio/mpeg"
    const val DCIM = "DCIM/Camera"
    const val CAMERA = "Camera"
}