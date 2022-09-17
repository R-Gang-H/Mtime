package com.kotlin.android.community.family.component.ui.manage

import android.app.Activity
import android.content.Intent
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyPermissionBinding
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetail
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back

/**
 * @author vivian.wei
 * @date 2020/8/7
 * @desc 家族加入权限页和发布权限管理页面
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_PERMISSION)
class FamilyPermissionActivity :
    BaseVMActivity<FamilyMemberManageFragViewModel, ActFamilyPermissionBinding>() {

    private var mPermission = FamilyDetail.PERMISSION_FREE
    private var mPublishPermission = -1L
    private var mGroupId = 0L

    private var isPublish = false

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        //初始化
        intent?.apply {
            // 默认自由加入
            mGroupId = intent.getLongExtra(FamilyConstant.KEY_FAMILY_ID, 0L)
            mPermission = intent.getLongExtra(
                FamilyConstant.KEY_FAMILY_PERMISSION,
                FamilyDetail.PERMISSION_FREE
            )
            mPublishPermission = intent.getLongExtra(
                FamilyConstant.KEY_FAMILY_PUBLISH_PERMISSION,
                mPublishPermission
            )
        }

        isPublish = mPublishPermission > 0L
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        val title = TitleBarManager.with(this)
            .addItem(
                isReversed = true,
                title = getString(R.string.community_save_btn),
                colorRes = R.color.color_20a0da,
                click = {
                    save()
                }
            )
            .back { finish() }
        if (isPublish) {
            //发布管理
            title.setTitle(title = getString(R.string.family_publish_permission))
        } else {
            //加入权限
            title.setTitle(title = getString(R.string.family_join_permission))
        }
    }

    override fun initView() {
        if (isPublish) {
            mBinding?.mCtlPublish?.visible()
            mBinding?.mCtlJoinPermission?.gone()
        } else {
            mBinding?.mCtlPublish?.gone()
            mBinding?.mCtlJoinPermission?.visible()
        }
        // 事件
        initEvent()
        // 设置选中状态
        setDefaultState()
    }

    override fun initData() {
    }

    override fun startObserve() {
        mViewModel?.mPublishAuthorityUIModelState?.observe(this) {
            it?.apply {
                success?.apply {
                    if (status == 1L) {
                        val intent = Intent()
                        intent.putExtra(
                            FamilyConstant.KEY_FAMILY_PUBLISH_PERMISSION,
                            mPublishPermission
                        )
                        setResult(Activity.RESULT_OK, intent)
                        finish()
                    } else {
                        showToast(failMsg)
                    }
                }
                error?.apply {
                    showToast(this)
                }
                netError?.apply {
                    showToast(this)
                }
            }
        }
    }

    // 初始化事件
    private fun initEvent() {
        mBinding?.mActFamilyPermissonFreeTv?.onClick {
            mPermission = FamilyDetail.PERMISSION_FREE
            setSelectIcon(mBinding?.mActFamilyPermissonFreeTv)
            setUnSelectIcon(mBinding?.mActFamilyPermissonReviewTv)
        }
        mBinding?.mActFamilyPermissonReviewTv?.onClick {
            mPermission = FamilyDetail.PERMISSION_REVIEW
            setUnSelectIcon(mBinding?.mActFamilyPermissonFreeTv)
            setSelectIcon(mBinding?.mActFamilyPermissonReviewTv)
        }
        mBinding?.mActFamilyNeedJoinTv?.onClick {
            setPublishItemState(FamilyDetail.GROUP_AUTHORITY_JOIN)
        }
        mBinding?.mActFamilyNoPermissionTv?.onClick {
            setPublishItemState(FamilyDetail.GROUP_AUTHORITY_FREE)
        }
        mBinding?.mActFamilyManagerTv?.onClick {
            setPublishItemState(FamilyDetail.GROUP_AUTHORITY_MANAGER)
        }
    }

    private fun setDefaultState() {
        if (isPublish) {
            setPublishItemState(mPublishPermission)
        } else {
            setJoinItemState(mPermission)
        }
    }

    private fun setJoinItemState(state: Long) {
        when (state) {
            FamilyDetail.PERMISSION_REVIEW -> {
                mPermission = FamilyDetail.PERMISSION_REVIEW
                //审核
                setUnSelectIcon(mBinding?.mActFamilyPermissonFreeTv)
                setSelectIcon(mBinding?.mActFamilyPermissonReviewTv)
            }
            else -> { //自由加入
                mPermission = FamilyDetail.PERMISSION_FREE
                setSelectIcon(mBinding?.mActFamilyPermissonFreeTv)
                setUnSelectIcon(mBinding?.mActFamilyPermissonReviewTv)
            }
        }
    }

    /**
     * 设置发布选中状态
     */
    private fun setPublishItemState(state: Long) {
        mPublishPermission = state
        when (state) {
            FamilyDetail.GROUP_AUTHORITY_JOIN -> {
                setSelectIcon(mBinding?.mActFamilyNeedJoinTv)
                setUnSelectIcon(mBinding?.mActFamilyNoPermissionTv)
                setUnSelectIcon(mBinding?.mActFamilyManagerTv)
            }
            FamilyDetail.GROUP_AUTHORITY_FREE -> {
                setSelectIcon(mBinding?.mActFamilyNoPermissionTv)
                setUnSelectIcon(mBinding?.mActFamilyNeedJoinTv)
                setUnSelectIcon(mBinding?.mActFamilyManagerTv)
            }
            else -> {
                setSelectIcon(mBinding?.mActFamilyManagerTv)
                setUnSelectIcon(mBinding?.mActFamilyNeedJoinTv)
                setUnSelectIcon(mBinding?.mActFamilyNoPermissionTv)
            }

        }
    }

    /**
     * 设置选中状态图标
     * @param tv
     */
    private fun setSelectIcon(tv: TextView?) {
        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_family_permisson_check)
        drawable!!.setBounds(0, 0, drawable.minimumWidth, drawable.minimumHeight)
        tv?.setCompoundDrawables(null, null, drawable, null)
    }

    /**
     * 设置未选中状态
     * @param tv
     */
    private fun setUnSelectIcon(tv: TextView?) {
        tv?.setCompoundDrawables(null, null, null, null)
    }

    /**
     * 保存
     */
    private fun save() {
        if (isPublish) {
            mViewModel?.setAuthority(groupId = mGroupId, groupAuthority = mPublishPermission)
        } else {
            val intent = Intent()
            //创建的时候,传的值是0,需要重置下
            if (mPermission == 0L) {
                mPermission = FamilyDetail.PERMISSION_FREE
            }
            intent.putExtra(FamilyConstant.KEY_FAMILY_PERMISSION, mPermission)
            setResult(Activity.RESULT_OK, intent)
            finish()
        }
    }

}