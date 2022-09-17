package com.kotlin.android.ugc.detail.component.binder

import android.view.View
import com.kotlin.android.app.data.entity.community.content.CommunityContent
import com.kotlin.android.ktx.ext.click.onClick

import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.ugc.detail.component.R
import com.kotlin.android.ugc.detail.component.bean.TopicFamilyViewBean
import com.kotlin.android.ugc.detail.component.databinding.ItemFamilyBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.dialog.BaseDialog

/**
 * create by lushan on 2020/8/13
 * description:帖子中家族信息卡片
 */
class FamilyBinder(var bean: TopicFamilyViewBean) : MultiTypeBinder<ItemFamilyBinding>() {
    override fun layoutId(): Int = R.layout.item_family

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FamilyBinder && other.bean.familyStatus != bean.familyStatus
    }

    override fun onBindViewHolder(binding: ItemFamilyBinding, position: Int) {
        super.onBindViewHolder(binding, position)
//        ShapeExt.setShapeCorner2Color(binding.familyLL,R.color.color_ffffff,4.dp)
        binding.familyItemView.joinFL.onClick {//加入家族
            val familyStatus = bean.familyStatus
            if (CommunityContent.GROUP_JOIN_UNDO == familyStatus){//需要加入
                super.onClick(it)
            }else{
                showExitGroupDialog(it)
            }

        }
    }

    private fun showExitGroupDialog(view: View){
        binding?.root?.context?.apply {
            BaseDialog.Builder(this).setContent(R.string.ugc_exit_family_confirm).setPositiveButton(R.string.ok) { dialog, id ->
                super.onClick(view)//需要退出家族
                dialog?.dismiss()
            }.setNegativeButton(R.string.cancel) { dialog, id ->
                dialog?.dismiss()
            }.create().show()
        }

    }

    override fun onClick(view: View) {
        if(view.id == R.id.familyRootView){//跳转到家族详情
            val provider: ICommunityFamilyProvider? = getProvider(ICommunityFamilyProvider::class.java)
            provider?.startFamilyDetail(bean.familyId)
        }else{
            super.onClick(view)
        }
    }
}