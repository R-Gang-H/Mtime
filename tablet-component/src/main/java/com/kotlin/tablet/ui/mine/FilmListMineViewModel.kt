package com.kotlin.tablet.ui.mine

import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.filmlist.FilmListCollectExtend
import com.kotlin.android.app.data.entity.filmlist.MyCollectionFilmList
import com.kotlin.android.app.data.entity.filmlist.MyCreateFilmList
import com.kotlin.android.core.BaseViewModel
import com.kotlin.tablet.repository.FilmListRepository

/**
 * 创建者: SunHao
 * 创建时间: 2022/3/28
 * 描述:个人中心-我的片单
 **/
class FilmListMineViewModel : BaseViewModel() {

    companion object{
        const val ACTION_CANCEL = 2L//取消收藏
        const val OBJ_TYPE_FILM_LIST = 10L//片单取消
    }
    private val repo by lazy { FilmListRepository() }

    private val myCreateUIModel by lazy { BaseUIModel<MyCreateFilmList>() }
    val myCreateUIState = myCreateUIModel.uiState

    private val myCollectionUIModel by lazy { BaseUIModel<MyCollectionFilmList>() }
    val myCollectionUIState = myCollectionUIModel.uiState

    private val cancelCollectUIModel by lazy { BaseUIModel<FilmListCollectExtend<Any>>() }
    val cancelCollectUIState = cancelCollectUIModel.uiState

    /**
     * 我创建的片单列表
     */
    fun reqMyCreateData(isRefresh: Boolean) {
        call(
            uiModel = myCreateUIModel,
            isRefresh = isRefresh,
            pageStamp = { it.nextStamp },
            isEmpty = { it.myCreates.isNullOrEmpty() },
            hasMore = { it.hasNext == true }) {
            repo.reqMyCreates(
                nextStamp = myCreateUIModel.pageStamp,
                pageSize = myCreateUIModel.pageSize
            )
        }
    }

    /**
     * 我收藏的片单列表
     */
    fun reqMyCollectionData(isRefresh: Boolean) {
        call(
            uiModel = myCollectionUIModel,
            isRefresh = isRefresh,
            pageStamp = { it.nextStamp },
            isEmpty = { it.myFavorites.isNullOrEmpty() },
            hasMore = { it.hasNext == true }) {
            repo.reqMyCollection(
                nextStamp = myCollectionUIModel.pageStamp,
                pageSize = myCollectionUIModel.pageSize
            )
        }
    }

    /**
     * 取消收藏
     */
    fun cancelCollect(filmListId: Long, position: Int) {
        call(uiModel = cancelCollectUIModel) {
            repo.cancelCollect(ACTION_CANCEL,OBJ_TYPE_FILM_LIST,filmListId, position)
        }
    }
}