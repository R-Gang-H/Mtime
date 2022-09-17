package com.kotlin.android.article.component.item.adapter

import android.view.View
import androidx.databinding.ViewDataBinding
import com.kotlin.android.article.component.R
import com.kotlin.android.article.component.item.bean.ArticleItem
import com.kotlin.android.app.data.entity.CommContent
import com.kotlin.android.app.router.provider.community_person.ICommunityPersonProvider
import com.kotlin.android.app.router.provider.ugc.IUgcProvider
import com.kotlin.android.router.ext.getProvider
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/28
 */
abstract class ArticleBaseItemBinder<V : ViewDataBinding>(val articleItem: ArticleItem)
    : MultiTypeBinder<V>() {

    override fun onClick(view: View) {
        when(view.id) {
            R.id.mNewsCommentCountTv,
            R.id.mNewsRoot -> {
                getProvider(IUgcProvider::class.java) {
                    launchDetail(
                        articleItem.id,
                        articleItem.type,
                        needToComment = view.id == R.id.mNewsCommentCountTv
                    )
                }
            }
            R.id.mNewsAuthorIv,
            R.id.mNewsAuthorTv -> {
                getProvider(ICommunityPersonProvider::class.java) {
                    startPerson(articleItem.authorId)
                }
            }
            else -> {
                super.onClick(view)
            }
        }
    }
}