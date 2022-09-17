package com.kotlin.android.community.family.component.ui.find.binder

import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import com.kotlin.android.app.data.entity.family.FindFamilyRecommend
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ItemRecommendItemFindFamilyBinding
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.mtime.ktx.getString
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author zhangjian
 * @date 2022/3/18 17:14
 */
class FindFamilyRecommendItemBinder(
    val data: FindFamilyRecommend,
    var mamagerGroup: ((type: Long, id: Long, binder: MultiTypeBinder<*>) -> Unit)?
) : MultiTypeBinder<ItemRecommendItemFindFamilyBinding>() {

    override fun layoutId(): Int {
        return R.layout.item_recommend_item_find_family
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FindFamilyRecommendItemBinder
    }

    override fun onBindViewHolder(binding: ItemRecommendItemFindFamilyBinding, position: Int) {
        super.onBindViewHolder(binding, position)
        //家族头像
        binding.ivImg.loadImage(data = data.logo, width = 47.dp, height = 47.dp)
        //家族名称
        binding.tvName.text = data.name
        //家族成员
        binding.tvInfo.text = getFamilyInfo(data.memberCount, data.postCount)
        //点击item跳转家族详情
        binding.ctlContainer.onClick {
            getProvider(ICommunityFamilyProvider::class.java)?.startFamilyDetail(data.groupId?:0L)
        }
        //按钮展示状态
        if (data.hasJoin == FamilyConstant.CONSTANT_STATE_3) {
            binding.tvState.gone()
        } else {
            binding.tvState.visible()
            binding.tvState.setStyle(data.hasJoin)
            binding.tvState.onClickChange = {
                mamagerGroup?.invoke(it, data.groupId ?: 0,this)
            }
        }
        //设置新内容
        binding.tvNewContent.text = data.newPost?.title
        //设置热内容
        binding.tvHotContent.text = data.hotPost?.title
    }

    //设置文案
    private fun getFamilyInfo(
        memberCount: Long? = 0L,
        postCount: Long? = 0L
    ): SpannableStringBuilder {
        val sb = SpannableStringBuilder()
        //族人
        val strGroup = String.format(
            getString(R.string.family_find_recommend_group_member_info),
            memberCount.toString()
        )
        val spanStringGroup = SpannableString(strGroup)
        spanStringGroup.setSpan(
            ForegroundColorSpan(getColor(R.color.color_8798af)),
            0,
            2,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spanStringGroup.setSpan(
            ForegroundColorSpan(getColor(R.color.color_20a0da)),
            2,
            strGroup.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        sb.append(spanStringGroup)
        //帖子数
        val str = String.format(
            getString(R.string.family_find_recommend_group_post_info),
            postCount.toString()
        )
        val spanString = SpannableString(str)
        spanString.setSpan(
            ForegroundColorSpan(getColor(R.color.color_8798af)),
            0,
            3,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        spanString.setSpan(
            ForegroundColorSpan(getColor(R.color.color_20a0da)),
            3,
            str.length,
            Spanned.SPAN_INCLUSIVE_EXCLUSIVE
        )
        sb.append("  ")
        sb.append(spanString)

        return sb
    }
}