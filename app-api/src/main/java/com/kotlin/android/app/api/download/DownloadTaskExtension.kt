/*
 * Copyright (c) 2018 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kotlin.android.app.api.download

import com.kotlin.android.app.api.download.listener.*
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.kotlin.listener.*
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.channels.Channel
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Correspond to [DownloadTask.execute].
 * This method will create a [com.liulishuo.okdownload.DownloadListener] instance internally.
 */
fun DownloadTask.execute(
        onTaskStart: onTaskStart? = null,
        onConnectTrialStart: onConnectTrialStart? = null,
        onConnectTrialEnd: onConnectTrialEnd? = null,
        onDownloadFromBeginning: onDownloadFromBeginning? = null,
        onDownloadFromBreakpoint: onDownloadFromBreakpoint? = null,
        onConnectStart: onConnectStart? = null,
        onConnectEnd: onConnectEnd? = null,
        onFetchStart: onFetchStart? = null,
        onFetchProgress: onFetchProgress? = null,
        onFetchEnd: onFetchEnd? = null,
        onTaskEnd: onTaskEnd
) = execute(createListener(
    onTaskStart,
    onConnectTrialStart,
    onConnectTrialEnd,
    onDownloadFromBeginning,
    onDownloadFromBreakpoint,
    onConnectStart,
    onConnectEnd,
    onFetchStart,
    onFetchProgress,
    onFetchEnd,
    onTaskEnd
))


/**
 * Correspond to [DownloadTask.enqueue].
 * This method will create a [com.liulishuo.okdownload.DownloadListener] instance internally.
 */
fun DownloadTask.enqueue(
        onTaskStart: onTaskStart? = null,
        onConnectTrialStart: onConnectTrialStart? = null,
        onConnectTrialEnd: onConnectTrialEnd? = null,
        onDownloadFromBeginning: onDownloadFromBeginning? = null,
        onDownloadFromBreakpoint: onDownloadFromBreakpoint? = null,
        onConnectStart: onConnectStart? = null,
        onConnectEnd: onConnectEnd? = null,
        onFetchStart: onFetchStart? = null,
        onFetchProgress: onFetchProgress? = null,
        onFetchEnd: onFetchEnd? = null,
        onTaskEnd: onTaskEnd
) = enqueue(createListener(
    onTaskStart,
    onConnectTrialStart,
    onConnectTrialEnd,
    onDownloadFromBeginning,
    onDownloadFromBreakpoint,
    onConnectStart,
    onConnectEnd,
    onFetchStart,
    onFetchProgress,
    onFetchEnd,
    onTaskEnd
))

/**
 * Correspond to [DownloadTask.enqueue].
 * This method will create a [com.liulishuo.okdownload.core.listener.DownloadListener4WithSpeed]
 * instance internally.
 */
fun DownloadTask.enqueue4WithSpeed(
        onTaskStart: onTaskStart? = null,
        onConnectStart: onConnectStart? = null,
        onConnectEnd: onConnectEnd? = null,
        onInfoReadyWithSpeed: onInfoReadyWithSpeed? = null,
        onProgressBlockWithSpeed: onProgressBlockWithSpeed? = null,
        onProgressWithSpeed: onProgressWithSpeed? = null,
        onBlockEndWithSpeed: onBlockEndWithSpeed? = null,
        onTaskEndWithSpeed: onTaskEndWithSpeed
) = enqueue(createListenerWithSpeed(
    onTaskStart,
    onConnectStart,
    onConnectEnd,
    onInfoReadyWithSpeed,
    onProgressBlockWithSpeed,
    onProgressWithSpeed,
    onBlockEndWithSpeed,
    onTaskEndWithSpeed
))

/**
 * This method will create a [Channel] to represents a single download task's progress.
 * In this way, the DownloadTask is a producer and the returned [Channel] can be consumed
 * by specific consumer.
 *
 * Note: This method must be invoked after the DownloadTask is started, otherwise, there is no any
 * effect. For example:
 * ```
 * val task = DownloadTask.Builder().build()
 * task.enqueue() { task, cause, realCause ->   }
 *
 * val progressChannel = task.spChannel()
 * runBlocking {
 *     for (dp in progressChannel) {
 *         // show progress
 *     }
 * }
 * ```
 */
fun DownloadTask.spChannel(): Channel<DownloadProgress> {
    val channel = Channel<DownloadProgress>(Channel.CONFLATED)
    val oldListener = listener
    val channelClosed = AtomicBoolean(false)
    val progressListener = createNormalListener(
        progress = { task, currentOffset, totalLength ->
            if (channelClosed.get()) return@createNormalListener
            channel.offer(DownloadProgress(task, currentOffset, totalLength))
        }
    ) { _, _, _, _ ->
        channelClosed.set(true)
        channel.close()
    }.also { it.setAlwaysRecoverAssistModelIfNotSet(true) }
    val replaceListener = createReplaceListener(oldListener, progressListener)
    replaceListener(replaceListener)
    return channel
}

/**
 * Returns a [DownloadListener] to replace old listener in [DownloadTask].
 * @param oldListener responses all callbacks except progress.
 * @param progressListener only responses progress callback.
 */
internal fun createReplaceListener(
    oldListener: DownloadListener?,
    progressListener: DownloadListener
): DownloadListener {
    if (oldListener == null) {
        return progressListener
    }
    val exceptProgressListener = oldListener.switchToExceptProgressListener()
    return createListener(
        onTaskStart = {
            exceptProgressListener.taskStart(it)
            progressListener.taskStart(it)
        },
        onConnectTrialStart = { task, requestFields ->
            exceptProgressListener.connectTrialStart(task, requestFields)
        },
        onConnectTrialEnd = { task, responseCode, responseHeaderFields ->
            exceptProgressListener.connectTrialEnd(task, responseCode, responseHeaderFields)
        },
        onDownloadFromBeginning = { task, info, cause ->
            exceptProgressListener.downloadFromBeginning(task, info, cause)
            progressListener.downloadFromBeginning(task, info, cause)
        },
        onDownloadFromBreakpoint = { task, info ->
            exceptProgressListener.downloadFromBreakpoint(task, info)
            progressListener.downloadFromBreakpoint(task, info)
        },
        onConnectStart = { task, blockIndex, requestHeaderFields ->
            exceptProgressListener.connectStart(task, blockIndex, requestHeaderFields)
        },
        onConnectEnd = { task, blockIndex, responseCode, responseHeaderFields ->
            exceptProgressListener.connectEnd(task, blockIndex, responseCode, responseHeaderFields)
        },
        onFetchStart = { task, blockIndex, contentLength ->
            exceptProgressListener.fetchStart(task, blockIndex, contentLength)
        },
        onFetchEnd = { task, blockIndex, contentLength ->
            exceptProgressListener.fetchEnd(task, blockIndex, contentLength)
        },
        onFetchProgress = { task, blockIndex, increaseBytes ->
            progressListener.fetchProgress(task, blockIndex, increaseBytes)
        }
    ) { task, cause, realCause ->
        exceptProgressListener.taskEnd(task, cause, realCause)
        progressListener.taskEnd(task, cause, realCause)
    }
}

internal fun CancellableContinuation<*>.cancelDownloadOnCancellation(task: DownloadTask) =
    invokeOnCancellation { task.cancel() }