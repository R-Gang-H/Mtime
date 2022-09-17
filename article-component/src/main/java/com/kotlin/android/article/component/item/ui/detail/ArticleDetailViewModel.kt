package com.kotlin.android.article.component.item.ui.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.article.component.item.bean.ArticleDetailViewBean
import com.kotlin.android.article.component.item.repository.ArticleDetailRepository
import com.kotlin.android.comment.component.DetailBaseViewModel
import com.kotlin.android.comment.component.bean.UgcCommonBarBean
import com.kotlin.android.ugc.detail.component.bean.UgcTitleBarBean
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/8/11
 * description: 文章详情
 */
class ArticleDetailViewModel : DetailBaseViewModel() {
    private val repo by lazy {
        ArticleDetailRepository()
    }
    private val _titleBar = MutableLiveData<UgcTitleBarBean>()

    val titleBar: LiveData<UgcTitleBarBean>
        get() = _titleBar

    //    文章详情
    private val detailUIModel: BaseUIModel<ArticleDetailViewBean> = BaseUIModel()
    val detailState = detailUIModel.uiState

    //    推荐新闻
    private val recommendUIModel = BaseUIModel<MutableList<MultiTypeBinder<*>>>()
    val recommendState = recommendUIModel.uiState


    fun setTitleBar(createUser: UgcCommonBarBean.CreateUser) {
        _titleBar.value = UgcTitleBarBean(
            createUser.userId,
            "${createUser.nikeName}  发布的文章",
            createUser.avatarUrl,
            createUser.createTime,
            createUser.followed,
            false,
            createUser.score,
            userAuthType = createUser.authType
        )
    }

    fun updateTitleBar(isAlbum: Boolean) {
        _titleBar.value?.isAlbumTitle = isAlbum
    }


    /**
     * 加载文章详情
     */
    fun loadDetailData(type: Long, contentId: Long, recId: Long) {
        viewModelScope.launch(main) {
            detailUIModel.emitUIState(showLoading = true)
            val withOnIO = withOnIO {
                repo.loadArticleDetail(contentId, type, recId)
            }
            checkResult(withOnIO, error = { detailUIModel.emitUIState(error = it) },
                netError = { detailUIModel.emitUIState(netError = it) },
                success = { detailUIModel.emitUIState(success = it) },
                empty = { detailUIModel.emitUIState(isEmpty = true) }
            )
        }

    }

    /**
     * 文章关联内容
     */
    fun loadRecommendData(articleId: Long, recId: Long) {
        viewModelScope.launch(main) {
            val withOnIO = withOnIO {
                repo.loadRecommendData(articleId, recId)
            }
            checkResult(withOnIO, error = { recommendUIModel.emitUIState(error = it) },
                netError = { recommendUIModel.emitUIState(netError = it) },
                success = { recommendUIModel.emitUIState(success = it) }
            )
        }
    }


    /**
     * 进入文章详情上报接口
     */
    fun poplarClick(title: String) {
        viewModelScope.launch(main) {
            withOnIO { repo.poplarClick(title) }
        }
    }

}