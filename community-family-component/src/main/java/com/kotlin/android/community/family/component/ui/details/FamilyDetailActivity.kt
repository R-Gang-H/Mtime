package com.kotlin.android.community.family.component.ui.details

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.alibaba.android.arouter.facade.annotation.Route
import com.google.android.material.appbar.AppBarLayout
import com.kotlin.android.app.api.viewmodel.CommViewModel
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_POST
import com.kotlin.android.app.data.constant.CommConstant
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.common.CommonShare
import com.kotlin.android.app.data.entity.community.group.GroupSection
import com.kotlin.android.app.data.entity.community.group.GroupSectionList
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.community_family.ICommunityFamilyProvider
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.app.router.provider.search.ISearchProvider
import com.kotlin.android.bonus.scene.component.postJoinFamily
import com.kotlin.android.community.family.component.BR
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyDetailBinding
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetail
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetail.Companion.GROUP_AUTHORITY_FREE
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetail.Companion.GROUP_AUTHORITY_JOIN
import com.kotlin.android.community.family.component.ui.details.bean.FamilyDetail.Companion.GROUP_AUTHORITY_MANAGER
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.community.family.component.ui.manage.widget.EditDialog
import com.kotlin.android.community.post.component.item.adapter.CommunityPostBinder
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.core.BaseVMFragment
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.ktx.ext.core.copyToClipboard
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.startActivity
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.mtime.ktx.saveCacheFamilyPostCount
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.router.ext.put
import com.kotlin.android.share.SharePlatform
import com.kotlin.android.share.entity.ShareEntity
import com.kotlin.android.share.ext.dismissShareDialog
import com.kotlin.android.share.ext.showShareDialog
import com.kotlin.android.share.ui.ShareFragment
import com.kotlin.android.user.afterLogin
import com.kotlin.android.widget.dialog.BaseDialog
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.tablayout.FragPagerItemAdapter
import com.kotlin.android.widget.tablayout.FragPagerItems
import kotlin.math.abs

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/29
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_DETAIL)
class FamilyDetailActivity : BaseVMActivity<FamilyDetailViewModel, ActFamilyDetailBinding>(),
    MultiStateView.MultiStateListener {
    companion object {
        const val KEY_ID = "family_id"
        fun start(context: Context, id: Long) {
            context.startActivity(FamilyDetailActivity::class.java, Bundle().put(KEY_ID, id))
        }
    }

    private var mCommViewModel: CommViewModel<CommunityPostBinder>? = null
    private val mFamilyProvider = getProvider(ICommunityFamilyProvider::class.java)
    private var mCurSort: Long = 9L //1.最新 2.最热 3.精华 9.最新回复
    private var mFamilyDetail: FamilyDetail? = null
    private var isManager = false
    private var mViewPagerAdapter: FragPagerItemAdapter? = null
    private var dialog: EditDialog? = null
    //刷新标记位,从详情页面回来不刷新
    private var isNeedRefresh = true


    override fun initTheme() {
        super.initTheme()
        immersive().transparentStatusBar().statusBarDarkFont(true)
    }

    override fun initVM(): FamilyDetailViewModel {
        mCommViewModel = viewModels<CommViewModel<CommunityPostBinder>>().value
        return viewModels<FamilyDetailViewModel>().value
    }

    override fun initView() {
        mBinding?.mFamilyDetailAppBarLayout?.addOnOffsetChangedListener(object :
            AppBarLayout.OnOffsetChangedListener {
            val statusBarColor = 0x00ffffff
            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                val height = (appBarLayout.totalScrollRange * 0.8f).toInt()
                var pos = abs(verticalOffset)
                //滑动过程改变title背景颜色
                if (pos < 0) {
                    pos = 0
                } else if (pos > height) {
                    pos = height
                }
                val ratio = pos / height.toFloat()
                val alpha = (255 * ratio).toInt()
                val color = statusBarColor and 0x00ffffff or (alpha shl 24)
                mBinding?.mFamilyDetailTitleLayoutCl?.setBackgroundColor(color)
            }
        })
        //点击筛选分类
        mBinding?.filterView?.onClickType = {
            mCurSort = it
            notifyFragRefresh(mBinding?.mViewPager?.currentItem ?: 0)
        }
        //viewPager切换监听
        mBinding?.mViewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

            }

            override fun onPageSelected(position: Int) {
                notifyFragRefresh(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })


        mBinding?.mFamilyDetailTitleMoreIv?.onClick {
            mCommViewModel?.getShareInfo(
                type = CommConstant.SHARE_TYPE_FAMILY,
                relateId = mViewModel?.mDetailId ?: 0L
            )
        }
        mBinding?.mFamilyDetailTitleBackIv?.onClick { finish() }
        mBinding?.mFamilyDetailTitleBackIv2?.onClick { finish() }
        mBinding?.mFamilyDetailTitleJoinTv?.onClickChange = { titleBtnClick() }
        mBinding?.mFamilyDetailTitleSearchIv?.onClick { jumpSearchPage() }
        mBinding?.mFamilyDetailPublishBtnIv?.onClick { publishBtnClick() }

        //管理员添加分区按钮
        mBinding?.ivAddSection?.onClick {
            showAddSectionDialog()
        }

        mBinding?.mFamilyDetailHeadLayout?.mFamilyDetailHeadFamilyUserNumberAllTv?.onClick {
            mFamilyProvider?.startFamilyMember(groupId = mViewModel?.mDetailId ?: 0L)
        }

        mBinding?.mMultiStateView?.setMultiStateListener(this)
    }

    /**
     * 添加分区的弹框
     */
    private fun showAddSectionDialog() {
        dialog = EditDialog(this, R.style.common_dialog, "")
        dialog?.apply {
            addClick = {
                mViewModel?.addFamilySection(mViewModel?.mDetailId.orZero(), it)
            }
            cancelClick = {
                this.dismiss()
            }
            create()
            show()
        }
    }

    override fun initData() {
        mViewModel?.mDetailId = intent.extras?.getLong(KEY_ID, 0) ?: 0L
    }

    override fun onResume() {
        super.onResume()
        loadFamilyData(isNeedRefresh)
        if(isNeedRefresh){
            isNeedRefresh = false
        }
    }


    override fun startObserve() {
        //注册详情回调监听
        registerDetailObserve()
        //获取分区回调
        registerSectionObserve()
        //添加分区回调
        registerAddSectionOberve()
        //注册加入家族回调监听
        registerJoinFamilyObserve()
        //注册退出家族回调监听
        registerOutFamilyObserve()
        //注册获取分享接口监听
        registerShareObserve()
    }

    /**
     * 注册获取分享接口监听
     */
    private fun registerShareObserve() {
        mCommViewModel?.mCommShareUIState?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    showShare(this)
                }

                error?.apply {
                    showToast(this)
                }

                netError?.apply {
                    showToast(netError)
                }
            }
        }
    }

    private fun showShare(share: CommonShare) {
        showShareDialog(
            ShareEntity.build(share),
            ShareFragment.LaunchMode.ADVANCED,
            SharePlatform.COPY_LINK
        ) {
            when (it) {
                SharePlatform.COPY_LINK -> {//复制粘贴
                    copyToClipboard(share.url.orEmpty())
                    showToast(R.string.share_copy_link_success)
                    dismissShareDialog()
                }
            }
        }
    }

    /**
     * 添加分区的回调
     */
    private fun registerAddSectionOberve() {
        mViewModel?.mAddSectionUIModelState?.observe(this) {
            it?.apply {
                success?.apply {
                    if (status == 1L) {
                        showToast("添加成功")
                        mViewModel?.loadFamilyDetail()
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
                dialog?.dismiss()
            }
        }
    }

    /**
     * 获取分区的数据
     */
    private fun registerSectionObserve() {
        mViewModel?.mFamilySectionUIModelState?.observe(this) {
            it.apply {
                mFamilyDetail?.apply {
                    showDetailNext(this)
                }
                success?.apply {
                    showTabLayout(isManager)
                    setViewPagerData(this, mViewModel?.mDetailId ?: 0L)
                }
                error?.apply {
                    showTabLayout(isManager)
                    setViewPagerData(null, mViewModel?.mDetailId ?: 0L)
                }
                netError?.apply {
                    showTabLayout(false)
                    setViewPagerData(null, mViewModel?.mDetailId ?: 0L)
                }
            }
        }
    }

    //注册详情回调监听
    private fun registerDetailObserve() {
        mViewModel?.uiDetailState?.observe(this, Observer {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }
                mBinding?.mFamilyDetailTitleBackIv2?.gone()
                success?.apply {
                    mFamilyDetail = this
                    if (bizCode != 1L) { //1 正常 2 群组不存在 3 群组已删除
                        if (bizCode == 2L) {
                            mBinding?.mMultiStateView?.setEmptyResource(resid = R.string.community_detail_family_no)
                        } else {
                            mBinding?.mMultiStateView?.setEmptyResource(resid = R.string.community_detail_family_deleted)
                        }
                        mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                        mBinding?.mFamilyDetailTitleBackIv2?.visible()
                    } else {
                        //获取分区
                        isManager = this.userType == FamilyDetail.USER_TYPE_MASTER
                        loadSectionList()
                    }
                }

                error?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                    mBinding?.mFamilyDetailTitleBackIv2?.visible()
                }

                netError?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                    mBinding?.mFamilyDetailTitleBackIv2?.visible()
                }
            }
        })
    }

    /**
     * 分区展示UI
     */
    private fun showDetailNext(detail: FamilyDetail) {
        showDetail(detail)
    }

    /**
     * 通知分区的fragment请求数据
     */
    private fun notifyFragRefresh(position:Int){
        val mCurrentFragment = mViewPagerAdapter?.getPage(position) as BaseVMFragment<*, *>
        mCurrentFragment.notifyRefresh(mCurSort)
    }

    /**
     * 展示viewpager的数据
     */
    private fun setViewPagerData(familySectionList: GroupSectionList? = null, groupId: Long) {
        val sectionIsNotEmpty =
            familySectionList != null && familySectionList.sectionList?.isNotEmpty() == true
        val pagerItems = FragPagerItems(this)
        val data = GroupSectionList()
        data.sectionList = arrayListOf(GroupSection(0L, "全部"))
        if (sectionIsNotEmpty) {
            familySectionList?.sectionList?.let { data.sectionList?.addAll(it) }
        }
        data.sectionList?.forEach {
            val bundle = Bundle()
            bundle.putBoolean(FamilyDetailFragment.KEY_ESSENCE, mCurSort == 3L)
            bundle.putLong(FamilyDetailFragment.KEY_SORT, mCurSort)
            bundle.putLong(FamilyDetailFragment.KEY_SECTION_ID, it.sectionId ?: 0L)
            bundle.putLong(FamilyDetailFragment.KEY_GROUP_ID, groupId)
            pagerItems.add(
                title = it.name.orEmpty(),
                clazz = FamilyDetailFragment::class.java,
                args = bundle
            )
        }
        mViewPagerAdapter = FragPagerItemAdapter(supportFragmentManager, pagerItems)
        mBinding?.mViewPager?.adapter = mViewPagerAdapter
        mBinding?.tabLayout?.setViewPager(mBinding?.mViewPager)
        //默认情况下刷新第0个
        notifyFragRefresh(0)
    }

    //显示详情的基本UI数据
    private fun showDetail(detail: FamilyDetail) {
        detail.apply {
            //缓存本地帖子数
            saveCacheFamilyPostCount(id, postNumber)
            //绑定详情数据
            (mBinding as ActFamilyDetailBinding).mFamilyDetailHeadLayout.setVariable(
                BR.detail,
                this
            )
            //黑名单用户隐藏title按钮
            mBinding?.mFamilyDetailTitleJoinFl?.visible(userType != FamilyDetail.USER_TYPE_BLACKLIST)
            setTitleBtnStyle(this)
        }
    }

    //注册加入家族回调监听
    private fun registerJoinFamilyObserve() {
        mViewModel?.mCommJoinFamilyUISate?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    joinChanged(result, extend)
                    postJoinFamily()
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

    //加入成功的回调处理
    private fun joinChanged(result: CommonResult, extend: FamilyDetail) {
        when (result.status) {
            CommConstant.JOIN_FAMILY_RESULT_STATUS_SUCCEED,
            CommConstant.JOIN_FAMILY_RESULT_STATUS_JOINED -> {
                extend.memberNumber++
                extend.userType = FamilyDetail.USER_TYPE_MEMBER
                showDetail(extend)
            }
            CommConstant.JOIN_FAMILY_RESULT_STATUS_JOINING -> {
                extend.memberNumber++
                extend.userType = FamilyDetail.USER_TYPE_JOINING
                showDetail(extend)
            }
            CommConstant.JOIN_FAMILY_RESULT_STATUS_BLACKLIST -> {
                extend.userType = FamilyDetail.USER_TYPE_BLACKLIST
                showDetail(extend)
            }
            else -> {
                showToast(result.failMsg)
            }
        }
    }

    //注册退出家族回调监听
    private fun registerOutFamilyObserve() {
        mViewModel?.mCommOutFamilyUISate?.observe(this) {
            it.apply {
                if (showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    if (result.status == 1L) {
                        extend.userType = FamilyDetail.USER_TYPE_NO_JOIN
                        extend.memberNumber--
                        showDetail(extend)
                    } else {
                        showToast(result.failMsg)
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

    private fun showTabLayout(isManager: Boolean) {
        mBinding?.llContainerTab?.visible()
        if (isManager) {
            mBinding?.ivAddSection?.visible()
        } else {
            mBinding?.ivAddSection?.gone()
        }
    }

    //发布按钮点击操作
    private fun publishBtnClick() {
        afterLogin {
            mViewModel?.uiDetailState?.value?.success?.run {
                when (groupAuthority) {
                    //加入发帖
                    GROUP_AUTHORITY_JOIN -> {
                        when (userType) {
                            FamilyDetail.USER_TYPE_JOINING -> {
                                showToast(R.string.community_joining_family_hint)
                            }
                            FamilyDetail.USER_TYPE_NO_JOIN -> {
                                joinFamilyDialog()
                            }
                            FamilyDetail.USER_TYPE_BLACKLIST -> {
                                showToast(R.string.community_blacklist_family_hint)
                            }
                            else -> {
                                // 跳转到发布页面
                                isNeedRefresh = true
                                getProvider(IPublishProvider::class.java) {
                                    startEditorActivity(
                                        type = CONTENT_TYPE_POST,
                                        familyId = id,
                                        familyName = name
                                    )
                                }
                            }
                        }
                    }
                    //自由发帖
                    GROUP_AUTHORITY_FREE -> {
                        when (userType) {
                            FamilyDetail.USER_TYPE_BLACKLIST -> {
                                showToast(R.string.community_blacklist_family_hint)
                            }
                            else -> {
                                // 自由发帖不需要判断是否加入家族，即可发帖
                                // 跳转到发布页面
                                isNeedRefresh = true
                                getProvider(IPublishProvider::class.java)
                                    ?.startEditorActivity(
                                        type = CONTENT_TYPE_POST,
                                        familyId = id,
                                        familyName = name
                                    )
                            }
                        }
                    }
                    //管理员发帖
                    GROUP_AUTHORITY_MANAGER -> {
                        if (userType == FamilyDetail.USER_TYPE_MASTER
                            || userType == FamilyDetail.USER_TYPE_MANAGER
                        ) {
                            // 跳转到发布页面
                            isNeedRefresh = true
                            getProvider(IPublishProvider::class.java)
                                ?.startEditorActivity(
                                    type = CONTENT_TYPE_POST,
                                    familyId = id,
                                    familyName = name
                                )
                        } else {
                            showToast(R.string.community_not_manager_family_hint)
                        }
                    }
                    else -> {

                    }
                }
            }
        }
    }

    //搜索按钮跳转
    private fun jumpSearchPage() {
        getProvider(ISearchProvider::class.java)?.startSearchGroupActivity(this,mViewModel?.mDetailId.orZero())
    }

    //未加入用户在发布前先提示是否加入
    private fun joinFamilyDialog() {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            if (which == -1) {
                //确定
                mViewModel?.uiDetailState?.value?.success?.run {
                    //加入操作
                    mViewModel?.joinFamily(id, this)
                }
            }
        }
        BaseDialog.Builder(this)
            .setContent(R.string.community_join_family_hint)
            .setNegativeButton(R.string.widget_cancel, listener)
            .setPositiveButton(R.string.widget_sure, listener)
            .create()
            .show()
    }

    //Title按钮点击操作
    private fun titleBtnClick() {
        //-1:申请者  1：群主  2：管理员  3：普通成员 4：黑名单 5:未加入
        mViewModel?.uiDetailState?.value?.success?.run {
            when (userType) {
                FamilyDetail.USER_TYPE_JOINING -> {
                    showToast(R.string.community_joining_family_hint)
                }
                FamilyDetail.USER_TYPE_MASTER -> {
                    //跳转到编辑群组页
                    isNeedRefresh = true
                    mFamilyProvider?.startFamilyEdit(id)
                }
                FamilyDetail.USER_TYPE_MANAGER,
                FamilyDetail.USER_TYPE_MEMBER -> {
                    //退出家族
                    exitFamily(this)
                }
                else -> {
                    //加入操作
                    mViewModel?.joinFamily(id, this)
                }
            }
        }
    }

    //退出家族相关逻辑处理
    private fun exitFamily(detail: FamilyDetail) {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            if (which == -1) {
                //确定退出家族
                mViewModel?.outFamily(detail.id, detail)
            }
        }
        BaseDialog.Builder(this)
            .setContent(R.string.community_exit_family_hint)
            .setNegativeButton(R.string.widget_cancel, listener)
            .setPositiveButton(R.string.widget_sure, listener)
            .create()
            .show()
    }

    //设置Title按钮样式
    private fun setTitleBtnStyle(detail: FamilyDetail) {
        detail.let {
            //-1:申请者  1：群主  2：管理员  3：普通成员 4：黑名单 5:未加入
            if (it.userType == FamilyDetail.USER_TYPE_MASTER) {
                mBinding?.mFamilyDetailTitleJoinTv?.setStyle(FamilyConstant.CONSTANT_STATE_4)
            } else if (it.userType == FamilyDetail.USER_TYPE_JOINING) {
                //加入中
                mBinding?.mFamilyDetailTitleJoinTv?.setStyle(FamilyConstant.CONSTANT_STATE_2)
            } else if (it.userType == FamilyDetail.USER_TYPE_MEMBER || it.userType == FamilyDetail.USER_TYPE_MANAGER) {
                //已加入
                mBinding?.mFamilyDetailTitleJoinTv?.setStyle(FamilyConstant.CONSTANT_STATE_1)
            } else {
                mBinding?.mFamilyDetailTitleJoinTv?.setStyle(FamilyConstant.CONSTANT_STATE_0)
            }
        }
    }

    //获取分区数据
    private fun loadSectionList() {
        mViewModel?.loadFamilySection()
    }

    //家族详情接口
    private fun loadFamilyData(isNeedRefresh: Boolean) {
        this.isNeedRefresh = isNeedRefresh
        if (isNeedRefresh) {
            mViewModel?.loadFamilyDetail()
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                loadFamilyData(true)
            }
        }
    }
}