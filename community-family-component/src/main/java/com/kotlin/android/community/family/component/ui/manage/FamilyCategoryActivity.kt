package com.kotlin.android.community.family.component.ui.manage

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.community.family.component.R
import com.kotlin.android.community.family.component.databinding.ActFamilyCategoryBinding
import com.kotlin.android.community.family.component.ui.manage.adapter.FamilyCategoryItemBinder
import com.kotlin.android.community.family.component.ui.manage.constant.FamilyConstant
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.app.data.entity.community.group.GroupCategory
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.titlebar.CommonTitleBar
import kotlinx.android.synthetic.main.act_family_category.*

/**
 * @author vivian.wei
 * @date 2020/8/10
 * @desc 家族群组分类页
 */
@Route(path = RouterActivityPath.CommunityFamily.PAGER_FAMILY_CATEGORY)
class FamilyCategoryActivity: BaseVMActivity<FamilyCategoryViewModel, ActFamilyCategoryBinding>(),
        MultiStateView.MultiStateListener {

    private lateinit var titleView: CommonTitleBar
    private var mPrimaryCategoryId: Long = 0
    private var mPrimaryCategoryName: String = ""
    private lateinit var mAdapter: MultiTypeAdapter
    private var mSelectedPosition = -1
    private var mValid = true

    override fun initVM(): FamilyCategoryViewModel {
        mPrimaryCategoryId = intent.getLongExtra(FamilyConstant.KEY_FAMILY_PRIMARY_CATEGORY_ID, 0)
        return viewModels<FamilyCategoryViewModel>().value
    }

    override fun initTheme() {
        super.initTheme()
        immersive()
            .statusBarColor(getColor(R.color.color_ffffff))
            .statusBarDarkFont(true)
    }

    override fun initCommonTitleView() {
        titleView = CommonTitleBar()
        titleView.init(this)
                .setTitle(R.string.family_category)
                .setRightTextAndClick(R.string.community_save_btn, View.OnClickListener {
                    save()
                })
                .setRightTextColor(R.color.color_aab7c7_20_alpha)
                .create()
    }

    override fun initView() {
        mAdapter = createMultiTypeAdapter(mActFamilyCategoryRv, LinearLayoutManager(this))
        mMultiStateView.setMultiStateListener(this)
    }

    override fun initData() {
        mViewModel?.getCommunityGroupCategory()
    }

    override fun startObserve() {
        mViewModel?.uiState?.observe(this, Observer {
            it.apply {
                if(showLoading) {
                    showProgressDialog()
                } else {
                    dismissProgressDialog()
                }

                success?.apply {
                    showCategoryList(this.list)
                }

                isEmpty.apply {
                    if(this) {
                        mMultiStateView.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    }
                }

                error?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_ERROR)
                }

                netError?.apply {
                    mMultiStateView.setViewState(MultiStateView.VIEW_STATE_NO_NET)
                }
            }
        })
    }

    /**
     * 点击页面错误状态"图标/按钮"后处理事件
     */
    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                // 获取榜单详情
                mViewModel?.getCommunityGroupCategory()
            }
        }
    }

    /**
     * 显示分类列表
     */
    private fun showCategoryList(list: List<GroupCategory>?) {
        list?.let {
            mAdapter.notifyAdapterAdded(getBinderList(it))
            if (mPrimaryCategoryId > 0) {
                mSelectedPosition = getSelectedPosition(it)
                mAdapter.notifyItemChanged(mSelectedPosition)
            }
            mValid = mPrimaryCategoryId > 0
            // 更新保存按钮状态
            updateSaveBtn()
        }
    }

    /**
     * 分类列表Binder
     */
    private fun getBinderList(list: List<GroupCategory>): List<MultiTypeBinder<*>> {
        val banderList = ArrayList<FamilyCategoryItemBinder>()
        list.map {
            val binder = FamilyCategoryItemBinder(this, it, :: onItemClick)
            banderList.add(binder)
        }
        return banderList
    }

    /**
     * 分类列表item点击事件回调
     */
    private fun onItemClick(bean: GroupCategory, newSelect: Int) {
        mViewModel?.uiState?.value?.success?.list?.let {
            // 取消选中的
            if(mSelectedPosition > -1) {
                it.get(mSelectedPosition).let { category ->
                    category.isSelect = false
                    mAdapter.notifyItemChanged(mSelectedPosition)
                }
            }
            // 选中点击的
            it.get(newSelect).let { category ->
                category.isSelect = true
                mAdapter.notifyItemChanged(newSelect)
            }
        }
        mSelectedPosition = newSelect
        mPrimaryCategoryId = bean.primaryCategoryId
        mPrimaryCategoryName = bean.primaryCategoryName ?: ""
        if(!mValid) {
            mValid = true
            updateSaveBtn()
        }
    }

    /**
     * 更新保存按钮状态
     */
    private fun updateSaveBtn() {
        titleView.setRightTextColor(if(mValid) R.color.color_20a0da else R.color.color_aab7c7_20_alpha)
    }

    /**
     * 获取选中的索引值
     */
    private fun getSelectedPosition(list: List<GroupCategory>): Int {
        var selectedPosition: Int = -1
        var endIndex = list.size - 1
        for (i in 0..endIndex) {
            if (list.get(i).primaryCategoryId == mPrimaryCategoryId) {
                list.get(i).isSelect = true
                mPrimaryCategoryName = list.get(i).primaryCategoryName ?: ""
                selectedPosition = i
                break
            }
        }
        return selectedPosition
    }

    /**
     * 保存
     */
    private fun save() {
        if(!mValid) {
            return
        }
        val intent = Intent()
        intent.putExtra(FamilyConstant.KEY_FAMILY_PRIMARY_CATEGORY_ID, mPrimaryCategoryId)
        intent.putExtra(FamilyConstant.KEY_FAMILY_PRIMARY_CATEGORY_NAME, mPrimaryCategoryName)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

}