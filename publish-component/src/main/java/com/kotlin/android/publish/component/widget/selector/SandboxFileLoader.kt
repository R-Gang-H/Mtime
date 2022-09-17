package com.kotlin.android.publish.component.widget.selector

import android.content.Context
import android.text.TextUtils
import com.kotlin.android.ktx.ext.orFalse
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.publish.component.widget.selector.MediaUtils.toLong
import java.io.File
import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.ArrayList

object SandboxFileLoader {
    /**
     * 查询应用内部目录的图片
     *
     * @param context    上下文
     * @param sandboxDir 资源目标路径
     */
    fun loadInAppSandboxFolderFile(context: Context?, sandboxDir: String?): LocalMediaFolder? {
        val list = loadInAppSandboxFile(context, sandboxDir)
        var folder: LocalMediaFolder? = null
        if (list != null && list.size > 0) {
            SortUtils.sortLocalMediaAddedTime(list)
            val firstMedia = list[0]
            folder = LocalMediaFolder()
            folder.folderName = firstMedia?.parentFolderName.orEmpty()
            folder.firstImagePath = firstMedia.path
            folder.firstMimeType = firstMedia.mimeType
            folder.bucketId = firstMedia.bucketId
            folder.folderTotalNum = list.size
            folder.data = list
        }
        return folder
    }

    /**
     * 查询应用内部目录的图片
     *
     * @param context    上下文
     * @param sandboxDir 资源目标路径
     */
    fun loadInAppSandboxFile(context: Context?, sandboxDir: String?): ArrayList<LocalMedia>? {
        if (TextUtils.isEmpty(sandboxDir)) {
            return null
        }
        val list = ArrayList<LocalMedia>()
        val sandboxFile = File(sandboxDir)
        if (sandboxFile.exists()) {
            val files = sandboxFile.listFiles { file -> !file.isDirectory } ?: return list
            val config = PictureSelectionConfig.instance
            var md: MessageDigest? = null
            try {
                md = MessageDigest.getInstance("MD5")
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
            for (f in files) {
                val mimeType = MediaUtils.getMimeTypeFromMediaUrl(f.absolutePath)
                if (config?.chooseMode == SelectMimeType.ofImage()) {
                    if (!PictureMimeType.isHasImage(mimeType)) {
                        continue
                    }
                } else if (config?.chooseMode == SelectMimeType.ofVideo()) {
                    if (!PictureMimeType.isHasVideo(mimeType)) {
                        continue
                    }
                } else if (config?.chooseMode == SelectMimeType.ofAudio()) {
                    if (!PictureMimeType.isHasAudio(mimeType)) {
                        continue
                    }
                }
                if (config?.queryOnlyList != null && config.queryOnlyList?.size.orZero() > 0 && !(config.queryOnlyList?.contains(
                        mimeType
                    ).orFalse())
                ) {
                    continue
                }
                if (!config?.isGif.orFalse()) {
                    if (PictureMimeType.isHasGif(mimeType)) {
                        continue
                    }
                }
                val absolutePath = f.absolutePath
                val size = f.length()
                var id: Long
                id = if (md != null) {
                    md.update(absolutePath.toByteArray())
                    BigInteger(1, md.digest()).toLong()
                } else {
                    f.lastModified() / 1000
                }
                val bucketId: Long = toLong(sandboxFile.name.hashCode(), 0)
                val dateTime = f.lastModified() / 1000
                var duration: Long
                var width: Int
                var height: Int
                if (PictureMimeType.isHasVideo(mimeType)) {
                    val videoSize = MediaUtils.getVideoSize(context, absolutePath)
                    width = videoSize.width
                    height = videoSize.height
                    duration = videoSize.duration
                } else if (PictureMimeType.isHasAudio(mimeType)) {
                    val audioSize = MediaUtils.getAudioSize(context, absolutePath)
                    width = audioSize.width
                    height = audioSize.height
                    duration = audioSize.duration
                } else {
                    val imageSize = MediaUtils.getImageSize(
                        context!!, absolutePath
                    )
                    width = imageSize.width
                    height = imageSize.height
                    duration = 0L
                }
                if (PictureMimeType.isHasVideo(mimeType) || PictureMimeType.isHasAudio(mimeType)) {
                    if (config?.filterVideoMinSecond.orZero() > 0 && duration < config?.filterVideoMinSecond.orZero()) {
                        // If you set the minimum number of seconds of video to display
                        continue
                    }
                    if (config?.filterVideoMaxSecond.orZero() in 1 until duration) {
                        // If you set the maximum number of seconds of video to display
                        continue
                    }
                    if (duration == 0L) {
                        //If the length is 0, the corrupted video is processed and filtered out
                        continue
                    }
                    if (size <= 0) {
                        // The video size is 0 to filter out
                        continue
                    }
                }
                val media = LocalMedia.parseLocalMedia(
                    id,
                    absolutePath,
                    absolutePath,
                    f.name,
                    sandboxFile.name,
                    duration,
                    config?.chooseMode.orZero(),
                    mimeType,
                    width,
                    height,
                    size,
                    bucketId,
                    dateTime
                )
                media.sandboxPath = if (isQ()) absolutePath else null
                list.add(media)
            }
        }
        return list
    }
}