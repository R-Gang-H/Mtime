package com.kotlin.android.widget.comment

/**
 * @author vivian.wei
 * @date 2020/6/29
 * @desc 评论组件回调接口
 */
interface IBottomCommentViewCallback {

    /**
     * 点击文本框
     */
    fun onClickContent()

    /**
     * 点击评论Icon
     */
    fun onClickComment()

    /**
     * 点击踩
     */
    fun onClickDislike()

    /**
     * 点击赞
     */
    fun onClickPraise()

    /**
     * 点击收藏
     */
    fun onClickCollect()

}