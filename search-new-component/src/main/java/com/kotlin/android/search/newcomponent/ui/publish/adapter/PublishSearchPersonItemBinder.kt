package com.kotlin.android.search.newcomponent.ui.publish.adapter

import com.kotlin.android.app.data.entity.search.Person
import com.kotlin.android.ktx.ext.core.getString
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemPublishSearchPersonBinding
import com.kotlin.android.search.newcomponent.ui.result.bean.PersonItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * 创建者: vivian.wei
 * 创建时间: 2022/4/8
 * 描述: 发布组件-关联影人-搜索结果Binder
 */
class PublishSearchPersonItemBinder(
        val person: Person,
        val viewBean : PersonItem
) : MultiTypeBinder<ItemPublishSearchPersonBinding>() {

    override fun layoutId(): Int = R.layout.item_publish_search_person

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is PublishSearchPersonItemBinder && other.viewBean == viewBean
    }

    override fun onBindViewHolder(binding: ItemPublishSearchPersonBinding, position: Int) {
        binding.apply {
            nameTv.text = viewBean.name.ifEmpty { viewBean.nameEn }
            nameEnTv.text = if(viewBean.name.isEmpty()) "" else viewBean.nameEn
            // 代表作
            movieTv.apply {
                var movies = ""
                viewBean.personMovies.map {
                            movies += getString(R.string.search_result_person_movie, it)
                        }
                text = if(movies.isNotEmpty())
                            getString(R.string.search_result_person_movies, movies)
                        else
                            ""
            }
        }
    }

}