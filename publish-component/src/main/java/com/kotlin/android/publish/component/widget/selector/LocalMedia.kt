package com.kotlin.android.publish.component.widget.selector

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.kotlin.android.app.data.ProguardRule

class LocalMedia : Parcelable,ProguardRule {

    /**
     * file to ID
     */
    var id: Long = 0

    /**
     * original path
     */
    var path: String? = null

    /**
     * The real path，But you can't get access from AndroidQ
     */
    var realPath: String? = null

    /**
     * # Check the original button to get the return value
     * original path
     */
    var originalPath: String? = null

    /**
     * compress path
     */
    var compressPath: String? = null

    /**
     * cut path
     */
    var cutPath: String? = null

    /**
     * app sandbox path
     */
    var sandboxPath: String? = null

    /**
     * video duration
     */
    var duration: Long = 0

    /**
     * If the selected
     * # Internal use
     */
    var isChecked = false

    /**
     * If the cut
     */
    private var isCut = false

    /**
     * media position of list
     */
    var position = 0

    /**
     * The media number of qq choose styles
     */
    var num = 0

    /**
     * The media resource type
     */
    var mimeType: String? = null

    /**
     * Gallery selection mode
     */
    var chooseModel = 0

    /**
     * If the compressed
     */
    private var compressed = false

    /**
     * image or video width
     *
     *
     * # If zero occurs, the developer needs to handle it extra
     */
    var width = 0

    /**
     * image or video height
     *
     *
     * # If zero occurs, the developer needs to handle it extra
     */
    var height = 0

    /**
     * Crop the width of the picture
     */
    var cropImageWidth = 0

    /**
     * Crop the height of the picture
     */
    var cropImageHeight = 0

    /**
     * Crop ratio x
     */
    var cropOffsetX = 0

    /**
     * Crop ratio y
     */
    var cropOffsetY = 0

    /**
     * Crop Aspect Ratio
     */
    var cropResultAspectRatio = 0f

    /**
     * file size
     */
    var size: Long = 0

    /**
     * Whether the original image is displayed
     */
    private var isOriginal = false

    /**
     * file name
     */
    var fileName: String? = null

    /**
     * Parent  Folder Name
     */
    var parentFolderName: String? = null

    /**
     * bucketId
     */
    var bucketId = -1L

    /**
     * media create time
     */
    var dateAddedTime: Long = 0

    /**
     * custom data
     *
     *
     * User defined data can be expanded freely
     *
     */
    var customData: String? = null

    /**
     * isMaxSelectEnabledMask
     * # For internal use only
     */
    var isMaxSelectEnabledMask = false

    /**
     * isGalleryEnabledMask
     * # For internal use only
     */
    var isGalleryEnabledMask = false

    /**
     * Whether the image has been edited
     * # For internal use only
     */
    private var isEditorImage = false

    /**
     * 获取当前匹配上的对象
     */
    /**
     * 当前匹配上的对象
     */
    var compareLocalMedia: LocalMedia? = null
        private set

