package com.kotlin.android.widget.refresh

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import com.kotlin.android.ktx.ext.core.gone
import com.kotlin.android.ktx.ext.core.visible
import com.kotlin.android.widget.R
import com.scwang.smart.drawable.ProgressDrawable
import com.scwang.smart.refresh.layout.api.RefreshHeader
import com.scwang.smart.refresh.layout.api.RefreshKernel
import com.scwang.smart.refresh.layout.api.RefreshLayout
import com.scwang.smart.refresh.layout.constant.RefreshState
import com.scwang.smart.refresh.layout.constant.SpinnerStyle
import kotlinx.android.synthetic.main.view_refresh_header.view.*

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/7/16
 *
 * 默认刷新头
 */
class MRefreshHeader(context: Context): FrameLayout(context), RefreshHeader {

    private var mHeaderView: View = LayoutInflater.from(context)
            .inflate(R.layout.view_refresh_header, this, true)
    //刷新动画
    private var mProgressDrawable: ProgressDrawable = ProgressDrawable()

    init {
        mProgressView.setImageDrawable(mProgressDrawable)
    }

    override fun getSpinnerStyle(): SpinnerStyle {
        return SpinnerStyle.Translate //指定为平移
    }

    override fun onFinish(refreshLayout: RefreshLayout, success: Boolean): Int {
        mProgressDrawable.stop() //停止动画
        if (success) {
            mHeaderText.setText(R.string.widget_refresh_success)
        } else {
            mHeaderText.setText(R.string.widget_refresh_failure)
        }
        return 100 //延迟之后再弹回

    }

    override fun getView(): View {
        return this
    }

    override fun onStartAnimator(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {
        mProgressDrawable.start() //开始动画
    }

    override fun onStateChanged(refreshLayout: RefreshLayout, oldState: RefreshState, newState: RefreshState) {
        when (newState) {
            RefreshState.None,
            RefreshState.PullDownToRefresh -> {
                mHeaderView.visible()
                mHeaderText.setText(R.string.widget_refresh_pull)
                mArrowView.visible() //显示下拉箭头
                mProgressView.gone() //隐藏动画
                mArrowView.animate().rotation(0f) //还原箭头方向
            }
            RefreshState.Refreshing -> {
                mHeaderText.setText(R.string.widget_refresh_ing)
                mProgressView.visible() //显示加载动画
                mArrowView.gone() //隐藏箭头
            }
            RefreshState.ReleaseToRefresh -> {
                mHeaderText.setText(R.string.widget_refresh_release)
                mArrowView.animate().rotation(180f) //显示箭头改为朝上
            }
        }
    }

    override fun setPrimaryColors(vararg colors: Int) {}

    override fun onInitialized(kernel: RefreshKernel, height: Int, maxDragHeight: Int) {}

    override fun onHorizontalDrag(percentX: Float, offsetX: Int, offsetMax: Int) {}

    override fun onReleased(refreshLayout: RefreshLayout, height: Int, maxDragHeight: Int) {}

    override fun onMoving(isDragging: Boolean, percent: Float, offset: Int, height: Int, maxDragHeight: Int) {}

    override fun isSupportHorizontalDrag(): Boolean {
        return false
    }


}