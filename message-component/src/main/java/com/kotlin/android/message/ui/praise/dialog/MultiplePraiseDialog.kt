package com.kotlin.android.message.ui.praise.dialog

import android.view.Gravity
import android.view.WindowManager
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.annotation.DialogFragmentTag
import com.kotlin.android.message.R
import com.kotlin.android.message.databinding.MessageDialogMultiplePraiseBinding
import com.kotlin.android.mtime.ktx.ext.progressdialog.dismissProgressDialog
import com.kotlin.android.mtime.ktx.ext.progressdialog.showProgressDialog
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.widget.adapter.multitype.MultiTypeAdapter
import com.kotlin.android.widget.adapter.multitype.createMultiTypeAdapter
import com.kotlin.android.widget.multistate.MultiStateView
import com.kotlin.android.widget.multistate.ext.complete
import com.kotlin.android.widget.refresh.ext.complete

/**
 * Created by zhaoninglongfei on 2022/4/26
 * 多人点赞弹框
 */
@DialogFragmentTag(tag = "tag_dialog_multiple_praise_dialog")
class MultiplePraiseDialog(val messageId: String, val title: String?) :
    BaseVMDialogFragment<MultiplePraiseViewModel, MessageDialogMultiplePraiseBinding>(),
    MultiStateView.MultiStateListener {

    private lateinit var adapter: MultiTypeAdapter

    companion object {
        fun newInstance(messageId: String, title: String) = MultiplePraiseDialog(messageId, title)
    }

    override fun initEnv() {
        window = {
            decorView.setPadding(0, 0, 0, 0)
            attributes.run {
                width = WindowManager.LayoutParams.MATCH_PARENT
                height = WindowManager.LayoutParams.WRAP_CONTENT
                gravity = Gravity.BOTTOM
                windowAnimations = R.style.BottomDialogAnimation
            }
            setBackgroundDrawable(null)
        }
    }

    override fun initView() {
        mBinding?.tvPraiseCount?.text = title
        mBinding?.apply {
            adapter = createMultiTypeAdapter(rvMultiplePraise)
            mMultiStateView.setMultiStateListener(this@MultiplePraiseDialog)
            refreshLayout.setOnRefreshListener {
                mViewModel?.loadMultiplePraiseList(messageId, true)
            }
            refreshLayout.setOnLoadMoreListener {
                mViewModel?.loadMultiplePraiseList(messageId, false)
            }
        }
        mBinding?.ivClose?.setOnClickListener {
            this.dismiss()
        }
    }

    override fun initData() {
        mBinding?.refreshLayout?.autoRefresh()
    }

    override fun startObserve() {
        mViewModel?.multiplePraiseUiState?.observe(this) {
            it?.apply {
                mBinding?.refreshLayout.complete(it)
                mBinding?.mMultiStateView.complete(it)

                success?.let {
                    binders?.apply {
                        if (isRefresh) {
                            adapter.notifyAdapterRemovedAll {
                                adapter.notifyAdapterAdded(this)
                            }
                        } else {
                            adapter.notifyAdapterAdded(this)
                        }
                    }
                }
            }
        }

        mViewModel?.followUserUiState?.observe(this) {
            it?.apply {
                if (showLoading) {
                    showProgressDialog()
                }

                success?.let {
                    dismissProgressDialog()
                }

                error?.let { error ->
                    dismissProgressDialog()
                    showToast(error)
                }

                netError?.let { error ->
                    dismissProgressDialog()
                    showToast(error)
                }
            }
        }
    }

    override fun onMultiStateChanged(viewState: Int) {
        when (viewState) {
            MultiStateView.VIEW_STATE_ERROR,
            MultiStateView.VIEW_STATE_NO_NET -> {
                mBinding?.refreshLayout?.autoRefresh()
            }
        }
    }
}