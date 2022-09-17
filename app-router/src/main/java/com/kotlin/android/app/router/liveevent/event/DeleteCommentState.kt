package com.kotlin.android.app.router.liveevent.event

import com.jeremyliao.liveeventbus.core.LiveEvent

/**
 * create by lushan on 2020/10/20
 * description:评论详情中删除评论
 */
class DeleteCommentState(var commentId:Long = 0L,var isCommentPositive:Boolean = false):LiveEvent