package com.kotlin.android.search.newcomponent.ui.publish.adapter

import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.screenWidth
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemPublishSearchMovieBinding
import com.kotlin.android.search.newcomponent.ui.result.bean.MovieItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/7
 * 描述: 发布组件-关联电影-搜索结果Binder
 */
class PublishSearchMovieItemBinder(
        val movie: Movie,
        val viewBean : MovieItem
        ) : MultiTypeBinder<ItemPublishSearchMovieBinding>() {

    companion object {
        const val TYPE_LOCATION_SPLIT = "，"
    }

    private val mRootPaddingLeftRight = 15.dp
    private val mImgWidth = 56.dp
    private val mNameMarginStart = 12.dp


    override fun layoutId(): Int = R.layout.item_publish_search_movie

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is PublishSearchMovieItemBinder && other.viewBean == viewBean
    }

    override fun onBindViewHolder(binding: ItemPublishSearchMovieBinding, position: Int) {
        binding.apply {
            yearTv.text = if(viewBean.year.isEmpty()) "" else "（${viewBean.year}）"
            // 中文名最大宽度
            nameTv.maxWidth = screenWidth - mRootPaddingLeftRight - mImgWidth
                              - mNameMarginStart - yearTv.measuredWidth - mRootPaddingLeftRight
            nameTv.text = viewBean.name.ifEmpty { viewBean.nameEn }
            nameEnTv.text = if(viewBean.name.isEmpty()) "" else viewBean.nameEn
            typeTv.text = StringBuffer().apply {
                if (viewBean.movieType.isNotEmpty()) {
                    append(viewBean.movieType)
                }
                if (viewBean.rLocation.isNotEmpty()) {
                    if(isNotEmpty()) {
                        append(TYPE_LOCATION_SPLIT)
                    }
                    append(viewBean.rLocation)
                }
            }.toString()
        }
    }

}