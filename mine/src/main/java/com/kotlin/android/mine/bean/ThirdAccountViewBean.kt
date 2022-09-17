package com.kotlin.android.mine.bean

import android.content.Context
import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.mine.UserBindViewBean
import com.kotlin.android.mine.R
import com.kotlin.android.mine.binder.ThirdAccountBinder
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

data class ThirdAccountViewBean(
    var success: Boolean,
    var error: String,
    var list: MutableList<MultiTypeBinder<*>> = mutableListOf()
) : ProguardRule {
    companion object {
        private const val PLATFORM_ID_WX = 4
        private const val PLATFORM_ID_QQ = 2
        private const val PLATFORM_ID_SINA = 1
        private const val PLATFORM_ID_APPLE = 6

        fun obtain(
            userBindViewBean: UserBindViewBean,
            context: Context
        ): ThirdAccountViewBean {
            val data: MutableList<MultiTypeBinder<*>> = mutableListOf()
            data.add(
                ThirdAccountBinder(
                    ThirdAccountDataBean(
                        context.getString(R.string.mine_wx_account),
                        if (!userBindViewBean.oauthWechat) context.getString(R.string.mine_bind) else context.getString(
                            R.string.mine_third_unbind
                        ),
                        if (userBindViewBean.oauthWechat) 0 else 1, PLATFORM_ID_WX
                    )
                )
            )
            data.add(
                ThirdAccountBinder(
                    ThirdAccountDataBean(
                        context.getString(R.string.mine_qq_account),
                        if (!userBindViewBean.oauthQQ) context.getString(R.string.mine_bind) else context.getString(
                            R.string.mine_third_unbind
                        ),
                        if (userBindViewBean.oauthQQ) 0 else 1, PLATFORM_ID_QQ
                    )
                )
            )
            data.add(
                ThirdAccountBinder(
                    ThirdAccountDataBean(
                        context.getString(R.string.mine_sina_account),
                        if (!userBindViewBean.oauthSina) context.getString(R.string.mine_bind) else context.getString(
                            R.string.mine_third_unbind
                        ),
                        if (userBindViewBean.oauthSina) 0 else 1, PLATFORM_ID_SINA
                    )
                )
            )
//            data.add(
//                ThirdAccountBinder(
//                    ThirdAccountDataBean(
//                        context.getString(R.string.mine_apple_account),
//                        if (!userBindViewBean.oauthApple) context.getString(R.string.mine_bind) else context.getString(
//                            R.string.mine_third_unbind
//                        ),
//                        if (userBindViewBean.oauthApple) 0 else 1, PLATFORM_ID_APPLE
//                    )
//                )
//            )
            return ThirdAccountViewBean(userBindViewBean.success, userBindViewBean.error, data)
        }
    }
}