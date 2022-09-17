package com.kotlin.android.mine.bean

import com.kotlin.android.app.data.ProguardRule
import com.kotlin.android.app.data.entity.mine.DataCenterBean
import com.kotlin.android.app.router.provider.mine.IMineProvider
import com.kotlin.android.mine.binder.AnalysisBinder
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.mine.bean
 * @ClassName:      AnalysisViewBean
 * @Description:    单篇分析 ViewBean
 * @Author:         haoruigang
 * @CreateDate:     2022/3/18 17:31
 * @UpdateUser:     更新者：
 * @UpdateDate:     2022/3/18 17:31
 * @UpdateRemark:   更新说明：
 * @Version:
 */
data class AnalysisViewBean(
        val hasNext: Boolean = false,//是否还有下一页
        val nextStamp: String? = null,
        val pageSize: Long = 0,
        var list: MutableList<MultiTypeBinder<*>> = mutableListOf(),//
) : ProguardRule {

    companion object {
        fun converster(
                type: Long,
                singleAnalysis: DataCenterBean.SingleAnalysisBean,
        ): AnalysisViewBean {
            return AnalysisViewBean(
                    singleAnalysis.hasNext,
                    singleAnalysis.nextStamp,
                    singleAnalysis.pageSize,
                    singleAnalysis.items?.map { converterSingleAnalysisBinder(type, it) }
                            ?.toMutableList()
                            ?: mutableListOf()
            )
        }

        /**
         * 获取单篇分析binder
         */
        private fun converterSingleAnalysisBinder(
                type: Long,
                bean: DataCenterBean.SingleAnalysisBean.Item,
        ): MultiTypeBinder<*> {
            return AnalysisBinder(bean, type) {
                // todo 跳转单片详情
                getProvider(IMineProvider::class.java)
                        ?.startActivitySingleAnalysActivity(
                                type,
                                bean)
            }
        }
    }

}