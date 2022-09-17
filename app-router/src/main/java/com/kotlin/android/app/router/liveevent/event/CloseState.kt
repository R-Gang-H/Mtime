package com.kotlin.android.app.router.liveevent.event

import com.jeremyliao.liveeventbus.core.LiveEvent
import com.kotlin.android.router.bus.annotation.LiveEventKey

/**
 * create by lushan on 2022/4/18
 * des:
 **/
@LiveEventKey("close_current_activity")
class CloseState():LiveEvent