package com.kotlin.android.publish.component.widget.selector

import android.provider.MediaStore
import android.content.ContentUris
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.InputStream
import java.lang.Exception
import java.net.URLConnection

object MediaUtils {
    /**
     * get uri
     *
     * @param id
     * @return
     */
    fun getRealPathUri(id: Long, mimeType: String?): String {
        val contentUri: Uri
        contentUri = if (PictureMimeType.isHasImage(mimeType)) {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        } else if (PictureMimeType.isHasVideo(mimeType)) {
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        } else if (PictureMimeType.isHasAudio(mimeType)) {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        } else {
            MediaStore.Files.getContentUri("external")
        }
        return ContentUris.withAppendedId(contentUri, id).toString()
    }

    /**
     * 获取mimeType
     *
     * @param url
     * @return
     */
    @JvmStatic
    fun getMimeTypeFromMediaUrl(url: String?): String {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(url)
        var mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(
            fileExtension.toLowerCase()
        )
        if (TextUtils.isEmpty(mimeType)) {
            mimeType = getMimeType(File(url))
        }
        return if (TextUtils.isEmpty(mimeType)) PictureMimeType.MIME_TYPE_JPEG else mimeType.orEmpty()
    }

    /**
     * 获取mimeType
     *
     * @param file
     * @return
     */
    private fun getMimeType(file: File): String {
        val fileNameMap = URLConnection.getFileNameMap()
        return fileNameMap.getContentTypeFor(file.name)
    }

    /**
     * 是否是长图
     *
     * @param width  图片宽度
     * @param height 图片高度
     * @return
     */
    fun isLongImage(width: Int, height: Int): Boolean {
        return if (width <= 0 || height <= 0) {
            false
        } else height > width * 3
    }

    /**
     * 生成BucketId
     *
     * @param context        上下文
     * @param cameraFile     拍照资源文件
     * @param outPutAudioDir 自定义拍照输出目录
     * @return
     */
    fun generateSoundsBucketId(context: Context, cameraFile: File, outPutAudioDir: String?): Long {
        val bucketId: Long
        bucketId = if (TextUtils.isEmpty(outPutAudioDir)) {
            getFirstBucketId(
                context,
                cameraFile.parent
            )
        } else {
            if (cameraFile.parentFile != null) {
                cameraFile.parentFile.name.hashCode().toLong()
            } else {
                getFirstBucketId(
                    context,
                    cameraFile.parent
                )
            }
        }
        return bucketId
    }

    /**
     * 创建目录名
     *
     * @param absolutePath 资源路径
     * @return
     */
    fun generateCameraFolderName(absolutePath: String?): String {
        val folderName: String
        val cameraFile = File(absolutePath)
        folderName = if (cameraFile.parentFile != null) {
            cameraFile.parentFile.name
        } else {
            PictureMimeType.CAMERA
        }
        return folderName
    }

