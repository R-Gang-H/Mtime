package com.kotlin.android.home.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.CommHasMoreList
import com.kotlin.android.review.component.item.bean.ReviewItem
import com.kotlin.android.widget.adapter.multitype.adapter.binder.MultiTypeBinder

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/8/6
 */
class ReviewRepository : BaseRepository() {
    /**
     * 加载影评数据
     *
     * @param pageIndex 页码
     * @param pageSize 一页个数
     */
    suspend fun loadData(pageIndex: Long, pageSize: Long): ApiResult<CommHasMoreList<MultiTypeBinder<*>>> {
        return request(
            converter = { contentList ->
                val list = mutableListOf<MultiTypeBinder<*>>()
                contentList.items?.run{
                    list.addAll(map {
                        ReviewItem.converter2Binder(it)
                    })
                }
                CommHasMoreList(hasMore = contentList.hasNext, list = list)
            },
            api = {
                apiMTime.getIndexMovieComments(pageIndex, pageSize)
            }
        )
    }

//    suspend fun loadData2(): ApiResult<List<MultiTypeBinder<*>>> {
//        return ApiResult.Success(getReviewDatas())
//    }
//
//    suspend fun loadMoreData(): ApiResult<List<MultiTypeBinder<*>>> {
//        return ApiResult.Success(getReviewDatas())
//    }
//
//    private fun getReviewDatas(): List<MultiTypeBinder<*>> {
//        val arrayList = ArrayList<ReviewBinder>()
//        for (index in 1..10L) {
//            val review = ReviewBinder(ReviewItem(index))
//            arrayList.add(review)
//        }
//        return arrayList
//    }
}