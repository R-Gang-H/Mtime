package com.kotlin.android.search.newcomponent.ui.result.adapter

import android.content.DialogInterface
import android.view.View
import androidx.core.view.isVisible
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.ktx.ext.convertToHtml
import com.kotlin.android.ktx.ext.core.getColor
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.ktx.ext.core.setCompoundDrawablesAndPadding
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultUserBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant
import com.kotlin.android.search.newcomponent.ui.result.bean.UserItem
import com.kotlin.android.user.UserManager
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.BaseDialog

class SearchResultUserItemBinder(val keyword: String, val bean : UserItem) :
    MultiTypeBinder<ItemSearchResultUserBinding>() {

    companion object {
        const val BUTTON_CORNER = 12            // dp
        const val BUTTON_STROKE_WIDTH = 2       // 描边宽 px
        const val BUTTON_DRAWABLE_PADDING = 3   // dp
    }

    val mCommunityPersonProvider: ICommunityPersonProvider? = getProvider(ICommunityPersonProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_search_result_user
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultUserItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemSearchResultUserBinding, position: Int) {
        // 搜索关键词标红
        binding.mItemSearchResultUserNicknameTv.convertToHtml(bean.nickName, SearchResultConstant.MATCH_KEYWORD_COLOR, keyword)
        // 认证类型Icon
        binding.mItemSearchResultUserAuthTypeIv.apply {
            var showAuthType: Boolean
            bean.authType.let { mvpType ->
                // 仅显示：2影评人，3电影人，4机构，
                showAuthType = mvpType > CommConstant.AUTH_TYPE_PERSONAL
                        && mvpType <= CommConstant.AUTH_TYPE_ORGAN
            }
            isVisible = showAuthType
            if(showAuthType) {
                // 影评人和电影人是蓝色，机构认证是黄色
                setImageResource(
                    if (bean.authType == CommConstant.AUTH_TYPE_ORGAN)
                        com.kotlin.android.review.component.R.drawable.ic_jigourenzheng
                    else
                        com.kotlin.android.review.component.R.drawable.ic_yingrenrenzheng
                )
            }
        }
        // 粉丝数
        binding.mItemSearchResultUserFansCountTv.apply {
            text = getString(R.string.search_result_user_fans_count, formatCount(bean.fansCount))
        }
        // 关注按钮
        binding.mItemSearchResultUserFollowLayout.apply {
            // 联合搜索显示，搜索提示不显示
            isVisible = bean.showUserFocus && UserManager.instance.userId != bean.userId
            if (isVisible) {
                if (bean.isFocus == UserItem.FOLLOW) {
                    // 已关注
                    ShapeExt.setShapeCorner2Color2Stroke(
                        view = this,
                        corner = BUTTON_CORNER,
                        strokeColor = R.color.color_20a0da,
                        strokeWidth = BUTTON_STROKE_WIDTH
                    )
                    binding.mItemSearchResultUserFollowTv.setTextColor(getColor(R.color.color_20a0da))
                    binding.mItemSearchResultUserFollowTv.setCompoundDrawablesAndPadding(
                        leftResId = R.drawable.ic_checkb,
                        padding = BUTTON_DRAWABLE_PADDING
                    )
                } else {
                    // 未关注
                    ShapeExt.setShapeColorAndCorner(this, R.color.color_20a0da, BUTTON_CORNER)
                    binding.mItemSearchResultUserFollowTv.setTextColor(getColor(R.color.color_ffffff))
                    binding.mItemSearchResultUserFollowTv.setCompoundDrawablesAndPadding()
                }
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.mItemSearchResultUserFollowLayout -> {
                if (bean.isFocus == UserItem.FOLLOW) {
                    // 已关注：显示取消确认框
                    showCancelDialog(view)
                } else {
                    // 未关注
                    super.onClick(view)
                }
            }
            else -> {
                super.onClick(view)
            }
        }
    }

    /**
     * 显示取消确认框
     */
    private fun showCancelDialog(view: View) {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            if (which == -1) {
                //确定
                super.onClick(view)
            }
        }
        BaseDialog.Builder(view.context)
            .setContent(R.string.search_result_user_cancel_follow_hint)
            .setNegativeButton(R.string.widget_cancel, listener)
            .setPositiveButton(R.string.widget_sure, listener)
            .create()
            .show()
    }

    /**
     * 关注、取消关注改变
     */
    fun followChanged() {
        bean.isFocus = if(bean.isFocus == UserItem.FOLLOW) UserItem.UN_FOLLOW else UserItem.FOLLOW
        if(bean.isFocus == UserItem.FOLLOW) {
            bean.fansCount++
        } else {
            bean.fansCount--
        }
        notifyAdapterSelfChanged()
    }

}