package com.kotlin.android.app.api.repository

import androidx.collection.arrayMapOf
import com.kotlin.android.api.ApiResult
import com.kotlin.android.app.api.base.BaseRepository
import com.kotlin.android.app.data.entity.common.*
import com.kotlin.android.retrofit.getRequestBody

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/9/3
 */
class CommRepository : BaseRepository() {

    /**
     * 设置电影为想看/取消想看
     * GET ("/library/movie/setWantToSee.api")
     *
     * movieId	Number  电影Id
     * flag	    Number  操作类型：1想看，2取消想看
     * year	    Number  年代（用于生成XXXX年我想看的第XX部电影）
     * extend   扩展数据，用于UI刷新等
     */
    suspend fun <T> getMovieWantToSee(
        movieId: Long,
        flag: Long,
        year: Long = 0,
        extend: T
    ): ApiResult<CommonResultExtend<WantToSeeResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend
                )
            },
            api = {
                apiMTime.getMovieWantToSee(movieId, flag, year)
            })
    }

    /**
     * 社区交互-点赞api - 点赞
     * POST (/community/praise_up.api)
     *
     * @param action : 动作 1.点赞 2.取消点赞
     * @param objType : 主体类型,具体值可参考{@CommConstant}
     * @param objId : 主体ID
     */
    suspend fun <T> praiseUp(
        action: Long,
        objType: Long,
        objId: Long,
        extend: T
    ): ApiResult<CommonResultExtend<CommBizCodeResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend,
                    addedValue = objType
                )
            },
            api = {
                apiMTime.praiseUp(action, objType, objId)
            })
    }

    /**
     * 社区交互-点赞api - 点踩
     * POST (/community/praise_down.api)
     *
     * @param action : 动作 1.点踩 2.取消点踩
     * @param objType : 主体类型,具体值可参考{@CommConstant}
     * @param objId : 主体ID
     */
    suspend fun <T> praiseDown(
        action: Long,
        objType: Long,
        objId: Long,
        extend: T
    ): ApiResult<CommonResultExtend<CommBizCodeResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend,
                    addedValue = objType
                )
            },
            api = {
                apiMTime.praiseDown(action, objType, objId)
            })
    }

    /***
     * 社区交互-投票api - 投票(/vote.api)
     *
     * @param objType Number 投票主体类型 POST(1, "帖子")
     * @param objId    Number 投票主体对象ID
     * @param voteId Number 用户投票的选项ID
     */
    suspend fun <T> communityVote(
        objType: Long,
        objId: Long,
        voteId: Long,
        extend: T
    ): ApiResult<CommonResultExtend<CommBizCodeResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend,
                    addedValue = voteId
                )
            },
            api = {
                val body = getRequestBody(
                    arrayMapOf(
                        "objType" to objType,
                        "objId" to objId,
                        "voteIds" to arrayOf(voteId)
                    )
                )
                apiMTime.communityVote(body)
            })
    }

    /**
     * 获取分享信息(/utility/share.api)
     *
     * type 1	文章详情页 2	片单详情页 3	视频详情页 4	家族详情页 5	帖子详情页 6	长影评页面 7 短影评页面 8	日志详情页 9	相册详情页 10	卡片大富翁 11	影人详情页 12	影片资料页
     * relateId 分享对象的ID，用于获取该对象的相关内容
     * secondRelateId 分享对象ID2（用于需要多个ID才能获取到分享内容的情况）
     */
    suspend fun getShareInfo(
        type: Long,
        relateId: Long,
        secondRelateId: Long? = null
    ): ApiResult<CommonShare> {
        return request { apiMTime.getShareInfo(type, relateId, secondRelateId) }
    }

    /**
     *
     * @param action 动作 1:关注 2:取消关注
     * @param userId 关注用户Id
     */
    suspend fun <T> followUser(
        action: Long,
        userId: Long,
        extend: T
    ): ApiResult<CommonResultExtend<CommBizCodeResult, T>> {
        return request(
            converter = {
                CommonResultExtend(result = it, extend = extend)
            },
            api = {
                apiMTime.followUser(action, userId)
            })
    }

    /**
     * 加入家族
     */
    suspend fun <T> joinFamily(
        id: Long,
        extend: T
    ): ApiResult<CommonResultExtend<CommonResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend
                )
            },
            api = {
                apiMTime.joinGroup(id)
            })
    }

    /**
     * 退出家族
     */
    suspend fun <T> outFamily(id: Long, extend: T): ApiResult<CommonResultExtend<CommonResult, T>> {
        return request(
            converter = {
                CommonResultExtend(
                    result = it,
                    extend = extend
                )
            },
            api = {
                apiMTime.outGroup(id)
            })
    }

}