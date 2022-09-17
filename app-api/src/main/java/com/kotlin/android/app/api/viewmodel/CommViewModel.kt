package com.kotlin.android.app.api.viewmodel

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.app.api.repository.CommRepository
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.app.data.entity.common.*
import kotlinx.coroutines.launch

/**
 * @author ZhouSuQiang
 * @email zhousuqiang@126.com
 * @date 2020/9/4
 */
open class CommViewModel<E> : BaseViewModel() {
    protected val mCommRepo by lazy { CommRepository() }

    private val mCommWantToSeeUIModel
            = BaseUIModel<CommonResultExtend<WantToSeeResult, E>>()
    val mCommWantToSeeUIState = mCommWantToSeeUIModel.uiState

    private val mCommPraiseUpUIModel = BaseUIModel<CommonResultExtend<CommBizCodeResult, E>>()
    val mCommPraiseUpUIState = mCommPraiseUpUIModel.uiState

    private val mCommPraiseDownUIModel = BaseUIModel<CommonResultExtend<CommBizCodeResult, E>>()
    val mCommPraiseDownUIState = mCommPraiseDownUIModel.uiState

    private val mCommVoteUIModel = BaseUIModel<CommonResultExtend<CommBizCodeResult, E>>()
    val mCommVoteUIState = mCommVoteUIModel.uiState

    private val mCommShareUIModel = BaseUIModel<CommonShare>()
    val mCommShareUIState = mCommShareUIModel.uiState

    // 关注
    private val mFollowUIModel = BaseUIModel<CommonResultExtend<CommBizCodeResult, E>>()
    val followState = mFollowUIModel.uiState

    // 加入家族
    private val mJoinFamilyUIModel = BaseUIModel<CommonResultExtend<CommonResult, E>>()
    val joinFamilyUISate = mJoinFamilyUIModel.uiState

    // 退出家族
    private val mOutFamilyUIModel = BaseUIModel<CommonResultExtend<CommonResult, E>>()
    val outFamilyUISate = mOutFamilyUIModel.uiState

    /**
     * 设置电影为想看/取消想看
     * GET ("/library/movie/setWantToSee.api")
     *
     * movieId	Number  电影Id
     * flag	    Number  操作类型：1想看，2取消想看
     * year	    Number  年代（用于生成XXXX年我想看的第XX部电影）
     * extend   扩展数据，用于UI刷新等
     */
    fun getMovieWantToSee(
            movieId: Long,
            flag: Long,
            year: Long = 0,
            extend: E
    ) {
        viewModelScope.launch(main) {
            mCommWantToSeeUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                mCommRepo.getMovieWantToSee(movieId, flag, year, extend)
            }

            mCommWantToSeeUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 社区交互-点赞api - 点赞
     * POST (/community/praise_up.api)
     *
     * @param action : 动作 1.点赞 2.取消点赞
     * @param objType : 主体类型,具体值可参考{@CommConstant}
     * @param objId : 主体ID
     */
    fun praiseUp(
            action: Long,
            objType: Long,
            objId: Long,
            extend: E
    ) {
        viewModelScope.launch(main) {
            mCommPraiseUpUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                mCommRepo.praiseUp(action, objType, objId, extend)
            }

            mCommPraiseUpUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 社区交互-点赞api - 点踩
     * POST (/community/praise_down.api)
     *
     * @param action : 动作 1.点踩 2.取消点踩
     * @param objType : 主体类型,具体值可参考{@CommConstant}
     * @param objId : 主体ID
     */
    fun praiseDown(
            action: Long,
            objType: Long,
            objId: Long,
            extend: E
    ) {
        viewModelScope.launch(main) {
            mCommPraiseDownUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                mCommRepo.praiseDown(action, objType, objId, extend)
            }

            mCommPraiseDownUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /***
     * 社区交互-投票api - 投票(/vote.api)
     *
     * @param objType Number 投票主体类型 POST(1, "帖子")
     * @param objId	Number 投票主体对象ID
     * @param voteId Number 用户投票的选项ID
     */
    fun communityVote(
            objType: Long,
            objId: Long,
            voteId: Long,
            extend: E
    ) {
        viewModelScope.launch {
            mCommVoteUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                mCommRepo.communityVote(objType, objId, voteId, extend)
            }

            mCommVoteUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 获取分享信息(/utility/share.api)
     *
     * type 1	文章详情页 2	片单详情页 3	视频详情页 4	家族详情页 5	帖子详情页 6	长影评页面 7 短影评页面 8	日志详情页 9	相册详情页 10	卡片大富翁 11	影人详情页 12	影片资料页
     * relateId 分享对象的ID，用于获取该对象的相关内容
     * secondRelateId 分享对象ID2（用于需要多个ID才能获取到分享内容的情况）
     */
    fun getShareInfo(
            type: Long,
            relateId: Long,
            secondRelateId: Long? = null) {
        viewModelScope.launch {
            mCommShareUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                mCommRepo.getShareInfo(type, relateId, secondRelateId)
            }

            mCommShareUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 关注/取消关注用户
     * action 1:关注 2:取消关注
     */
    fun follow(action: Long, userId: Long, extend: E) {
        viewModelScope.launch(main) {
            mFollowUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                mCommRepo.followUser(action, userId, extend)
            }
            mFollowUIModel.checkResultAndEmitUIState(result = result)
        }
    }


    /**
     * 加入家族
     */
    fun joinFamily(id: Long, extend: E) {
        viewModelScope.launch(main) {
            mJoinFamilyUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                mCommRepo.joinFamily(id, extend)
            }
            mJoinFamilyUIModel.checkResultAndEmitUIState(result = result)
        }

    }

    /**
     * 退出家族
     */
    fun outFamily(id: Long, extend: E) {
        viewModelScope.launch(main) {
            mOutFamilyUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                mCommRepo.outFamily(id, extend)
            }
            mOutFamilyUIModel.checkResultAndEmitUIState(result = result)
        }
    }
}