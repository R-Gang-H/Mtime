package com.kotlin.android.mine.binder

import com.kotlin.android.mine.R
import com.kotlin.android.mine.bean.TodayPerformViewBean
import com.kotlin.android.mine.databinding.MineItemTodayPerformsBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.mine.binder
 * @ClassName:      TodayPerformsBinder
 * @Description:     java类作用描述
 * @Author:         haoruigang
 * @CreateDate:     2022/3/17 17:53
 * @UpdateUser:     更新者：
 * @UpdateDate:     2022/3/17 17:53
 * @UpdateRemark:   更新说明：
 * @Version:
 */
class TodayPerformsBinder(val data: TodayPerformViewBean) :
    MultiTypeBinder<MineItemTodayPerformsBinding>() {
    override fun layoutId(): Int = R.layout.mine_item_today_performs

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is TodayPerformsBinder && other.data != data
    }

    override fun onBindViewHolder(binding: MineItemTodayPerformsBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.apply {
            todayData = this@TodayPerformsBinder
        }
    }
}