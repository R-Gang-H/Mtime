package com.kotlin.android.community.family.component.ui.manage

import android.app.Activity
import android.content.Intent
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilySectionManagerBinding
import com.kotlin.android.community.family.component.ui.manage.adapter.FamilySectionManagerItemBinder
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.TitleBarManager
import com.kotlin.android.widget.titlebar.back

/**
 * @des 家族分区管理页面
 * @author zhangjian
 * @date 2022/4/11 17:23
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_SECTION_MANAGER)
class FamilySectionManagerActivity :
    BaseVMActivity<FamilyMemberManageFragViewModel, ActFamilySectionManagerBinding>(),
    MultiStateView.MultiStateListener {

    var mAdapter: MultiTypeAdapter? = null
    var mListData: ArrayList<MultiTypeBinder<*>> = ArrayList()
    var groupId: Long = 0L
    var mCount: Int = 0
    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            groupId = getLongExtra(FamilyConstant.KEY_FAMILY_ID, 0L)
        }
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initView() {
        //设置title
        TitleBarManager.with(this).setTitle(title = "分区管理").back { finishPage() }
        mBinding?.rvSection?.apply {
            mAdapter = createMultiTypeAdapter(
                this,
                LinearLayoutManager(
                    this@FamilySectionManagerActivity,
                    LinearLayoutManager.VERTICAL,
                    false
                )
            )
        }
        mBinding?.mMultiStateView?.setMultiStateListener(this)
    }

    override fun initData() {
        loadSectionData()
    }

    override fun startObserve() {
        mViewModel?.mSectionListUIModelState?.observe(this) {
            it?.apply {
                dismissProgressDialog()
                success?.apply {
                    if (this.sectionList.isNullOrEmpty()) {
                        mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                    mCount = (sectionList?.size) ?: 0
                    mListData.clear()
                    mAdapter?.notifyAdapterClear()
                    sectionList?.forEach {
                        mListData.add(
                            FamilySectionManagerItemBinder(
                                data = it,
                                editor = ::editor,
                                del = ::del
                            )
                        )
                    }
                    mAdapter?.notifyAdapterAdded(mListData)
                }
                error?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }
                netError?.apply {
                    mBinding?.mMultiStateView?.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        }

        mViewModel?.mSectionDeleteUIModelState?.observe(this) {
            it?.apply {
                success?.apply {
                    if (status == 1L) {
                        loadSectionData()
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
        mViewModel?.mSectionEditorUIModelState?.observe(this) {
            it?.apply {
                success?.apply {
                    if (status == 1L) {
                        loadSectionData()
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

    private fun editor(sectionId: Long, sectionName: String) {
        mViewModel?.editorSectionItem(groupId, sectionId, sectionName)
    }

    private fun del(sectionId: Long) {
        mViewModel?.delSectionItem(groupId, sectionId)
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_NO_NET,
            MultiStateView.VIEW_STATE_ERROR -> {
                loadSectionData()
            }
        }
    }

    override fun onBackPressed() {
        finishPage()
    }

    private fun loadSectionData() {
        showProgressDialog()
        mViewModel?.getSectionList(groupId)
    }

    /**
     * 关闭页面向前一个页面传数量值
     */
    private fun finishPage() {
        val intent = Intent()
        intent.putExtra(
            FamilyConstant.KEY_FAMILY_SECTION_COUNT,
            mCount
        )
        setResult(Activity.RESULT_OK, intent)
        finish()
    }
}