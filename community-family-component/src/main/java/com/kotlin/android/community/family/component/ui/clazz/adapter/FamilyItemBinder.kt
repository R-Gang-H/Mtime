package com.kotlin.android.community.family.component.ui.clazz.adapter

import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.view.View
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ItemCommunityFamilyBinding
import com.kotlin.android.community.family.component.ui.clazz.bean.FamilyItem
import com.kotlin.android.community.family.component.ui.details.FamilyDetailActivity
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.setTextWithFormat
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.BaseDialog

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/27
 */
class FamilyItemBinder(val item: FamilyItem, private val goneLinePosition: Int = 0) : MultiTypeBinder<ItemCommunityFamilyBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_community_family
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FamilyItemBinder && other.item.joinType != item.joinType
    }

    override fun onBindViewHolder(binding: ItemCommunityFamilyBinding, position: Int) {
        //成员
        binding.mCommunityFamilyNumberTv.setTextWithFormat(
                R.string.community_family_number, formatCount(item.numberCount))

        //顶部分割线
        if (position == goneLinePosition) {
            binding.mCommunityFamilyTopLineView.gone()
        } else {
            binding.mCommunityFamilyTopLineView.visible()
        }
        //按钮
        binding.mCommunityFamilyBtnFl.visible(
                item.joinType != FamilyItem.JOIN_TYPE_BLACKLIST) //黑名单用户隐藏按钮
        when (item.joinType) {
            //已加入
            FamilyItem.JOIN_TYPE_JOINED -> {
                ShapeExt.setShapeCorner2Color2Stroke(
                        binding.mCommunityFamilyBtnFl,
                        R.color.color_00ffffff,
                        30,
                        R.color.color_20a0da,
                        1
                )
                binding.mCommunityFamilyBtnTv.setTextColorRes(R.color.color_20a0da)
                binding.mCommunityFamilyBtnTv.setCompoundDrawablesAndPadding(
                        leftResId = R.drawable.ic_checkb,
                        padding = 3
                )
                binding.mCommunityFamilyBtnTv.setText(R.string.community_join_btn)
            }
            //加入中
            FamilyItem.JOIN_TYPE_JOINING -> {
                ShapeExt.setShapeCorner2Color2Stroke(
                        binding.mCommunityFamilyBtnFl,
                        R.color.color_00ffffff,
                        30,
                        R.color.color_20a0da,
                        1
                )
                binding.mCommunityFamilyBtnTv.setTextColorRes(R.color.color_20a0da)
                binding.mCommunityFamilyBtnTv.setCompoundDrawablesAndPadding()
                binding.mCommunityFamilyBtnTv.setText(R.string.community_joining_btn)
            }
            //未加入
            FamilyItem.JOIN_TYPE_NO_JOIN -> {
                ShapeExt.setGradientColor(
                        binding.mCommunityFamilyBtnFl,
                        GradientDrawable.Orientation.TOP_BOTTOM,
                        R.color.color_20a0da,
                        R.color.color_1bafe0,
                        30)
                binding.mCommunityFamilyBtnTv.setTextColorRes(R.color.color_ffffff)
                binding.mCommunityFamilyBtnTv.setCompoundDrawablesAndPadding()
                binding.mCommunityFamilyBtnTv.setText(R.string.community_join_btn)
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mCommunityFamilyItemRoot -> {
                FamilyDetailActivity.start(view.context, item.id)
            }
            R.id.mCommunityFamilyBtnFl -> {
                joinBtnClick(view)
            }
            else -> {
                super.onClick(view)
            }
        }
    }

    //按钮点击事件
    private fun joinBtnClick(view: View) {
        //按钮
        when (item.joinType) {
            //已加入
            FamilyItem.JOIN_TYPE_JOINED -> {
                exitFamily(view)
            }
            //加入中
            FamilyItem.JOIN_TYPE_JOINING -> {
                showToast(R.string.community_joining_family_hint)
            }
            //未加入
            FamilyItem.JOIN_TYPE_NO_JOIN -> {
                super.onClick(view)
            }
        }
    }

    //退出家族相关逻辑处理
    private fun exitFamily(view: View) {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            if (which == -1) {
                //确定退出家族
                super.onClick(view)
            }
        }
        BaseDialog.Builder(view.context)
                .setContent(R.string.community_exit_family_hint)
                .setNegativeButton(R.string.widget_cancel, listener)
                .setPositiveButton(R.string.widget_sure, listener)
                .create()
                .show()
    }

    //加入成功的回调处理
    fun joinChanged(result: CommonResult) {
        when (result.status) {
            CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED,
            CommConstant.JOIN_FAMILY_RESULT_STATUS_JOINED -> {
                item.numberCount++
                item.joinType = FamilyItem.JOIN_TYPE_JOINED
                notifyAdapterSelfChanged()
            }
            CommConstant.JOIN_FAMILY_RESULT_STATUS_JOINING -> {
                item.numberCount++
                item.joinType = FamilyItem.JOIN_TYPE_JOINING
                notifyAdapterSelfChanged()
            }
            CommConstant.JOIN_FAMILY_RESULT_STATUS_BLACKLIST -> {
                item.joinType = FamilyItem.JOIN_TYPE_BLACKLIST
                notifyAdapterSelfChanged()
            }
            else -> {
                showToast(result.failMsg)
            }
        }
    }

    //退出成功的回调处理
    fun outChanged() {
        item.numberCount--
        item.joinType = FamilyItem.JOIN_TYPE_NO_JOIN
        notifyAdapterSelfChanged()
    }
}