package com.kotlin.android.search.newcomponent.adapter.binder

import android.text.Html
import android.text.TextUtils
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchHintItemBinding
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2021/5/21
 */
class SearchHintBinder(val item: String, val keyword: String) : MultiTypeBinder<ItemSearchHintItemBinding>() {
    override fun layoutId(): Int {
        return R.layout.item_search_hint_item
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchHintBinder && !TextUtils.equals(item, other.item)
    }

    override fun onBindViewHolder(binding: ItemSearchHintItemBinding, position: Int) {
        binding.mItemSearchHintTv.text = convertToHtml(item, keyword)
    }

    private fun convertToHtml(title: String, keyword: String): CharSequence {
        val htmlKey = "<font color=\"#FF5A36\">${keyword}</font>"
        return Html.fromHtml(title.replace(keyword, htmlKey), Html.FROM_HTML_MODE_LEGACY)
    }
}