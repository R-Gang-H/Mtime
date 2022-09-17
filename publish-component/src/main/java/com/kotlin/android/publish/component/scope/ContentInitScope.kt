package com.kotlin.android.publish.component.scope

import com.kotlin.android.api.base.checkResult
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.publish.component.repo.EditorRepository
import kotlinx.coroutines.*

/**
 * 发布相关：
 * 1，ID获取电影名称/电影url
 * 2，ID获取影人名称
 * 3，ID获取家族名称
 *
 * Created on 2022/4/28.
 *
 * @author o.s
 */
class ContentInitScope private constructor() {

    companion object {
        val instance by lazy { ContentInitScope() }
    }
    val repo by lazy { EditorRepository() }

    /**
     * ID获取电影名称
     */
    fun getMovieName(
        movieId: Long,
        error: ((String?) -> Unit)? = null,
        netError: ((String) -> Unit)? = null,
        success: String.() -> Unit
    ) {
        MainScope().let { main ->
            main.launch(Dispatchers.Main) {
                val deferred = async(Dispatchers.IO) {
                    repo.getMovieName(movieId = movieId)
                }

                val result = deferred.await()

                result.i()

                checkResult(
                    result = result,
                    error = {
                        error?.invoke(it)
                    },
                    netError = {
                        netError?.invoke(it)
                    },
                    success = {
                        success.invoke(it)
                    }
                )

                // 取消协程
                main.cancel()
            }
        }
    }

    /**
     * ID获取电影url
     */
    fun getMovieUrl(
        movieId: Long,
        error: ((String?) -> Unit)? = null,
        netError: ((String) -> Unit)? = null,
        success: String.() -> Unit
    ) {
        MainScope().let { main ->
            main.launch(Dispatchers.Main) {
                val deferred = async(Dispatchers.IO) {
                    repo.getMovieUrl(movieId = movieId)
                }

                val result = deferred.await()

                result.i()

                checkResult(
                    result = result,
                    error = {
                        error?.invoke(it)
                    },
                    netError = {
                        netError?.invoke(it)
                    },
                    success = {
                        success.invoke(it)
                    }
                )

                // 取消协程
                main.cancel()
            }
        }
    }
}