package com.kotlin.tablet.adapter

import com.kotlin.android.app.data.entity.filmlist.Category
import com.kotlin.android.ktx.ext.click.onClick
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import com.kotlin.tablet.R
import com.kotlin.tablet.databinding.ItemFilmLabelLayoutBinding

class FilmListLabelBinder(val category: List<Category>) :
    MultiTypeBinder<ItemFilmLabelLayoutBinding>() {

    var selectPosition = -1
    override fun layoutId(): Int {
        return R.layout.item_film_label_layout
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is FilmListLabelBinder
    }

    override fun onBindViewHolder(binding: ItemFilmLabelLayoutBinding, position: Int) {
        selectPosition = position
        binding.tvCategoryName.text = category.get(position).categoryName
        binding.tvCategoryName.isSelected = category.get(position).isSelect
        binding.binder = this
    }
}