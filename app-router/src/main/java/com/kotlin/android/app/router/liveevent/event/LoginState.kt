package com.kotlin.android.app.router.liveevent.event

import android.content.Context
import android.os.Bundle
import com.jeremyliao.liveeventbus.core.LiveEvent
import java.lang.ref.WeakReference

/**
 * 创建者: zl
 * 创建时间: 2020/6/8 4:21 PM
 * 描述:既可以定义消息类，再发送；也可以不定义，直接发送。推荐使用定义类消息
 */
class LoginState(
        var isLogin: Boolean = false,
) : LiveEvent {
    var context: WeakReference<Context>? = null
    var bundle: Bundle? = null
    var requestCode: Int? = null
    var mTargetActivityId: String? = null
}
