package com.kotlin.android.mine.bean

import android.content.Context
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.activity.Activity
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mine.ui.activity.adapter.ActivityItemBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/3/21
 * 描述:
 */
data class ActivityViewBean(
        var introId: Long = 0L,       // 活动ID
        var name: String = "",        // 活动名称
        var placardUrl: String = "",  // 活动海报
        var appSkipType: Long = 0L,   // 相关链接-下拉框类型，1：H5、2：AppLink
        var appSkipLink:String = "",  // 相关链接
): ProguardRule {

    companion object {

        /**
         * 转换ViewBean
         */
        private fun objectToViewBean(bean: Activity): ActivityViewBean {
            return ActivityViewBean(
                    introId = bean.introId.orZero(),
                    name = bean.name.orEmpty(),
                    placardUrl = bean.placardUrl.orEmpty(),
                    appSkipType = bean.appSkip?.type.orZero(),
                    appSkipLink = bean.appSkip?.appLink.orEmpty(),
            )
        }

        /**
         * 我的-个人中心-活动模块/活动页 BinderList
         */
        fun build(context: Context, beans: List<Activity>?, isMine: Boolean = false) : MutableList<MultiTypeBinder<*>> {
            val binderList = mutableListOf<MultiTypeBinder<*>>()
            beans?.let { list ->
                list.map {
                    val viewBean = objectToViewBean(it)
                    binderList.add(
                            ActivityItemBinder(
                                context = context,
                                viewBean = viewBean,
                                isMine = isMine
                            )
                    )
                }
            }
            return binderList
        }

    }

}
