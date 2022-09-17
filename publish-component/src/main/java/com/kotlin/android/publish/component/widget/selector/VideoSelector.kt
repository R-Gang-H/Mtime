package com.kotlin.android.publish.component.widget.selector

import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.text.TextUtils
import com.kotlin.android.core.CoreApp
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.ktx.utils.LogUtils
import java.lang.Integer.max
import java.util.*

/**
 * create by lushan on 2022/4/1
 * des:
 **/
val QUERY_URI: Uri = MediaStore.Files.getContentUri("external")
const val COLUMN_BUCKET_ID = "bucket_id"
const val COLUMN_DURATION = "duration"
const val COLUMN_BUCKET_DISPLAY_NAME = "bucket_display_name"
const val COLUMN_ORIENTATION = "orientation"
const val MIME_TYPE_PREFIX_IMAGE = "image"
const val MIME_TYPE_PREFIX_VIDEO = "video"
const val MIME_TYPE_PREFIX_AUDIO = "audio"
const val MAX_SORT_SIZE = 60
const val ORDER_BY = MediaStore.MediaColumns.DATE_MODIFIED + " DESC"
val PROJECTION = arrayOf(
    MediaStore.Files.FileColumns._ID,
    MediaStore.MediaColumns.DATA,
    MediaStore.MediaColumns.MIME_TYPE,
    MediaStore.MediaColumns.WIDTH,
    MediaStore.MediaColumns.HEIGHT,
    COLUMN_DURATION,
    MediaStore.MediaColumns.SIZE,
    COLUMN_BUCKET_DISPLAY_NAME,
    MediaStore.MediaColumns.DISPLAY_NAME,
    COLUMN_BUCKET_ID,
    MediaStore.MediaColumns.DATE_ADDED,
    COLUMN_ORIENTATION
)


