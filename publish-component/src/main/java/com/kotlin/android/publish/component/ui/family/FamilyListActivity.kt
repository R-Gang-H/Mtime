package com.kotlin.android.publish.component.ui.family

import android.content.Intent
import android.view.Gravity
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.android.arouter.facade.annotation.Route
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_POST
import com.kotlin.android.app.data.entity.community.group.MyGroupList
import com.kotlin.android.core.BaseVMActivity
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.publish.component.Publish
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.ActFamilyListBinding
import com.kotlin.android.publish.component.ui.adapter.FamilyListAdapter
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterActivityPath
import com.kotlin.android.app.router.provider.publish.IPublishProvider
import com.kotlin.android.ktx.ext.orZero
import com.kotlin.android.widget.titlebar.State
import com.kotlin.android.widget.titlebar.ThemeStyle
import com.kotlin.android.widget.titlebar.back
import kotlinx.android.synthetic.main.act_family_list.*

/**
 * 我的家族列表:
 *
 * Created on 2020/10/14.
 *
 * @author o.s
 */
@Route(path = RouterActivityPath.Publish.PAGER_FAMILY_LIST_ACTIVITY)
class FamilyListActivity : BaseVMActivity<FamilyListViewModel, ActFamilyListBinding>() {

    private val mProvide by lazy {
        getProvider(IPublishProvider::class.java)
    }

    private val mAdapter by lazy {
        FamilyListAdapter {
            Intent().apply {
                putExtra(Publish.KEY_TYPE, CONTENT_TYPE_POST)
                putExtra(Publish.KEY_FAMILY_ID, it.groupId)
                putExtra(Publish.KEY_FAMILY_NAME, it.groupName)
            }.apply {
                setResult(Publish.FAMILY_LIST_RESULT_CODE, this)
            }
            finish()
        }
    }

    private var isFootmarks = false

    override fun initVM(): FamilyListViewModel = viewModels<FamilyListViewModel>().value

    override fun getIntentData(intent: Intent?) {
        super.getIntentData(intent)
        intent?.apply {
            isFootmarks = getBooleanExtra(Publish.KEY_IS_FOOTMARKS, false)
        }
    }

    override fun initView() {
        immersive().statusBarColor(getColor(R.color.color_ffffff))
                .statusBarDarkFont(true)
        initTitleView()
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }
    }

    private fun initTitleView() {
        titleBar?.apply {
            setThemeStyle(ThemeStyle.STANDARD)
            setState(State.NORMAL)
            setTitle(
                    title = getString(R.string.chose_publish_family),
                    gravity = Gravity.CENTER
            )
            back {
                finish()
            }
        }
    }

    override fun initData() {
        if (isFootmarks) {
            mViewModel?.getGroupFootmarks()
        } else {
            mViewModel?.loadFamilyList()
        }
    }

    override fun startObserve() {
        mViewModel?.uiState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    mAdapter.setData(this)
                }
            }
        }
        mViewModel?.footmarksUIState?.observe(this) {
            it?.apply {
                showProgressDialog(showLoading)

                success?.apply {
                    val groups = ArrayList<MyGroupList.Group>()
                    this.groupList?.forEach { group ->
                        groups.add(
                            MyGroupList.Group(
                                groupId = group.groupId.orZero(),
                                groupName = group.name,
                                groupImg = group.logoPath,
                            )
                        )
                    }
                    mAdapter.setData(groups)
                }
            }
        }
    }

}