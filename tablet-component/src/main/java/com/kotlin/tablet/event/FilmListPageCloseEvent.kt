package com.kotlin.tablet.event

import com.jeremyliao.liveeventbus.core.LiveEvent
import com.kotlin.android.router.bus.annotation.LiveEventKey

@LiveEventKey("live_event_key_page_close")
class FilmListPageCloseEvent(
    var page: Int, //PAGE_SEARCH_ACTIVITY、PAGE_SELECTED_ACTIVITY、PAGE_SUCCESS_ACTIVITY
    var close: Boolean? = true//true关闭
) : LiveEvent