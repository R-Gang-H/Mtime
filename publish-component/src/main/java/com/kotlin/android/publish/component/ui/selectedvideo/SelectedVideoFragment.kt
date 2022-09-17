package com.kotlin.android.publish.component.ui.selectedvideo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import com.kotlin.android.app.router.liveevent.event.CloseState
import com.kotlin.android.core.BaseVMDialogFragment
import com.kotlin.android.core.ITitleBarOfFragment
import com.kotlin.android.ktx.ext.immersive.immersive
import com.kotlin.android.ktx.lifecycle.viewModels
import com.kotlin.android.mtime.ktx.ext.progressdialog.showOrHideProgressDialog
import com.kotlin.android.mtime.ktx.getColor
import com.kotlin.android.publish.component.R
import com.kotlin.android.publish.component.databinding.DialogSelectVideoBinding
import com.kotlin.android.publish.component.ui.adapter.VideoListAdapter
import com.kotlin.android.publish.component.ui.video.PreviewVideoActivity
import com.kotlin.android.publish.component.widget.selector.LocalMedia
import com.kotlin.android.router.bus.ext.observe
import com.kotlin.android.widget.multistate.MultiStateView
import com.scwang.smart.refresh.layout.listener.OnMultiListener

/**
 * create by lushan on 2022/4/6
 * des:选择发布视频列表
 **/
class SelectedVideoFragment :
    BaseVMDialogFragment<SelectVideoViewModel, DialogSelectVideoBinding>() {
    private var videoAdapter: VideoListAdapter? = null
    var listener: ((LocalMedia) -> Unit)? = null
    var toVideoPublish: Boolean = true

    override fun startObserve() {
        mViewModel?.videoState?.observe(this, Observer {
            it?.apply {
                success?.apply {
                    if (isRefresh && this.isEmpty()) {
                        mBinding?.stateView?.setViewState(MultiStateView.VIEW_STATE_EMPTY)
                    } else {
                        mBinding?.stateView?.setViewState(MultiStateView.VIEW_STATE_CONTENT)
                    }
                    if (isRefresh) {
                        videoAdapter?.setData(this, toVideoPublish)
                    }else{
                        videoAdapter?.addData(this,toVideoPublish)
                    }
                    mBinding?.refreshLayout?.finishLoadMore()
                    mBinding?.refreshLayout?.finishRefresh()
                    mBinding?.refreshLayout?.setNoMoreData(noMoreData)
                }
            }
        })

        observe(CloseState::class.java, Observer {
            dismissAllowingStateLoss()
        })
    }

    private fun loadVideoData(isRefresh: Boolean) {
        mViewModel?.getAllVideoData(isRefresh)
    }

    private fun createTitleBar() {
        mBinding?.titleBar?.addItem(
            drawableRes = R.drawable.ic_title_bar_back_light,
            reverseDrawableRes = R.drawable.ic_title_bar_back_dark
        ) {
            dismiss()
        }?.setTitle(titleRes = R.string.publish_component_all_video)
    }

    private fun initRv() {
        videoAdapter = VideoListAdapter(requireActivity())
        mBinding?.videoRv?.apply {
            layoutManager = GridLayoutManager(requireActivity(), 3)
            adapter = videoAdapter
        }

    }

    override fun initEnv() {
        theme = {
            setStyle(STYLE_NORMAL, R.style.ImmersiveDialog)
        }
        window = {
            setWindowAnimations(R.style.BottomDialogAnimation)
        }
        immersive = {
            dialog?.apply {
                activity?.immersive(this)
                    ?.updateStatusBarColor(getColor(R.color.color_ffffff))
                    ?.statusBarDarkFont(true)
            }

        }
    }

    override fun initView() {
        createTitleBar()
        initRv()
        initSmartRefreshLayout()
    }

    private fun initSmartRefreshLayout() {
        mBinding?.refreshLayout?.apply {
            setOnLoadMoreListener {
                loadVideoData(false)
            }
            setOnRefreshListener {
                loadVideoData(true)
            }
            autoRefresh()
        }

    }

    override fun initVM(): SelectVideoViewModel = viewModels<SelectVideoViewModel>().value

    override fun initData() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_VIDEO) {

            data?.getParcelableExtra<LocalMedia>(PreviewVideoActivity.KEY_VIDEO_BEAN)?.apply {
                listener?.invoke(this)
            }

            dismissAllowingStateLoss()
        }
    }


}