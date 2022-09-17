package com.kotlin.android.widget.comment

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.kotlin.android.mtime.ktx.ext.ShapeExt
import com.kotlin.android.widget.R
import kotlinx.android.synthetic.main.view_bottom_comment.view.*

/**
 * @author vivian.wei
 * @date 2020/6/29
 * @desc 评论组件
 */
class BottomCommentView(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    private var mType: Int = BottomCommentViewType.COMMON
    private var mCallback: IBottomCommentViewCallback ?= null

    /**
     * 评论组件类型
     */
    object BottomCommentViewType {
        const val COMMON = 1    // 通用评论
        const val REVIEW = 2    // 长影评评论
        const val REPLY = 3     // 回复
        const val DISABLE = 4   // 不可评论
    }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_bottom_comment, this)
        initView()
        initEvent()
    }

    private fun initView() {
        ShapeExt.setViewSelector(mContentTv, corner = 4, normalColor = R.color.color_f2f4f7)
        InitCountUI()
    }

    /**
     * 初始化数字UI
     */
    private fun InitCountUI() {
        hideCountUI()
        setCountBg(mCommentCountTv)
        setCountBg(mDislikeCountTv)
        setCountBg(mPraiseCountTv)
    }

    /**
     * 设置数字背景
     */
    private fun setCountBg(view: TextView) {
        ShapeExt.setViewSelector(view, corner = 8, normalColor = R.color.color_8798af)
    }

    /**
     * 隐藏数字
     */
    private fun hideCountUI() {
        mCommentCountTv.isGone = true
        mDislikeCountTv.isGone = true
        mPraiseCountTv.isGone = true
    }

    /**
     * 显示组件UI
     */
    private fun showUI() {
        when(mType) {
            BottomCommentViewType.COMMON -> {
                // 通用评论：没有踩
                mDislikeIv.isGone = true
            }
            BottomCommentViewType.REPLY -> {
                // 回复：没有评论、踩、收藏
                mCommentIv.isGone = true
                mDislikeIv.isGone = true
                mCollectIv.isGone = true
            }
            BottomCommentViewType.DISABLE -> {
                // 不可评论：没有灰底文本框、评论、踩, 有提示语
                mContentTv.isGone = true
                mCommentIv.isGone = true
                mDislikeIv.isGone = true
                mDisableCommentTipTv.isVisible = true
            }
            else -> {
                // 长影评评论：全显示
            }
        }
    }

    /**
     * 初始化事件
     */
    private fun initEvent() {
        mContentTv.setOnClickListener {
            mCallback?.onClickContent()
        }
        mCommentIv.setOnClickListener {
            mCallback?.onClickComment()
        }
        mDislikeIv.setOnClickListener {
            mCallback?.onClickDislike()
        }
        mPraiseIv.setOnClickListener {
            mCallback?.onClickPraise()
        }
        mCollectIv.setOnClickListener {
            mCallback?.onClickCollect()
        }
    }

    /**
     * 设置组件类型
     */
    fun setType(type: Int) {
        mType = type
        showUI()
    }

    /**
     * 设置回调
     */
    fun setCallback(callback: IBottomCommentViewCallback) {
        mCallback = callback
    }

    /**
     * 更新数量
     */
    fun updateCount(commentCount: Int, dislikeCount: Int, praiseCount: Int) {
        updateViewCount(mCommentCountTv, commentCount)
        updateViewCount(mDislikeCountTv, dislikeCount)
        updateViewCount(mPraiseCountTv, praiseCount)
    }

    /**
     * 更新指定组件数量
     */
    fun updateViewCount(tv: TextView, count: Int) {
        if(count > 0) {
            tv.isVisible = true
            tv.text = if(count > 999) "999+" else count.toString()
        } else {
            tv.isGone = true
        }
    }

}