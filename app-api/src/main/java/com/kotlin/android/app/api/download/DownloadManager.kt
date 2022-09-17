package com.kotlin.android.app.api.download

import android.text.TextUtils
import com.kotlin.android.app.api.download.listener.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.SpeedCalculator
import com.liulishuo.okdownload.core.Util
import com.liulishuo.okdownload.core.cause.EndCause
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import java.io.File

/**
 * create by lushan on 2021/1/28
 * description:
 */
object DownloadManager {


    /**
     * 下载文件
     * @param fileName 文件名 xx.apk
     * @param filePath 文件存放路径
     */
    fun download(url: String, fileName: String, filePath: String, downloadListener: DownloadListener?) {
        if (TextUtils.isEmpty(url)) {
            return
        }
        var file = File(filePath + fileName)
        if (file.exists()){
            file.delete()
        }
        var task = DownloadTask.Builder(url, File(filePath))
                .setFilename(fileName)
                // the minimal interval millisecond for callback progress
                .setMinIntervalMillisCallbackProcess(16)
                // ignore the same task has already completed in the past.
                .setPassIfAlreadyCompleted(false)
                .build()

        if (task.tag != null) {
            // to cancel
            task.cancel()
        } else
        // to start
            startAction(filePath + fileName, task, downloadListener)
        task.tag = "mark-task-started"
    }


    private fun startAction(filePathName: String, originTask: DownloadTask?, downloadListener: DownloadListener?) {

        var totalLength: Long = 0
        var readableTotalLength: String? = null
        originTask?.enqueue4WithSpeed(
                onTaskStart = {
//                              开始下载
//                    "enqueue4WithSpeed开始下载：".e()
                    downloadListener?.onProgress(0)
                },
                onInfoReadyWithSpeed = { _, info, _, _ ->
//                    准备下载
                    totalLength = info.totalLength
                    readableTotalLength = Util.humanReadableBytes(totalLength, true)
                    val progress = if (totalLength != 0L) (100 * info.totalOffset.toFloat() / totalLength).toInt() else 0
//                    "enqueue4WithSpeed：$progress".e()
                    downloadListener?.onProgress(progress)
                },
                onConnectStart = { _, blockIndex, _ ->
                    val status = "Connect End $blockIndex"
//                    "链接状态：$status".e()
                }
        ) { task, cause, realCause, taskSpeed ->
            val statusWithSpeed = cause.toString() + " " + taskSpeed.averageSpeed()
            // remove mark
            task.tag = null
//            "下载完成:$cause".e()
            if (cause == EndCause.COMPLETED) {
//                "下载完成".e()
                downloadListener?.onComplete(filePathName)
                originTask?.cancel()
            }
            realCause?.let {
//                "下载失败：${it.message}".e()
                downloadListener?.onFailed(it.message.orEmpty())
            }
        }

        // Second way to show progress.
        val speedCalculator = SpeedCalculator()
        CoroutineScope(Dispatchers.Main).launch {
            var lastOffset = 0L
            originTask?.spChannel()?.consumeEach { dp ->
                val increase = when (lastOffset) {
                    0L -> 0L
                    else -> dp.currentOffset - lastOffset
                }
                lastOffset = dp.currentOffset
                speedCalculator.downloading(increase)
                val readableOffset = Util.humanReadableBytes(dp.currentOffset, true)
                val progressStatus = "$readableOffset/$readableTotalLength"
                val speed = speedCalculator.speed()
                val progressStatusWithSpeed = "$progressStatus($speed)"
//                网速
                val progress = if (totalLength != 0L) (100 * dp.currentOffset.toFloat() / totalLength).toInt() else 0
//                "enqueue4WithSpeed：$progress".e()
                downloadListener?.onProgress(progress)
                if (progress >= 100) {
                    downloadListener?.onComplete(filePathName)
                    originTask?.cancel()
                }
            }
        }
    }

}

