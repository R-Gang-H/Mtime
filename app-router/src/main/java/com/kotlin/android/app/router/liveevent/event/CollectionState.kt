package com.kotlin.android.app.router.liveevent.event

import com.jeremyliao.liveeventbus.core.LiveEvent

/**
 * create by lushan on 2020/11/16
 * description: 收藏列表页面更新刷新
 */

data class CollectionState(var collectionType:Long):LiveEvent
