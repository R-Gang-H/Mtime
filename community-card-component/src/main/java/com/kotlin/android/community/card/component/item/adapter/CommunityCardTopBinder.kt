package com.kotlin.android.community.card.component.item.adapter

import com.kotlin.android.community.card.component.R
import com.kotlin.android.community.card.component.databinding.ItemCommunityCardTopBinding
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.ktx.ext.core.Direction
import com.kotlin.android.ktx.ext.core.setBackground
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.ktx.ext.time.TimeExt
import java.util.*

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/23
 *
 * 置顶样式
 */
class CommunityCardTopBinder(item: CommunityCardItem):
        CommunityCardBaseBinder<ItemCommunityCardTopBinding>(item){
    override fun layoutId(): Int {
        return R.layout.item_community_card_top
    }

    override fun onBindViewHolder(binding: ItemCommunityCardTopBinding, position: Int) {
        binding.run {
            //日期
            val day = TimeExt.getValueByCalendarField(Calendar.DAY_OF_MONTH).toString()
            val month = TimeExt.getCurMonthEn()
            val year = TimeExt.getValueByCalendarField(Calendar.YEAR).toString()
            mCommunityCardTopDateDayTv.text = day
            mCommunityCardTopDateMonthAndYearTv.text = "$month\n$year"
            //底部数据
            mCommunityCardCommonBottom.data = this@CommunityCardTopBinder
            //内容区域的背景图
            mCommunityCardTopTagTv.setBackground(
                    colorRes = R.color.color_20a0da,
                    cornerRadius = 5.dpF,
                    direction = Direction.LEFT_TOP or Direction.RIGHT_TOP
            )
            mCommunityCardTopContentLl.setBackground(
                    colorRes = R.color.color_f2f3f6,
                    cornerRadius = 5.dpF,
                    direction = Direction.RIGHT_TOP or Direction.LEFT_BOTTOM or Direction.RIGHT_BOTTOM
            )
        }
    }
}