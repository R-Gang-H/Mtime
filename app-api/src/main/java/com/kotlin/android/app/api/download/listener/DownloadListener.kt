package com.kotlin.android.app.api.download.listener

/**
 * create by lushan on 2021/1/28
 * description:
 */
interface DownloadListener {
    fun onProgress(progress:Int)
    fun onFailed(msg:String?)
    fun onComplete(filePath:String)
}