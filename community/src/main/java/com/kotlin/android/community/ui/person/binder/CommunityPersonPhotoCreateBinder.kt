package com.kotlin.android.community.ui.person.binder

import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ItemCommunityPersonPhotoCreateBinding
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by Wangwei on 2020/9/28
 * description:社区个人主页中添加照片卡片
 */
class CommunityPersonPhotoCreateBinder : MultiTypeBinder<ItemCommunityPersonPhotoCreateBinding>() {
    override fun layoutId(): Int = R.layout.item_community_person_photo_create

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityPersonPhotoCreateBinder
    }

    override fun onBindViewHolder(binding: ItemCommunityPersonPhotoCreateBinding, position: Int) {
        ShapeExt.setShapeCorner2Color2Stroke(binding.photoCreateRootView,corner = 2,strokeColor = R.color.color_ebedf2,strokeWidth = 1)
        super.onBindViewHolder(binding, position)
    }
}