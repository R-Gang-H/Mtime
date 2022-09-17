package com.kotlin.android.live.component.ui.fragment.list.adapter

import android.view.View
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.setTextWithFormat
import com.kotlin.android.live.component.R
import com.kotlin.android.live.component.constant.*
import com.kotlin.android.live.component.databinding.ItemLiveListBinding
import com.kotlin.android.live.component.viewbean.LiveListInfoBean
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.live.ILiveProvider
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/3/4
 */
class LiveListItemBinder(val itemData: LiveListInfoBean) : MultiTypeBinder<ItemLiveListBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_live_list
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is LiveListItemBinder && other.itemData.appointStatus != itemData.appointStatus
    }

    override fun onBindViewHolder(binding: ItemLiveListBinding, position: Int) {
        binding.run {
            mLiveTagTv.setText(
                if (itemData.liveStatus == LIVE_STATUS_LIVING) {
                    R.string.live_component_live_tag_living
                } else {
                    R.string.live_component_live_tag_replay
                }
            )

            mLiveAppointBtnTv.run {
                if (itemData.appointStatus == LIVE_UN_APPOINT) {
                    setBackground(
                        colorRes = R.color.color_feb12a,
                        cornerRadius = 27.dpF
                    )
                    setTextColorRes(R.color.color_ffffff)
                    setText(R.string.live_component_live_appoint)
                } else {
                    setBackground(
                        strokeColorRes = R.color.color_feb12a,
                        strokeWidth = 1,
                        cornerRadius = 27.dpF
                    )
                    setTextColorRes(R.color.color_feb12a)
                    setText(R.string.live_component_live_appoint_done)
                }
            }

            mLiveNumberTv.run {
                when (itemData.liveStatus) {
                    LIVE_STATUS_LIVING -> {
                        setTextWithFormat(
                            R.string.live_component_live_list_living_num_format,
                            itemData.statistic
                        )
                    }
                    LIVE_STATUS_APPOINT -> {
                        setTextWithFormat(
                            R.string.live_component_live_list_appoint_num_format,
                            itemData.statistic
                        )
                    }
                    LIVE_STATUS_END, LIVE_STATUS_REVIEW -> {
                        setTextWithFormat(
                            R.string.live_component_live_list_end_num_format,
                            itemData.statistic
                        )
                    }
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mItemRoot -> {
                afterLogin {
                    //跳转到直播详情页面
                    getProvider(ILiveProvider::class.java)
                        ?.launchLiveDetail(itemData.liveId)
                }
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}