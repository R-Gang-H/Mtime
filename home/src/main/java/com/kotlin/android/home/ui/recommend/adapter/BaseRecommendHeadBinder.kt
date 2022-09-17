package com.kotlin.android.home.ui.recommend.adapter

import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 推荐页面头部binder基类
 */
abstract class BaseRecommendHeadBinder<V : ViewDataBinding> : MultiTypeBinder<V>() {
    override fun onBindViewHolder(binding: V, position: Int) {
        super.onBindViewHolder(binding, position)
        binding.root.apply {
            if (layoutParams is StaggeredGridLayoutManager.LayoutParams) {
                (layoutParams as StaggeredGridLayoutManager.LayoutParams).isFullSpan = true
            }  
        }
    }
}