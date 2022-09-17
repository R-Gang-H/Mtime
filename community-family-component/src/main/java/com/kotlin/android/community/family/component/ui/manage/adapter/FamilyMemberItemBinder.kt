package com.kotlin.android.community.family.component.ui.manage.adapter

import androidx.core.view.isVisible
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ItemFamilyMemberBinding
import com.kotlin.android.app.data.entity.community.group.GroupUser
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author vivian.wei
 * @date 2020/8/14
 * @desc 家族成员
 */
class FamilyMemberItemBinder(val bean: GroupUser, val showFollow: Boolean, val isManage: Boolean,
                             onClick: (bean: GroupUser, position: Int) -> Unit,
                             onClickFollow: (bean: GroupUser, position: Int) -> Unit) : MultiTypeBinder<ItemFamilyMemberBinding>() {

    private var mIsManage: Boolean = isManage
    private var mOnClick = onClick
    private var mOnClickFollow = onClickFollow

    override fun layoutId(): Int {
        return R.layout.item_family_member
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FamilyMemberItemBinder && other.bean.userId == bean.userId
    }

    override fun onBindViewHolder(binding: ItemFamilyMemberBinding, position: Int) {
        // checkbox
        binding.mItemFamilyMemberCb.isVisible = mIsManage
        binding.mItemFamilyMemberCb.isChecked = bean.checked
        // 群主标识
        var isCreator = bean.userType == GroupUser.USER_TYPE_CREATOR
        binding.mItemFamilyMemberCreatorIconTv.isVisible = isCreator
        if(isCreator) {
            ShapeExt.setShapeColorAndCorner(binding.mItemFamilyMemberCreatorIconTv, R.color.color_20a0da, 7)
        }
        // 关注
        var showFollowBtn = showFollow && !(bean.followed?:false) && bean.userId != com.kotlin.android.user.UserManager.instance.userId
        binding.mItemFamilyMemberFollowTv.isVisible = showFollowBtn
        if (showFollowBtn) {
            ShapeExt.setShapeColorAndCorner(binding.mItemFamilyMemberFollowTv, R.color.color_20a0da, 12)
            // 点击关注按钮
            binding.mItemFamilyMemberFollowTv.onClick {
                mOnClickFollow.invoke(bean, position)
            }
        }

        binding.root.onClick {
            if(mIsManage) {
                bean.checked = !bean.checked
                notifyAdapterSelfChanged()
                mOnClick.invoke(bean, position)
            }
        }

    }

}