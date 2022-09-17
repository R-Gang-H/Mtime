package com.kotlin.android.user.login.jguang

import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.user.Login
import com.kotlin.android.ktx.ext.log.i
import com.kotlin.android.user.repository.UserRepository
import kotlinx.coroutines.*

/**
 * 极光一键登录协程
 *
 * Created on 2021/4/9.
 *
 * @author o.s
 */
class JLoginScope private constructor() /** : CoroutineScope by MainScope() */ {

    companion object {
        val instance by lazy { JLoginScope() }
    }

    val repo by lazy { UserRepository() }

    var scope: CoroutineScope? = null

    fun jVerifyLogin(
            loginToken: String,
            error: (String?) -> Unit,
            netError: (String) -> Unit,
            success: Login.() -> Unit,
    ) {
        scope = MainScope().apply {
            launch(Dispatchers.Main) {
                val deferred = async(Dispatchers.IO) {
                    repo.jVerifyLogin(loginToken)
                }

                val result = deferred.await()

                result.i()

                checkResult(
                        result = result,
                        error = {
                            error.invoke(it)
                        },
                        netError = {
                            netError.invoke(it)
                        },
                        success = {
//                        loginUIState.value = it
                            success.invoke(it)
                        }
                )

                // 取消协程
                onCleared()
            }
        }
    }

    fun onCleared() {
        "取消协程".i()
        scope?.cancel()
    }
}