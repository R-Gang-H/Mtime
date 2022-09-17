package com.kotlin.android.community.ui.person.binder

import android.view.View
import com.kotlin.android.community.R
import com.kotlin.android.community.databinding.ItemCommunityPersonPhotoBinding
import com.kotlin.android.community.ui.person.bean.CommunityPhotoViewBean

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * create by Wangwei on 2020/9/27
 * description:社区个人主页中照片卡片
 */
class CommunityPersonPhotoBinder(var viewBean: CommunityPhotoViewBean) : MultiTypeBinder<ItemCommunityPersonPhotoBinding>() {
    override fun layoutId(): Int = R.layout.item_community_person_photo

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is CommunityPersonPhotoBinder && other.viewBean.id != viewBean.id
    }

    override fun onClick(view: View) {
        if (view.id == R.id.photoRootView) {//跳转到图片详情
            val provider: IUgcProvider? = getProvider(IUgcProvider::class.java)
            provider?.startAlbumDetail(viewBean.id)
        } else {
            super.onClick(view)
        }
    }
}