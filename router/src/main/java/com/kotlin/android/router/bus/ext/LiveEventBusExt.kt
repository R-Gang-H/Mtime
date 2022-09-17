package com.kotlin.android.router.bus.ext

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.core.LiveEvent
import com.kotlin.android.router.bus.LiveEventBusManager

/**
 * 感知生命周期的事件总线扩展：
 *
 * Created on 2021/4/23.
 *
 * @author o.s
 */

/**
 * 发送事件扩展
 */
fun LiveEvent.post() {
    LiveEventBusManager.instance.post(this)
//    javaClass.getAnnotation(LiveEventKey::class.kotlin)?.let {
//        LiveEventBus.get(it.key, javaClass).post(this)
//    } ?: "$javaClass not EventKey annotation".e()
}

/**
 * 观察事件扩展
 */
fun <T : LiveEvent> LifecycleOwner.observe(clazz: Class<T>, observer: Observer<T>) {
    LiveEventBusManager.instance.observe(clazz, this, observer)
}
