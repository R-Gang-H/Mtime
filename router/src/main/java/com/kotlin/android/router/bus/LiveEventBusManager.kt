package com.kotlin.android.router.bus

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.jeremyliao.liveeventbus.LiveEventBus
import com.jeremyliao.liveeventbus.core.LiveEvent
import com.jeremyliao.liveeventbus.core.Observable
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.router.bus.annotation.LiveEventKey

/**
 * 感知生命周期的事件总线管理器:
 * event 对象需继承自 [LiveEvent]，并添加事件注解 [LiveEventKey] 指定唯一key值 [LiveEventKey.key]
 * 使用 [post] 发送事件。并注册 [observe] 观察者观察事件对象的到达。
 *
 * Created on 2021/4/23.
 *
 * @author o.s
 */
class LiveEventBusManager private constructor() {

    companion object {
        val instance by lazy { LiveEventBusManager() }
    }

    /**
     * 根据事件对象类型 Class 获取观察者
     */
    fun <T> with(clazz: Class<T>): Observable<T> {
        return clazz.getAnnotation(LiveEventKey::class.java).run {
            LiveEventBus.get(key, clazz)
        }
    }

    /**
     * 观察 [clazz] 指定是事件
     */
    fun <T : LiveEvent> observe(clazz: Class<T>, owner: LifecycleOwner, observer: Observer<T>) {
        with(clazz).observe(owner, observer)
    }

    /**
     * 发送 [event] 指定的事件
     */
    fun post(event: LiveEvent) {
        event.javaClass.getAnnotation(LiveEventKey::class.java)?.apply {
            LiveEventBus.get(key, event.javaClass).post(event)
        } ?: "${event.javaClass} not EventKey annotation".e()
    }

}