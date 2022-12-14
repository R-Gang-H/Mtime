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
    private var mCurSort: Long = 9L //1.?????? 2.?????? 3.?????? 9.????????????
    private var mFamilyDetail: FamilyDetail? = null
    private var isManager = false
    private var mViewPagerAdapter: FragPagerItemAdapter? = null
    private var dialog: EditDialog? = null
    //???????????????,??????????????????????????????
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
                //??????????????????title????????????
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
        //??????????????????
        mBinding?.filterView?.onClickType = {
            mCurSort = it
            notifyFragRefresh(mBinding?.mViewPager?.currentItem ?: 0)
        }
        //viewPager????????????
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

        //???????????????????????????
        mBinding?.ivAddSection?.onClick {
            showAddSectionDialog()
        }

        mBinding?.mFamilyDetailHeadLayout?.mFamilyDetailHeadFamilyUserNumberAllTv?.onClick {
            mFamilyProvider?.startFamilyMember(groupId = mViewModel?.mDetailId ?: 0L)
        }

        mBinding?.mMultiStateView?.setMultiStateListener(this)
    }

    /**
     * ?????????????????????
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
        //????????????????????????
        registerDetailObserve()
        //??????????????????
        registerSectionObserve()
        //??????????????????
        registerAddSectionOberve()
        //??????????????????????????????
        registerJoinFamilyObserve()
        //??????????????????????????????
        registerOutFamilyObserve()
        //??????????????????????????????
        registerShareObserve()
    }

    /**
     * ??????????????????????????????
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
                SharePlatform.COPY_LINK -> {//????????????
                    copyToClipboard(share.url.orEmpty())
                    showToast(R.string.share_copy_link_success)
                    dismissShareDialog()
                }
            }
        }
    }

    /**
     * ?????????????????????
     */
    private fun registerAddSectionOberve() {
        mViewModel?.mAddSectionUIModelState?.observe(this) {
            it?.apply {
                success?.apply {
                    if (status == 1L) {
                        showToast("????????????")
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
     * ?????????????????????
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

    //????????????????????????
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
                    if (bizCode != 1L) { //1 ?????? 2 ??????????????? 3 ???????????????
                        if (bizCode == 2L) {
                            mBinding?.mMultiStateView?.setEmptyResource(resid = R.string.community_detail_family_no)
                        } else {
                            mBinding?.mMultiStateView?.setEmptyResource(resid = R.string.community_detail_family_deleted)
                        }
                        mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                        mBinding?.mFamilyDetailTitleBackIv2?.visible()
                    } else {
                        //????????????
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
     * ????????????UI
     */
    private fun showDetailNext(detail: FamilyDetail) {
        showDetail(detail)
    }

    /**
     * ???????????????fragment????????????
     */
    private fun notifyFragRefresh(position:Int){
        val mCurrentFragment = mViewPagerAdapter?.getPage(position) as BaseVMFragment<*, *>
        mCurrentFragment.notifyRefresh(mCurSort)
    }

    /**
     * ??????viewpager?????????
     */
    private fun setViewPagerData(familySectionList: GroupSectionList? = null, groupId: Long) {
        val sectionIsNotEmpty =
            familySectionList != null && familySectionList.sectionList?.isNotEmpty() == true
        val pagerItems = FragPagerItems(this)
        val data = GroupSectionList()
        data.sectionList = arrayListOf(GroupSection(0L, "??????"))
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
        //????????????????????????0???
        notifyFragRefresh(0)
    }

    //?????????????????????UI??????
    private fun showDetail(detail: FamilyDetail) {
        detail.apply {
            //?????????????????????
            saveCacheFamilyPostCount(id, postNumber)
            //??????????????????
            (mBinding as ActFamilyDetailBinding).mFamilyDetailHeadLayout.setVariable(
                BR.detail,
                this
            )
            //?????????????????????title??????
            mBinding?.mFamilyDetailTitleJoinFl?.visible(userType != FamilyDetail.USER_TYPE_BLACKLIST)
            setTitleBtnStyle(this)
        }
    }

    //??????????????????????????????
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

    //???????????????????????????
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

    //??????????????????????????????
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

    //????????????????????????
    private fun publishBtnClick() {
        afterLogin {
            mViewModel?.uiDetailState?.value?.success?.run {
                when (groupAuthority) {
                    //????????????
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
                                // ?????????????????????
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
                    //????????????
                    GROUP_AUTHORITY_FREE -> {
                        when (userType) {
                            FamilyDetail.USER_TYPE_BLACKLIST -> {
                                showToast(R.string.community_blacklist_family_hint)
                            }
                            else -> {
                                // ????????????????????????????????????????????????????????????
                                // ?????????????????????
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
                    //???????????????
                    GROUP_AUTHORITY_MANAGER -> {
                        if (userType == FamilyDetail.USER_TYPE_MASTER
                            || userType == FamilyDetail.USER_TYPE_MANAGER
                        ) {
                            // ?????????????????????
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

    //??????????????????
    private fun jumpSearchPage() {
        getProvider(ISearchProvider::class.java)?.startSearchGroupActivity(this,mViewModel?.mDetailId.orZero())
    }

    //????????????????????????????????????????????????
    private fun joinFamilyDialog() {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            if (which == -1) {
                //??????
                mViewModel?.uiDetailState?.value?.success?.run {
                    //????????????
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

    //Title??????????????????
    private fun titleBtnClick() {
        //-1:?????????  1?????????  2????????????  3??????????????? 4???????????? 5:?????????
        mViewModel?.uiDetailState?.value?.success?.run {
            when (userType) {
                FamilyDetail.USER_TYPE_JOINING -> {
                    showToast(R.string.community_joining_family_hint)
                }
                FamilyDetail.USER_TYPE_MASTER -> {
                    //????????????????????????
                    isNeedRefresh = true
                    mFamilyProvider?.startFamilyEdit(id)
                }
                FamilyDetail.USER_TYPE_MANAGER,
                FamilyDetail.USER_TYPE_MEMBER -> {
                    //????????????
                    exitFamily(this)
                }
                else -> {
                    //????????????
                    mViewModel?.joinFamily(id, this)
                }
            }
        }
    }

    //??????????????????????????????
    private fun exitFamily(detail: FamilyDetail) {
        val listener = DialogInterface.OnClickListener { dialog, which ->
            dialog.cancel()
            if (which == -1) {
                //??????????????????
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

    //??????Title????????????
    private fun setTitleBtnStyle(detail: FamilyDetail) {
        detail.let {
            //-1:?????????  1?????????  2????????????  3??????????????? 4???????????? 5:?????????
            if (it.userType == FamilyDetail.USER_TYPE_MASTER) {
                mBinding?.mFamilyDetailTitleJoinTv?.setStyle(FamilyConstant.CONSTANT_STATE_4)
            } else if (it.userType == FamilyDetail.USER_TYPE_JOINING) {
                //?????????
                mBinding?.mFamilyDetailTitleJoinTv?.setStyle(FamilyConstant.CONSTANT_STATE_2)
            } else if (it.userType == FamilyDetail.USER_TYPE_MEMBER || it.userType == FamilyDetail.USER_TYPE_MANAGER) {
                //?????????
                mBinding?.mFamilyDetailTitleJoinTv?.setStyle(FamilyConstant.CONSTANT_STATE_1)
            } else {
                mBinding?.mFamilyDetailTitleJoinTv?.setStyle(FamilyConstant.CONSTANT_STATE_0)
            }
        }
    }

    //??????????????????
    private fun loadSectionList() {
        mViewModel?.loadFamilySection()
    }

    //??????????????????
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