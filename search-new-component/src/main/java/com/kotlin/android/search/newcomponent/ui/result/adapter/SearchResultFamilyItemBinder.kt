package com.kotlin.android.search.newcomponent.ui.result.adapter

import android.content.DialogInterface
import android.graphics.drawable.GradientDrawable
import android.view.View
import androidx.core.view.isVisible
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.ktx.ext.convertToHtml
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.ktx.ext.core.setTextColorRes
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultFamilyBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant
import com.kotlin.android.search.newcomponent.ui.result.bean.FamilyItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.BaseDialog

class SearchResultFamilyItemBinder(val keyword: String, val bean : FamilyItem) :
    MultiTypeBinder<ItemSearchResultFamilyBinding>() {

    companion object {
        const val BUTTON_CORNER = 12            // dp
        const val BUTTON_STROKE_WIDTH = 2       // 描边宽 px
        const val BUTTON_DRAWABLE_PADDING = 3   // dp
    }

    val mProvider: ICommunityFamilyProvider? = getProvider(ICommunityFamilyProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_search_result_family
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultFamilyItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemSearchResultFamilyBinding, position: Int) {
        // 搜索关键词标红
        binding.mItemSearchResultFamilyNameTv.convertToHtml(bean.name, SearchResultConstant.MATCH_KEYWORD_COLOR, keyword)
        binding.mItemSearchResultFamilyMemberCountTv.apply {
            text = getString(R.string.search_result_family_member_count, formatCount(bean.memberNum))
        }
        // 加入按钮
        binding.mItemSearchResultFamilyJoinLayout.apply {
            // 联合搜索显示（黑名单用户隐藏按钮）；搜索提示不显示
            isVisible = bean.showIsJoin && bean.isJoin != FamilyItem.JOIN_STATUS_BLACK_LIST
            if (isVisible) {
                when(bean.isJoin) {
                    FamilyItem.JOIN_STATUS_NOT -> {
                        // 未加入
                        ShapeExt.setGradientColor(
                            this,
                            GradientDrawable.Orientation.TOP_BOTTOM,
                            R.color.color_20a0da,
                            R.color.color_1bafe0,
                            BUTTON_CORNER)
                        binding.mItemSearchResultFamilyJoinTv.apply {
                            setText(R.string.search_result_family_join)
                            setTextColor(getColor(R.color.color_ffffff))
                            setCompoundDrawablesAndPadding()
                        }
                    }
                    FamilyItem.JOIN_STATUS_JOINING -> {
                        // 加入中
                        ShapeExt.setShapeCorner2Color2Stroke(
                            this,
                            R.color.color_00ffffff,
                            BUTTON_CORNER,
                            R.color.color_20a0da,
                            BUTTON_STROKE_WIDTH
                        )
                        binding.mItemSearchResultFamilyJoinTv.apply {
                            setText(R.string.search_result_family_joining)
                            setTextColorRes(R.color.color_20a0da)
                            setCompoundDrawablesAndPadding()
                        }
                    }
                    FamilyItem.JOIN_STATUS_JOINED -> {
                        // 已加入
                        ShapeExt.setShapeCorner2Color2Stroke(
                            this,
                            R.color.color_00ffffff,
                            BUTTON_CORNER,
                            R.color.color_20a0da,
                            BUTTON_STROKE_WIDTH
                        )
                        binding.mItemSearchResultFamilyJoinTv.apply {
                            setText(R.string.search_result_family_join)
                            setTextColorRes(R.color.color_20a0da)
                            setCompoundDrawablesAndPadding(
                                leftResId = R.drawable.ic_checkb,
                                padding = BUTTON_DRAWABLE_PADDING
                            )
                        }
                    }
                    else -> {
                    }
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mItemSearchResultFamilyJoinLayout -> {
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
        when (bean.isJoin) {
            //未加入
            FamilyItem.JOIN_STATUS_NOT -> {
                super.onClick(view)
            }
            //加入中
            FamilyItem.JOIN_STATUS_JOINING -> {
                showToast(R.string.search_result_family_joining_hint)
            }
            //已加入
            FamilyItem.JOIN_STATUS_JOINED -> {
                exitFamily(view)
            }
            else -> {
            }
        }
    }

    //加入成功的回调处理
    fun joinChanged(result: CommonResult) {
        when (result.status) {
            CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED,
            CommConstant.JOIN_FAMILY_RESULT_STATUS_JOINED -> {
                // 加入成功
                bean.memberNum++
                bean.isJoin = FamilyItem.JOIN_STATUS_JOINED
                notifyAdapterSelfChanged()
            }
            CommConstant.JOIN_FAMILY_RESULT_STATUS_JOINING -> {
                // 加入中
                notifyAdapterSelfChanged()
            }
            CommConstant.JOIN_FAMILY_RESULT_STATUS_BLACKLIST -> {
                // 黑名单
                bean.isJoin = FamilyItem.JOIN_STATUS_BLACK_LIST
                notifyAdapterSelfChanged()
            }
            else -> {
                showToast(result.failMsg)
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
            .setContent(R.string.search_result_family_exit_hint)
            .setNegativeButton(R.string.widget_cancel, listener)
            .setPositiveButton(R.string.widget_sure, listener)
            .create()
            .show()
    }

    //退出成功的回调处理
    fun outChanged() {
        bean.memberNum--
        bean.isJoin = FamilyItem.JOIN_STATUS_NOT
        notifyAdapterSelfChanged()
    }

}