    /**
     * 重写equals进行值的比较
     *
     * @param o
     * @return
     */
    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is LocalMedia) return false
        val media = o
        val isCompare = TextUtils.equals(path, media.path) || id == media.id
        compareLocalMedia = if (isCompare) media else null
        return isCompare
    }

    /**
     * get real and effective resource path
     *
     * @return
     */
    val availablePath: String?
        get() {
            var path: String? = ""
            path = when {
                isCompressed() -> {
                    compressPath
                }
                isCut() -> {
                    cutPath
                }
                isToSandboxPath -> {
                    sandboxPath
                }
                else -> {
                    path
                }
            }
            return path
        }

    fun isCut(): Boolean {
        return isCut && !TextUtils.isEmpty(cutPath)
    }

    fun setCut(cut: Boolean) {
        isCut = cut
    }

    fun isCompressed(): Boolean {
        return compressed && !TextUtils.isEmpty(compressPath)
    }

    fun setCompressed(compressed: Boolean) {
        this.compressed = compressed
    }

    fun isOriginal(): Boolean {
        return isOriginal && !TextUtils.isEmpty(originalPath)
    }

    fun setOriginal(original: Boolean) {
        isOriginal = original
    }

    fun isEditorImage(): Boolean {
        return isEditorImage && !TextUtils.isEmpty(cutPath)
    }

    fun setEditorImage(editorImage: Boolean) {
        isEditorImage = editorImage
    }

    val isToSandboxPath: Boolean
        get() = !TextUtils.isEmpty(sandboxPath)

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        path = parcel.readString()
        realPath = parcel.readString()
        originalPath = parcel.readString()
        compressPath = parcel.readString()
        cutPath = parcel.readString()
        sandboxPath = parcel.readString()
        duration = parcel.readLong()
        isChecked = parcel.readByte() != 0.toByte()
        isCut = parcel.readByte() != 0.toByte()
        position = parcel.readInt()
        num = parcel.readInt()
        mimeType = parcel.readString()
        chooseModel = parcel.readInt()
        compressed = parcel.readByte() != 0.toByte()
        width = parcel.readInt()
        height = parcel.readInt()
        cropImageWidth = parcel.readInt()
        cropImageHeight = parcel.readInt()
        cropOffsetX = parcel.readInt()
        cropOffsetY = parcel.readInt()
        cropResultAspectRatio = parcel.readFloat()
        size = parcel.readLong()
        isOriginal = parcel.readByte() != 0.toByte()
        fileName = parcel.readString()
        parentFolderName = parcel.readString()
        bucketId = parcel.readLong()
        dateAddedTime = parcel.readLong()
        customData = parcel.readString()
        isMaxSelectEnabledMask = parcel.readByte() != 0.toByte()
        isGalleryEnabledMask = parcel.readByte() != 0.toByte()
        isEditorImage = parcel.readByte() != 0.toByte()
    }

    constructor()

    override fun toString(): String {
        return "LocalMedia{" +
                "id=" + id +
                ", path='" + path + '\'' +
                ", realPath='" + realPath + '\'' +
                ", originalPath='" + originalPath + '\'' +
                ", compressPath='" + compressPath + '\'' +
                ", cutPath='" + cutPath + '\'' +
                ", sandboxPath='" + sandboxPath + '\'' +
                ", duration=" + duration +
                ", isChecked=" + isChecked +
                ", isCut=" + isCut +
                ", position=" + position +
                ", num=" + num +
                ", mimeType='" + mimeType + '\'' +
                ", chooseModel=" + chooseModel +
                ", compressed=" + compressed +
                ", width=" + width +
                ", height=" + height +
                ", cropImageWidth=" + cropImageWidth +
                ", cropImageHeight=" + cropImageHeight +
                ", cropOffsetX=" + cropOffsetX +
                ", cropOffsetY=" + cropOffsetY +
                ", cropResultAspectRatio=" + cropResultAspectRatio +
                ", size=" + size +
                ", isOriginal=" + isOriginal +
                ", fileName='" + fileName + '\'' +
                ", parentFolderName='" + parentFolderName + '\'' +
                ", bucketId=" + bucketId +
                ", dateAddedTime=" + dateAddedTime +
                ", customData='" + customData + '\'' +
                ", isMaxSelectEnabledMask=" + isMaxSelectEnabledMask +
                ", isGalleryEnabledMask=" + isGalleryEnabledMask +
                ", isEditorImage=" + isEditorImage +
                ", compareLocalMedia=" + compareLocalMedia +
                '}'
    }

    companion object {
        @JvmField
        val CREATOR  = object :Parcelable.Creator<LocalMedia> {
            override fun createFromParcel(parcel: Parcel): LocalMedia {
                return LocalMedia(parcel)
            }

            override fun newArray(size: Int): Array<LocalMedia?> {
                return arrayOfNulls(size)
            }
        }

        /**
         * 构造网络资源下的LocalMedia
         *
         * @param url      网络url
         * @param mimeType 资源类型 [# PictureMimeType.ofGIF() ...][]
         * @return
         */
        fun generateLocalMedia(url: String?, mimeType: String?): LocalMedia {
            val media = LocalMedia()
            media.path = url
            media.mimeType = mimeType
            return media
        }

        /**
         * 构造LocalMedia
         *
         * @param id               资源id
         * @param path             资源路径
         * @param absolutePath     资源绝对路径
         * @param fileName         文件名
         * @param parentFolderName 文件所在相册目录名称
         * @param duration         视频/音频时长
         * @param chooseModel      相册选择模式
         * @param mimeType         资源类型
         * @param width            资源宽
         * @param height           资源高
         * @param size             资源大小
         * @param bucketId         文件目录id
         * @param dateAdded        资源添加时间
         * @return
         */
        @JvmStatic
        fun parseLocalMedia(
            id: Long, path: String?, absolutePath: String?,
            fileName: String?, parentFolderName: String?,
            duration: Long, chooseModel: Int, mimeType: String?,
            width: Int, height: Int, size: Long, bucketId: Long, dateAdded: Long
        ): LocalMedia {
            val localMedia = LocalMedia()
            localMedia.id = id
            localMedia.path = path
            localMedia.realPath = absolutePath
            localMedia.fileName = fileName
            localMedia.parentFolderName = parentFolderName
            localMedia.duration = duration
            localMedia.chooseModel = chooseModel
            localMedia.mimeType = mimeType
            localMedia.width = width
            localMedia.height = height
            localMedia.size = size
            localMedia.bucketId = bucketId
            localMedia.dateAddedTime = dateAdded
            return localMedia
        }
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(path)
        parcel.writeString(realPath)
        parcel.writeString(originalPath)
        parcel.writeString(compressPath)
        parcel.writeString(cutPath)
        parcel.writeString(sandboxPath)
        parcel.writeLong(duration)
        parcel.writeByte(if (isChecked) 1 else 0)
        parcel.writeByte(if (isCut) 1 else 0)
        parcel.writeInt(position)
        parcel.writeInt(num)
        parcel.writeString(mimeType)
        parcel.writeInt(chooseModel)
        parcel.writeByte(if (compressed) 1 else 0)
        parcel.writeInt(width)
        parcel.writeInt(height)
        parcel.writeInt(cropImageWidth)
        parcel.writeInt(cropImageHeight)
        parcel.writeInt(cropOffsetX)
        parcel.writeInt(cropOffsetY)
        parcel.writeFloat(cropResultAspectRatio)
        parcel.writeLong(size)
        parcel.writeByte(if (isOriginal) 1 else 0)
        parcel.writeString(fileName)
        parcel.writeString(parentFolderName)
        parcel.writeLong(bucketId)
        parcel.writeLong(dateAddedTime)
        parcel.writeString(customData)
        parcel.writeByte(if (isMaxSelectEnabledMask) 1 else 0)
        parcel.writeByte(if (isGalleryEnabledMask) 1 else 0)
        parcel.writeByte(if (isEditorImage) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }


}