package com.kotlin.tablet.adapter

import com.kotlin.android.app.data.entity.search.Movie
import com.kotlin.android.image.coil.ext.loadImage
import com.kotlin.android.ktx.ext.dimension.dp
import com.kotlin.android.ktx.ext.dimension.dpF
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemFilmListSelectedBinding

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/15
 * 描述:已选影片binder
 **/
class FilmListSelectedBinder(val bean: Movie, var isSelect: Boolean = false) :
    MultiTypeBinder<ItemFilmListSelectedBinding>() {

    override fun layoutId(): Int = R.layout.item_film_list_selected

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean =
        other is FilmListSelectedBinder && other.bean == bean

    override fun onBindViewHolder(binding: com.kotlin.tablet.databinding.ItemFilmListSelectedBinding, position: Int) {
        binding.apply {
            mFilmCoverIv.loadImage(bean.img, roundedRadius = 4.dpF, width = 58.dp, height = 77.dp)
            mFilmYearTv.text = getYearFormat(bean.year)
            mFilmTitleTv.text = when{
                bean.name?.isNotBlank() == true -> bean.name
                bean.nameEn?.isNotBlank() == true -> bean.nameEn
                else -> ""
            }
        }
    }

    private fun getYearFormat(year: String?): String {
        return year?.let {
            if (it.isBlank() or (it == "0")) {
                ""
            } else {
                "($it)"
            }
        } ?: ""
    }
}