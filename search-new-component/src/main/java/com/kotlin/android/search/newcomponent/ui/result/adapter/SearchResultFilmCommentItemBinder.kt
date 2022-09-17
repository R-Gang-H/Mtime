package com.kotlin.android.search.newcomponent.ui.result.adapter

import com.kotlin.android.ktx.ext.convertToHtml
import com.kotlin.android.mtime.ktx.formatCount
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.app.router.path.RouterProviderPath
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.search.newcomponent.R
import com.kotlin.android.search.newcomponent.databinding.ItemSearchResultFilmCommentBinding
import com.kotlin.android.search.newcomponent.ui.result.SearchResultConstant
import com.kotlin.android.search.newcomponent.ui.result.bean.FilmCommentItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

class SearchResultFilmCommentItemBinder(val keyword: String, val bean : FilmCommentItem)
    : MultiTypeBinder<ItemSearchResultFilmCommentBinding>() {

    val mProvider: IUgcProvider? = getProvider(IUgcProvider::class.java)

    override fun layoutId(): Int {
        return R.layout.item_search_result_film_comment
    }

    override fun areContentsTheSame(other: MultiTypeBinder<*>): Boolean {
        return other is SearchResultFilmCommentItemBinder && other.bean == bean
    }

    override fun onBindViewHolder(binding: ItemSearchResultFilmCommentBinding, position: Int) {
        // 搜索关键词标红
        binding.mItemSearchResultFilmCommentTitleTv.convertToHtml(bean.filmCommentTitle, SearchResultConstant.MATCH_KEYWORD_COLOR, keyword)
        // 赞数
        binding.mItemSearchResultFilmCommentLikeTv.text = formatCount(bean.likeUp, false)
        // 踩数
        binding.mItemSearchResultFilmCommentDislikeTv.text = formatCount(bean.likeDown, false)
    }

    /**
     * 点赞、取消点赞改变
     */
//    fun praiseUpChanged() {
//        // 取消点踩（点踩与点赞互斥）
//        if (bean.isLikeDown) {
//            bean.isLikeDown = false
//            bean.likeDown--
//        }
//        bean.isLikeUp = !bean.isLikeUp
//        if (bean.isLikeUp) {
//            bean.likeUp++
//        } else {
//            bean.likeUp--
//        }
//        notifyAdapterSelfChanged()
//    }

    /**
     * 点踩、取消点踩改变
     */
//    fun praiseDownChanged() {
//        // 取消点赞（点踩与点赞互斥）
//        if (bean.isLikeUp) {
//            bean.isLikeUp = false
//            bean.likeUp--
//        }
//        bean.isLikeDown = !bean.isLikeDown
//        if (bean.isLikeDown) {
//            bean.likeDown++
//        } else {
//            bean.likeDown--
//        }
//        notifyAdapterSelfChanged()
//    }
    
}