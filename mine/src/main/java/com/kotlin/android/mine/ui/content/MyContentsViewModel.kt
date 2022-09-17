package com.kotlin.android.mine.ui.content

import android.os.Bundle
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.constant.CommConstant.KEY_CONTENT_TYPE
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_JOURNAL
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_POST
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_FILM_COMMENT
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_ARTICLE
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_VIDEO
import com.kotlin.android.app.data.annotation.CONTENT_TYPE_AUDIO
import com.kotlin.android.app.data.entity.community.content.ArticleUser
import com.kotlin.android.mine.*
import com.kotlin.android.mine.repoistory.ContentsRepository
import com.kotlin.android.mine.ui.content.fragment.ContentsFragment
import com.kotlin.android.router.ext.put
import com.kotlin.android.widget.tablayout.FragPagerItems


class MyContentsViewModel : BaseViewModel() {

    private val repo by lazy {
        ContentsRepository()
    }

    //查询当前文章用户信息
    private val queryArticleUserUIModel: BaseUIModel<ArticleUser> = BaseUIModel()
    val queryArticleUserState = queryArticleUserUIModel.uiState


    fun getPageItem(creator: FragPagerItems,articleUserLimit:Boolean): FragPagerItems {
        creator.add(titleRes = R.string.mine_collection_article, clazz = ContentsFragment::class.java, args = getBundle(CONTENT_TYPE_ARTICLE))
        creator.add(titleRes = R.string.mine_content_tab_long_comment, clazz = ContentsFragment::class.java, args = getBundle(CONTENT_TYPE_FILM_COMMENT))
        creator.add(titleRes = R.string.mine_collection_post, clazz = ContentsFragment::class.java, args = getBundle(CONTENT_TYPE_POST))
        creator.add(titleRes = R.string.mine_content_tab_video, clazz = ContentsFragment::class.java, args = getBundle(CONTENT_TYPE_VIDEO))
        if(!articleUserLimit) creator.add(titleRes = R.string.mine_content_tab_audio, clazz = ContentsFragment::class.java, args = getBundle(CONTENT_TYPE_AUDIO))
        creator.add(titleRes = R.string.mine_content_tab_journal, clazz = ContentsFragment::class.java, args = getBundle(CONTENT_TYPE_JOURNAL))
        return creator
    }

    private fun getBundle(type:Long):Bundle{
        return Bundle().put(KEY_CONTENT_TYPE,type)
    }

    /**
     * 查询当前文章用户信息
     */
    fun getQueryArticleUser() {
        call(
            uiModel = queryArticleUserUIModel
        ) {
            repo.getQueryArticleUser()
        }
    }

}