fun loadAllVideo(
    config: PictureSelectionConfig,
    videoListener: ((ArrayList<LocalMedia>) -> Unit)? = null
): ArrayList<LocalMedia> {
    val imageFolders: MutableList<LocalMediaFolder> = mutableListOf()
    val data: Cursor? = CoreApp.instance.contentResolver.query(
        QUERY_URI,
        PROJECTION,
        getSelectionArgsForVideoMediaCondition(
            getDurationCondition(config).orEmpty(),
            getQueryMimeCondition(config).orEmpty()
        ),
        getSelectionArgsForSingleMediaType(MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO),
        getSortOrder(config)
    )
    val latelyImages: ArrayList<LocalMedia> = ArrayList<LocalMedia>()
    try {
        if (data != null) {
            val allImageFolder = LocalMediaFolder()

            val count = data.count
            if (count > 0) {
                val idColumn = data.getColumnIndexOrThrow(PROJECTION[0])
                val dataColumn = data.getColumnIndexOrThrow(PROJECTION[1])
                val mimeTypeColumn = data.getColumnIndexOrThrow(PROJECTION[2])
                val widthColumn = data.getColumnIndexOrThrow(PROJECTION[3])
                val heightColumn = data.getColumnIndexOrThrow(PROJECTION[4])
                val durationColumn = data.getColumnIndexOrThrow(PROJECTION[5])
                val sizeColumn = data.getColumnIndexOrThrow(PROJECTION[6])
                val folderNameColumn = data.getColumnIndexOrThrow(PROJECTION[7])
                val fileNameColumn = data.getColumnIndexOrThrow(PROJECTION[8])
                val bucketIdColumn = data.getColumnIndexOrThrow(PROJECTION[9])
                val dateAddedColumn = data.getColumnIndexOrThrow(PROJECTION[10])
                val orientationColumn = data.getColumnIndexOrThrow(PROJECTION[11])
                data.moveToFirst()
                do {
                    val id = data.getLong(idColumn)
                    var mimeType = data.getString(mimeTypeColumn)
                    mimeType =
                        if (TextUtils.isEmpty(mimeType)) PictureMimeType.ofJPEG() else mimeType
                    val absolutePath = data.getString(dataColumn)
                    val url = if (isQ()) MediaUtils.getRealPathUri(
                        id,
                        mimeType
                    ) else absolutePath
                    // Here, it is solved that some models obtain mimeType and return the format of image / *,
                    // which makes it impossible to distinguish the specific type, such as mi 8,9,10 and other models
                    if (mimeType.endsWith("image/*")) {
                        mimeType = MediaUtils.getMimeTypeFromMediaUrl(absolutePath)
                        if (!config.isGif) {
                            if (PictureMimeType.isHasGif(mimeType)) {
                                continue
                            }
                        }
                    }
                    if (mimeType.endsWith("image/*")) {
                        continue
                    }
                    if (!config.isWebp) {
                        if (mimeType.startsWith(PictureMimeType.ofWEBP())) {
                            continue
                        }
                    }
                    if (!config.isBmp) {
                        if (mimeType.startsWith(PictureMimeType.ofBMP())) {
                            continue
                        }
                    }
                    if (!config.isMp4) {
                        if (mimeType.startsWith(PictureMimeType.ofMP4())) {
                            continue
                        }
                    }
                    var width = data.getInt(widthColumn)
                    var height = data.getInt(heightColumn)
                    val orientation = data.getInt(orientationColumn)
                    if (orientation == 90 || orientation == 270) {
                        width = data.getInt(heightColumn)
                        height = data.getInt(widthColumn)
                    }
                    val duration = data.getLong(durationColumn)
                    val size = data.getLong(sizeColumn)
                    val folderName = data.getString(folderNameColumn)
                    val fileName = data.getString(fileNameColumn)
                    val bucketId = data.getLong(bucketIdColumn)
                    "fileName=$fileName orientation:$orientation $width x $height size:$size duration:$duration".e()
//                    LogUtils.e("查询数据：size:$size--duration:$duration")
                    if (PictureMimeType.isHasVideo(mimeType) || PictureMimeType.isHasAudio(mimeType)) {
                        if (config.selectMinDurationSecond > 0 && duration < config.selectMinDurationSecond * 1000) {
                            // If you set the minimum number of seconds of video to display
                            continue
                        }
                        if (config.selectMaxDurationSecond > 0 && duration > config.selectMaxDurationSecond * 1000) {
                            // If you set the maximum number of seconds of video to display
                            continue
                        }
                        if (duration == 0L) {
                            //If the length is 0, the corrupted video is processed and filtered out
                            continue
                        }
                        if (size <= 0 || size > config.selectMaxFileSize) {
                            // The video size is 0 to filter out
                            continue
                        }
                    }
                    val media: LocalMedia = LocalMedia.parseLocalMedia(
                        id,
                        url,
                        absolutePath,
                        fileName,
                        folderName,
                        duration,
                        config.chooseMode,
                        mimeType,
                        width,
                        height,
                        size,
                        bucketId,
                        data.getLong(dateAddedColumn)
                    )
                    val folder: LocalMediaFolder =
                        getImageFolder(url, mimeType, folderName, imageFolders)
                    folder.bucketId = media.bucketId
                    folder.data?.add(media)
                    folder.folderTotalNum = folder.folderTotalNum + 1
                    folder.bucketId = media.bucketId
                    latelyImages.add(media)
                    val imageNum = allImageFolder.folderTotalNum
                    allImageFolder.folderTotalNum = imageNum + 1
                } while (data.moveToNext())
                val selfFolder: LocalMediaFolder? = SandboxFileLoader
                    .loadInAppSandboxFolderFile(CoreApp.instance, config.sandboxDir)
                if (selfFolder != null) {
                    imageFolders.add(selfFolder)
                    allImageFolder.folderTotalNum =
                        allImageFolder.folderTotalNum + selfFolder.folderTotalNum
                    allImageFolder.data = selfFolder.data
                    latelyImages.addAll(0, selfFolder.data ?: arrayListOf())
                    if (MAX_SORT_SIZE > selfFolder.folderTotalNum) {
                        if (latelyImages.size > MAX_SORT_SIZE) {
                            SortUtils.sortLocalMediaAddedTime(
                                latelyImages.subList(
                                    0,
                                    MAX_SORT_SIZE
                                )
                            )
                        } else {
                            SortUtils.sortLocalMediaAddedTime(latelyImages)
                        }
                    }
                }
                if (latelyImages.size > 0) {
                    SortUtils.sortFolder(imageFolders)
                    imageFolders.add(0, allImageFolder)
                    allImageFolder.firstImagePath = latelyImages[0].path
                    allImageFolder.firstMimeType = latelyImages[0].mimeType
                    val title: String =
                        if (config.chooseMode == SelectMimeType.ofAudio()) "所有音频" else "相机交卷"
                    allImageFolder.folderName = title
                    allImageFolder.bucketId = -1
                    allImageFolder.data = latelyImages
                }
            }

//            latelyImages.forEach {
//                Log.e("视频加载", "${it.toString()}")
//
//            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        videoListener?.invoke(latelyImages)
        if (data != null && !data.isClosed) {
            data.close()
        }
    }
    return latelyImages
}

/**
 * Create folder
 *
 * @param firstPath
 * @param firstMimeType
 * @param imageFolders
 * @param folderName
 * @return
 */
private fun getImageFolder(
    firstPath: String,
    firstMimeType: String,
    folderName: String,
    imageFolders: MutableList<LocalMediaFolder>
): LocalMediaFolder {
    for (folder in imageFolders) {
        // Under the same folder, return yourself, otherwise create a new folder
        val name = folder.folderName
        if (TextUtils.isEmpty(name)) {
            continue
        }
        if (TextUtils.equals(name, folderName)) {
            return folder
        }
    }
    val newFolder = LocalMediaFolder()
    newFolder.folderName = if (TextUtils.isEmpty(folderName)) "unknown" else folderName
    newFolder.firstImagePath = firstPath
    newFolder.firstMimeType = firstMimeType
    imageFolders.add(newFolder)
    return newFolder
}

fun isQ(): Boolean {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
}

fun getSortOrder(config: PictureSelectionConfig): String? {
    val sortOrder: String = if (TextUtils.isEmpty(config.sortOrder)) {
        ORDER_BY
    } else {
        config.sortOrder.orEmpty()
    }
    return sortOrder
}

fun getSelectionArgsForSingleMediaType(mediaType: Int): Array<String>? {
    return arrayOf(mediaType.toString())
}

private fun getSelectionArgsForVideoMediaCondition(
    durationCondition: String,
    queryMimeCondition: String
): String {
    return MediaStore.Files.FileColumns.MEDIA_TYPE + "=?" + queryMimeCondition + " AND " + durationCondition
}

/**
 * Get video (maximum or minimum time)
 *
 * @return
 */
fun getDurationCondition(config: PictureSelectionConfig): String? {
    val maxS =
        if (config.filterVideoMaxSecond == 0) Long.MAX_VALUE else config.filterVideoMaxSecond
    return java.lang.String.format(
        Locale.CHINA,
        "%d <%s $COLUMN_DURATION and $COLUMN_DURATION <= %d",
        max(0, config.filterVideoMinSecond),
        if (max(0, config.filterVideoMinSecond) == 0) "" else "=",
        maxS
    )
}

fun getFileSizeCondition(config: PictureSelectionConfig): String? {
    val maxS = if (config.filterMaxFileSize == 0L) Long.MAX_VALUE else config.filterMaxFileSize
    return String.format(
        Locale.CHINA,
        "%d <%s " + MediaStore.MediaColumns.SIZE + " and " + MediaStore.MediaColumns.SIZE + " <= %d",
        Math.max(0, config.filterMinFileSize),
        if (Math.max(0, config.filterMinFileSize) == 0L) "" else "=",
        maxS
    )
}

fun getQueryMimeCondition(config: PictureSelectionConfig): String? {
    val filters: List<String> = config.queryOnlyList ?: arrayListOf()
    val filterSet = HashSet(filters)
    val iterator: Iterator<String> = filterSet.iterator()
    val stringBuilder = StringBuilder()
    var index = -1
    while (iterator.hasNext()) {
        val value = iterator.next()
        if (TextUtils.isEmpty(value)) {
            continue
        }
        if (value.startsWith(MIME_TYPE_PREFIX_IMAGE) || value.startsWith(
                MIME_TYPE_PREFIX_AUDIO
            )
        ) {
            continue
        }
        index++
        stringBuilder.append(if (index == 0) " AND " else " OR ")
            .append(MediaStore.MediaColumns.MIME_TYPE).append("='").append(value).append("'")
    }
    return stringBuilder.toString()
}