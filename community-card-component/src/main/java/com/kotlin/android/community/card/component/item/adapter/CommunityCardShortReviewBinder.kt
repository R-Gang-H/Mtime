package com.kotlin.android.community.card.component.item.adapter

import android.text.Spannable
import android.text.SpannableString
import com.kotlin.android.community.card.component.R
import com.kotlin.android.community.card.component.databinding.ItemCommunityCardShortReviewBinding
import com.kotlin.android.community.card.component.item.bean.CommunityCardItem
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.widget.views.CommunityQuoteSpan

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/23
 */
class CommunityCardShortReviewBinder(item: CommunityCardItem):
        CommunityCardBaseBinder<ItemCommunityCardShortReviewBinding>(item) {
    override fun layoutId(): Int {
        return R.layout.item_community_card_short_review
    }

    override fun onBindViewHolder(binding: ItemCommunityCardShortReviewBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.mCommunityCardCommonBottom.data = this

        binding.mCommunityCardContentTv.run {
            val ssb = SpannableString(item.content /*+ " 1"*/)
            ssb.setSpan(
                    CommunityQuoteSpan(
                            context,
                            R.drawable.ic_community_quote_left,
                            3.dp
                    )
                    , 0, 0, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            ssb.setSpan(
//                    ImageSpan(
//                            context,
//                            R.drawable.ic_community_quote_right,
//                            if (Build.VERSION.SDK_INT >= 29)
//                                DynamicDrawableSpan.ALIGN_CENTER
//                            else
//                                DynamicDrawableSpan.ALIGN_BASELINE
//                    )
//                    , ssb.length - 1, ssb.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            text = ssb
        }
    }
}