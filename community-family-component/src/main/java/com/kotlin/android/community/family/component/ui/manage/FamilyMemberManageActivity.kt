package com.kotlin.android.community.family.component.ui.manage

import android.os.Bundle
import androidx.activity.viewModels
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyMemberManageBinding
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.community.group.GroupUser
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.router.ext.put
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import com.kotlin.android.widget.tablayout.setSelectedAnim
import com.ogaclejapan.smarttablayout.utils.v4.FragmentPagerItems
import kotlinx.android.synthetic.main.act_family_member_manage.*

/**
 * @author vivian.wei
 * @date 2020/8/13
 * @desc 家族成员管理页
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_MEMBER_MANAGE)
class FamilyMemberManageActivity : BaseVMActivity<BaseViewModel, ActFamilyMemberManageBinding>() {

    private var mGroupId: Long = 0

    override fun initVM(): BaseViewModel {
        mGroupId = intent.getLongExtra(FamilyConstant.KEY_FAMILY_ID, 0)
        return viewModels<BaseViewModel>().value
    }

    override fun initView() {
        // TabLayout
        ShapeExt.setGradientColor(
            view = mActFamilyMemberManageTabLayout,
            startColor = R.color.color_f2f3f6,
            endColor = R.color.color_ffffff
        )
        val adapter = FragPagerItemAdapter(
            supportFragmentManager, FragPagerItems(baseContext)
                .add(
                    title = "成员",
                    clazz = FamilyMemberManageFragment::class.java,
                    args = Bundle().put(FamilyConstant.KEY_FAMILY_ID, mGroupId)
                        .put(FamilyConstant.KEY_FAMILY_MEMBER_TYPE, GroupUser.USER_TYPE_MEMBER)
                )
                .add(
                    title = "黑名单", 
                    clazz = FamilyMemberManageFragment::class.java,
                    args = Bundle().put(FamilyConstant.KEY_FAMILY_ID, mGroupId)
                        .put(FamilyConstant.KEY_FAMILY_MEMBER_TYPE, GroupUser.USER_TYPE_BLACK_LIST)
                )
                .add(
                    title = "申请", 
                    clazz = FamilyMemberManageFragment::class.java,
                    args = Bundle().put(FamilyConstant.KEY_FAMILY_ID, mGroupId)
                        .put(FamilyConstant.KEY_FAMILY_MEMBER_TYPE, GroupUser.USER_TYPE_APPLY)
                )
        )
        mActFamilyMemberManageViewPager.adapter = adapter
        mActFamilyMemberManageTabLayout.setViewPager(mActFamilyMemberManageViewPager)
        mActFamilyMemberManageTabLayout.setSelectedAnim()

        // 点击"返回"箭头
        mActFamilyMemberManageBackIv.onClick {
            finish()
        }

    }

    override fun initData() {

    }

    override fun startObserve() {

    }
}