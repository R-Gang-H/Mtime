package com.kotlin.android.widget.refresh

import android.graphics.Color
import com.kotlin.android.widget.R
import com.scwang.smart.refresh.footer.ClassicsFooter
import com.scwang.smart.refresh.layout.SmartRefreshLayout

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/16
 */
object Refresh {

    /**
     * 初始化全局下拉刷新和上拉加载样式
     */
    @JvmStatic
    fun initHeaderAndFooter() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context, layout ->
            MRefreshHeader(context)
        }

        SmartRefreshLayout.setDefaultRefreshFooterCreator { context, layout ->
            ClassicsFooter.REFRESH_FOOTER_NOTHING =
                    context.getString(R.string.widget_refresh_footer_end)
            val classicsFooter = ClassicsFooter(context)
            //指定为经典Footer
            classicsFooter
                    .setDrawableSize(20f)
                    .setTextSizeTitle(12f)
                    .setAccentColor(Color.parseColor("#8798af"))
        }
    }
}