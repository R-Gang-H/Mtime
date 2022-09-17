package com.kotlin.android.publish.component.scope

import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.community.content.CheckReleasedResult
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.publish.component.repo.EditorRepository
import kotlinx.coroutines.*

/**
 * 文章ID获取发布的文章
 *
 * Created on 2022/4/28.
 *
 * @author o.s
 */
class ArticleIDScope private constructor() {

    companion object {
        val instance by lazy { ArticleIDScope() }
    }
    val repo by lazy { EditorRepository() }

    fun checkReleased(
        type: Long = CommConstant.CHECK_RELEASED_CONTENT_TYPE_ARTICLE,
        contentId: Long,
        error: ((String?) -> Unit)? = null,
        netError: ((String) -> Unit)? = null,
        success: CheckReleasedResult.() -> Unit
    ) {
        MainScope().let { main ->
            main.launch(Dispatchers.Main) {
                val deferred = async(Dispatchers.IO) {
                    repo.checkReleased(
                        type = type,
                        contentId = contentId
                    )
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