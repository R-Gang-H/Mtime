package com.kotlin.android.home.repository

import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.WantToSeeResult
import com.kotlin.android.app.data.entity.toplist.*

/**
 * @author vivian.wei
 * @date 2020/8/26
 * @desc 榜单请求接口数据类
 */
class TopListRepository : BaseRepository() {

    /**
     * 首页-榜单-电影/电视剧/影人列表 所有数据
     */
    suspend fun getIndexTopList(): ApiResult<IndexAppTopList> {
        return request {
            apiMTime.getIndexTopList()
        }
    }

    /**
     * 榜单分页查询
     *
     * @param type      类型 1电影 2电视剧 3影人
     * @param pageIndex 页码
     * @param pageSize  一页个数
     */
    suspend fun getTopListQuery(
        type: Long,
        pageIndex: Long,
        pageSize: Long
    ): ApiResult<IndexTopListQuery> {
        return request {
            apiMTime.getTopListQuery(type, pageIndex, pageSize)
        }
    }

    /**
     * 电影/影人榜单详情
     *
     * @param id  榜单ID
     */
    suspend fun getTopListDetail(id: Long): ApiResult<TopListInfo> {
        return request {
            apiMTime.getTopListDetail(id)
        }
    }

    /**
     * 首页-榜单-游戏列表 所有数据
     */
    suspend fun getIndexGameTopList(): ApiResult<IndexAppGameTopList> {
        return request {
            apiMTime.getIndexGameTopList()
        }
    }

    /**
     * 游戏榜单详情页 - 排行榜
     * @param rankType 排行榜分类：1昨日道具狂人，2昨日衰人，3昨日交易达人，4昨日收藏大玩家，5金币大富翁，6套装组合狂
     */
    suspend fun getRichmanTopUserList(rankType: Long): ApiResult<GameTopList> {
        return request {
            apiMTime.getRichmanTopUserList(rankType)
        }
    }

    /**
     * 设置电影为想看/取消想看
     *
     * @param movieId    Number  电影Id
     * @param flag        Number  操作类型：1想看，2取消想看
     * @param year        Number  年代（用于生成XXXX年我想看的第XX部电影）
     */
    suspend fun getMovieWantToSee(
        movieId: Long,
        flag: Long,
        year: Long
    ): ApiResult<WantToSeeResult> {
        return request {
            apiMTime.getMovieWantToSee(movieId, flag, year)
        }
    }

}