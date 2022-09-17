package com.kotlin.android.live.component.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.CommonResultExtend
import com.kotlin.android.app.data.entity.live.LiveAppointResult
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.ui.fragment.list.adapter.LiveListItemBinder
import com.kotlin.android.live.component.ui.fragment.list.adapter.LiveListTitleBinder
import com.kotlin.android.live.component.viewbean.LiveListInfoBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/3/4
 * 直播列表
 */
class LiveListRepository : BaseRepository() {

    /**
     * 直播列表页总接口
     */
    suspend fun getLiveList(): ApiResult<List<MultiTypeBinder<*>>> {
        return request(
            converter = {
                val list = mutableListOf<MultiTypeBinder<*>>()
                if (!it.livings.isNullOrEmpty()) {
                    it.livings?.run {
                        list.add(
                            LiveListTitleBinder(
                                R.drawable.ic_live_list_title_bg1,
                                R.string.live_component_live_list_title_living
                            )
                        )
                        list.addAll(map { liveInfo ->
                            LiveListItemBinder(
                                LiveListInfoBean.converter(liveInfo)
                            )
                        })
                    }
                }
                if (!it.livePreviews.isNullOrEmpty()) {
                    it.livePreviews?.run {
                        list.add(
                            LiveListTitleBinder(
                                R.drawable.ic_live_list_title_bg2,
                                R.string.live_component_live_list_title_preview
                            )
                        )
                        list.addAll(map { liveInfo ->
                            LiveListItemBinder(
                                LiveListInfoBean.converter(liveInfo)
                            )
                        })
                    }
                }
                if (!it.wonderVods.isNullOrEmpty()) {
                    it.wonderVods?.run {
                        list.add(
                            LiveListTitleBinder(
                                R.drawable.ic_live_list_title_bg3,
                                R.string.live_component_live_list_title_end
                            )
                        )
                        list.addAll(map { liveInfo ->
                            LiveListItemBinder(
                                LiveListInfoBean.converter(liveInfo)
                            )
                        })
                    }
                }
                list
            },
            api = {
                apiMTimeLive.getLiveList()
            })
    }

    /**
     * 精彩回放模块（/live_room/getWonderVodList）
     */
    suspend fun getLiveWonderVodList(
        pageNo: Long,
        pageSize: Long
    ): ApiResult<List<MultiTypeBinder<*>>> {
        return request(
            converter = {
                val list = mutableListOf<MultiTypeBinder<*>>()
                it.wonderVods?.run {
                    list.addAll(map { liveInfo ->
                        LiveListItemBinder(
                            LiveListInfoBean.converter(liveInfo)
                        )
                    })
                }
                list
            },
            api = {
                apiMTimeLive.getLiveWonderVodList(pageNo, pageSize)
            })
    }

    /**
     * 直播预约（/live_room/appoint）
     */
    suspend fun <T> getLiveAppoint(
        liveId: Long,
        extend: T
    ): ApiResult<CommonResultExtend<LiveAppointResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend
                )
            },
            api = {
                apiMTimeLive.getLiveAppoint(liveId)
            })
    }

}