    /**
     * get Local image width or height
     *
     * @param url
     * @return
     */
    @JvmStatic
    fun getImageSize(context: Context, url: String?): MediaExtraInfo {
        val mediaExtraInfo = MediaExtraInfo()
        var inputStream: InputStream? = null
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            inputStream = if (PictureMimeType.isContent(url.orEmpty())) {
                getContentResolverOpenInputStream(context, Uri.parse(url))
            } else {
                FileInputStream(url)
            }
            BitmapFactory.decodeStream(inputStream, null, options)
            mediaExtraInfo.width = options.outWidth
            mediaExtraInfo.height = options.outHeight
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            close(inputStream)
        }
        return mediaExtraInfo
    }

    fun getContentResolverOpenInputStream(context: Context, uri: Uri?): InputStream? {
        try {
            return context.contentResolver.openInputStream(uri!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    fun close(c: Closeable?) {
        // java.lang.IncompatibleClassChangeError: interface not implemented
        if (c is Closeable) {
            try {
                c.close()
            } catch (e: Exception) {
                // silence
            }
        }
    }

    /**
     * get Local video width or height
     *
     * @param context
     * @param url
     * @return
     */
    @JvmStatic
    fun getVideoSize(context: Context?, url: String?): MediaExtraInfo {
        val mediaExtraInfo = MediaExtraInfo()
        val retriever = MediaMetadataRetriever()
        try {
            if (PictureMimeType.isContent(url.orEmpty())) {
                retriever.setDataSource(context, Uri.parse(url))
            } else {
                retriever.setDataSource(url)
            }
            val orientation =
                retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
            val width: Int
            val height: Int
            if (TextUtils.equals("90", orientation) || TextUtils.equals("270", orientation)) {
                height = toInt(
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH),
                    0
                )
                width = toInt(
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT),
                    0
                )
            } else {
                width = toInt(
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH),
                    0
                )
                height = toInt(
                    retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT),
                    0
                )
            }
            mediaExtraInfo.width = width
            mediaExtraInfo.height = height
            mediaExtraInfo.orientation = orientation
            mediaExtraInfo.duration =
                toLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION), 0L)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }
        return mediaExtraInfo
    }

    fun toLong(o: Any?, defaultValue: Long): Long {
        if (o == null) {
            return defaultValue
        }
        var value: Long = 0
        value = try {
            val s = o.toString().trim { it <= ' ' }
            if (s.contains(".")) {
                s.substring(0, s.lastIndexOf(".")).toLong()
            } else {
                s.toLong()
            }
        } catch (e: Exception) {
            defaultValue
        }
        return value
    }

    fun toInt(o: Any?, defaultValue: Int): Int {
        if (o == null) {
            return defaultValue
        }
        val value: Int
        value = try {
            val s = o.toString().trim { it <= ' ' }
            if (s.contains(".")) {
                s.substring(0, s.lastIndexOf(".")).toInt()
            } else {
                s.toInt()
            }
        } catch (e: Exception) {
            defaultValue
        }
        return value
    }

    /**
     * get Local video width or height
     *
     * @param context
     * @param url
     * @return
     */
    @JvmStatic
    fun getAudioSize(context: Context?, url: String?): MediaExtraInfo {
        val mediaExtraInfo = MediaExtraInfo()
        val retriever = MediaMetadataRetriever()
        try {
            if (PictureMimeType.isContent(url.orEmpty())) {
                retriever.setDataSource(context, Uri.parse(url))
            } else {
                retriever.setDataSource(url)
            }
            mediaExtraInfo.duration =
                toLong(retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION), 0L)
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            retriever.release()
        }
        return mediaExtraInfo
    }

    /**
     * 删除部分手机 拍照在DCIM也生成一张的问题
     *
     * @param id
     */
    fun removeMedia(context: Context, id: Int) {
        try {
            val cr = context.applicationContext.contentResolver
            val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val selection = MediaStore.Images.Media._ID + "=?"
            cr.delete(uri, selection, arrayOf(java.lang.Long.toString(id.toLong())))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 获取最新一条拍照记录
     *
     * @return
     */
    fun getFirstBucketId(context: Context, absoluteDir: String): Long {
        var data: Cursor? = null
        try {
            //selection: 指定查询条件
            val selection = MediaStore.Files.FileColumns.DATA + " like ?"
            //定义selectionArgs：
            val selectionArgs = arrayOf("%$absoluteDir%")
            data = if (isR) {
                val queryArgs = createQueryArgsBundle(
                    selection,
                    selectionArgs,
                    1,
                    0,
                    MediaStore.Files.FileColumns._ID + " DESC"
                )
                context.applicationContext.contentResolver.query(
                    MediaStore.Files.getContentUri("external"),
                    null,
                    queryArgs,
                    null
                )
            } else {
                val orderBy = MediaStore.Files.FileColumns._ID + " DESC limit 1 offset 0"
                context.applicationContext.contentResolver.query(
                    MediaStore.Files.getContentUri("external"),
                    null,
                    selection,
                    selectionArgs,
                    orderBy
                )
            }
            if (data != null && data.count > 0 && data.moveToFirst()) {
                return data.getLong(data.getColumnIndex("bucket_id"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            data?.close()
        }
        return -1
    }

    val isR: Boolean
        get() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R

    /**
     * Key for an SQL style `LIMIT` string that may be present in the
     * query Bundle argument passed to
     * [ContentProvider.query].
     *
     *
     * **Apps targeting [Build.VERSION_CODES.O] or higher are strongly
     * encourage to use structured query arguments in lieu of opaque SQL query clauses.**
     *
     * @see .QUERY_ARG_LIMIT
     *
     * @see .QUERY_ARG_OFFSET
     */
    const val QUERY_ARG_SQL_LIMIT = "android:query-arg-sql-limit"

    /**
     * R  createQueryArgsBundle
     *
     * @param selection
     * @param selectionArgs
     * @param limitCount
     * @param offset
     * @return
     */
    fun createQueryArgsBundle(
        selection: String?,
        selectionArgs: Array<String>?,
        limitCount: Int,
        offset: Int,
        orderBy: String?
    ): Bundle {
        val queryArgs = Bundle()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SELECTION, selection)
            queryArgs.putStringArray(ContentResolver.QUERY_ARG_SQL_SELECTION_ARGS, selectionArgs)
            queryArgs.putString(ContentResolver.QUERY_ARG_SQL_SORT_ORDER, orderBy)
            if (isR) {
                queryArgs.putString(
                    ContentResolver.QUERY_ARG_SQL_LIMIT,
                    "$limitCount offset $offset"
                )
            }
        }
        return queryArgs
    }
    /**
     * 异步获取视频缩略图地址
     *
     * @param context
     * @param url
     * @param call
     * @return
     */
    //    public static void getAsyncVideoThumbnail(Context context, String url, OnCallbackListener<MediaExtraInfo> call) {
    //        PictureThreadUtils.executeByIo(new PictureThreadUtils.SimpleTask<MediaExtraInfo>() {
    //
    //            @Override
    //            public MediaExtraInfo doInBackground() {
    //                return getVideoThumbnail(context, url);
    //            }
    //
    //            @Override
    //            public void onSuccess(MediaExtraInfo result) {
    //                PictureThreadUtils.cancel(this);
    //                if (call != null) {
    //                    call.onCall(result);
    //                }
    //            }
    //        });
    //    }
    /**
     * 获取视频缩略图地址
     *
     * @param context
     * @param url
     * @return
     */
    //    public static MediaExtraInfo getVideoThumbnail(Context context, String url) {
    //        Bitmap bitmap = null;
    //        ByteArrayOutputStream stream = null;
    //        FileOutputStream fos = null;
    //        MediaExtraInfo extraInfo = new MediaExtraInfo();
    //        try {
    //            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
    //            if (PictureMimeType.isContent(url)) {
    //                mmr.setDataSource(context, Uri.parse(url));
    //            } else {
    //                mmr.setDataSource(url);
    //            }
    //            bitmap = mmr.getFrameAtTime();
    //            if (bitmap != null && !bitmap.isRecycled()) {
    //                stream = new ByteArrayOutputStream();
    //                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);
    //                String videoThumbnailDir = PictureFileUtils.getVideoThumbnailDir(context);
    //                File targetFile = new File(videoThumbnailDir, DateUtils.getCreateFileName("vid_") + "_thumb.jpg");
    //                fos = new FileOutputStream(targetFile);
    //                fos.write(stream.toByteArray());
    //                fos.flush();
    //                extraInfo.setVideoThumbnail(targetFile.getAbsolutePath());
    //                extraInfo.setWidth(bitmap.getWidth());
    //                extraInfo.setHeight(bitmap.getHeight());
    //            }
    //        } catch (IOException e) {
    //            e.printStackTrace();
    //        } finally {
    //            PictureFileUtils.close(stream);
    //            PictureFileUtils.close(fos);
    //            if (bitmap != null && !bitmap.isRecycled()) {
    //                bitmap.recycle();
    //            }
    //        }
    //        return extraInfo;
    //    }
    /**
     * delete camera PATH
     *
     * @param context Context
     * @param path    path
     */
    fun deleteUri(context: Context, path: String?) {
        try {
            if (PictureMimeType.isContent(path.orEmpty())) {
                context.contentResolver.delete(Uri.parse(path), null, null)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}