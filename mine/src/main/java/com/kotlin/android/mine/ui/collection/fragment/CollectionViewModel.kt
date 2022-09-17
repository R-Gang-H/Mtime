package com.kotlin.android.mine.ui.collection.fragment

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_ARTICLE
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_CINEMA
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_MOVIE
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_PERSON
import com.kotlin.android.app.data.constant.CommConstant.COLLECTION_TYPE_POST
import com.kotlin.android.mine.*
import com.kotlin.android.mine.bean.CollectionViewBean
import com.kotlin.android.mine.repoistory.CollectionRepository
import kotlinx.coroutines.launch

/**
 * create by lushan on 2020/9/11
 * description:电影收藏
 */
class CollectionViewModel:BaseViewModel() {
    private val repo by lazy {
        CollectionRepository()
    }
    private val collectionUIModel:BaseUIModel<CollectionViewBean> = BaseUIModel()
    val collectionState = collectionUIModel.uiState

    /**
     * 获取收藏影片数据
     */
    fun getCollectionData(type: Long, isMore:Boolean){
        viewModelScope.launch(main){
            val result = withOnIO {
                requestData(type, isMore)
            }
            checkResult(result,success = {collectionUIModel.emitUIState(success = it,loadMore = isMore,noMoreData = it.hasNext.not())},
            error = {collectionUIModel.emitUIState(error = it)},netError = {collectionUIModel.emitUIState(netError = it)})
        }
    }

    private suspend fun requestData(type: Long, isMore: Boolean):ApiResult<CollectionViewBean>{
        return when(type){
            COLLECTION_TYPE_MOVIE->{//影片
                repo.getCollectionMovie(isMore)
            }
            COLLECTION_TYPE_CINEMA->{//影院
                repo.getCollectionCinema(isMore)
            }
            COLLECTION_TYPE_PERSON->{//影人
                repo.getCollectionPerson(isMore)
            }
            COLLECTION_TYPE_ARTICLE->{//文章
                repo.getCollectionArticle(isMore)
            }
            COLLECTION_TYPE_POST->{
                repo.getCollectionPost(isMore)
            }

            else->repo.getCollectionMovie(isMore)
        }
    }
}