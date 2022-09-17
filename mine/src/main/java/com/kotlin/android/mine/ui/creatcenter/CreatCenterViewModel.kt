package com.kotlin.android.mine.ui.creatcenter

import com.kotlin.android.api.ApiResult
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.call
import com.kotlin.android.app.data.entity.common.CommonResult
import com.kotlin.android.app.data.entity.community.content.ArticleUser
import com.kotlin.android.app.data.entity.mine.CreatorCenterBean
import com.kotlin.android.app.data.entity.mine.CreatorTaskEntity
import com.kotlin.android.app.data.entity.mine.HelpInfoList
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.mine.repoistory.CreatCenterRepository
import com.kotlin.android.mine.repoistory.CreatorRepository

/**
 *
 * @ProjectName:    b2c
 * @Package:        com.kotlin.android.mine.ui.creatcenter
 * @ClassName:      CreatCenterViewModel
 * @Description:    创作中心ViewModel
 * @Author:         haoruigang
 * @CreateDate:     2022/3/28 10:59
 */
class CreatCenterViewModel : BaseViewModel() {

    private val repo by lazy {
        CreatCenterRepository()
    }

    var creatorCenterBean: CreatorCenterBean? = null  // 创作者中心首页数据
    var levelNum: String = "L1"  // 创作等级

    private val creatorUiModel by lazy { BaseUIModel<CreatorCenterBean>() }
    var creatorState = creatorUiModel.uiState

    fun getCreator() {
        call(creatorUiModel) {
            getCreatorInfo()
        }
    }

    private suspend fun getCreatorInfo(): ApiResult<CreatorCenterBean> {
        return repo.getCreator()
    }

    val helpLeveList by lazy { HelpInfoList() }
    private val helpLeveUIModel by lazy { BaseUIModel<HelpInfoList>() }
    val helpLeveState = helpLeveUIModel.uiState

    fun getHelpInfos() {
        call(helpLeveUIModel) {
            getCreatorHelpLeve()
        }
    }

    private suspend fun getCreatorHelpLeve(): ApiResult<HelpInfoList> {
        return repo.getCreatorHelpLeve()
    }

    private val repoCreatorTask by lazy {
        CreatorRepository()
    }
    private val taskUiModel by lazy { BaseUIModel<CreatorTaskEntity>() }
    var taskState = taskUiModel.uiState

    private val checkUserUIModel by lazy { BaseUIModel<CommonResult>() }
    var checkUserUiState = checkUserUIModel.uiState

    /**
     * 任务中心数据
     */
    fun loadData() {
        call(
            uiModel = taskUiModel
        ) {
            repoCreatorTask.getCreatorTask()
        }
    }

    /**
     * 任务中心 判断用户是否加入家族
     */
    fun checkUserInGroup(groupId: Long) {
        call(
            uiModel = checkUserUIModel
        ) {
            repo.checkUserInGroup(groupId)
        }
    }

    var mCannotPublishAudio: Boolean = false //默认不展示音频
    //查询当前文章用户信息
    private val queryArticleUserUIModel: BaseUIModel<ArticleUser> = BaseUIModel()
    val queryArticleUserState = queryArticleUserUIModel.uiState

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