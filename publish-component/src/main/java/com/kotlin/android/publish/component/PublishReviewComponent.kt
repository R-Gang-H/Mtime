package com.kotlin.android.publish.component

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout

/**
 * @author vivian.wei
 * @date 2020/7/9
 * @desc 发布长短影评组件
 */
class PublishReviewComponent(context: Context, attrs: AttributeSet) : ConstraintLayout(context, attrs) {

    init {
        LayoutInflater.from(context).inflate(R.layout.view_publish_review, this)
        initView()
    }

    private fun initView() {

    }

}