package com.kotlin.tablet.event

import com.jeremyliao.liveeventbus.core.LiveEvent
import com.kotlin.android.router.bus.annotation.LiveEventKey

@LiveEventKey("live_event_key_notify_fragment")
class FilmListDetailsEvent(var title: String?, var isSelectId: Long? = 0) : LiveEvent