package com.kotlin.android.message.ui.chat

import androidx.lifecycle.viewModelScope
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.api.base.checkResult
import com.kotlin.android.app.data.entity.message.BlockResult
import com.kotlin.android.app.data.entity.message.BlockStatusResult
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.message.repository.MessageRepository
import kotlinx.coroutines.launch

/**
 * Created by zhaoninglongfei on 2022/4/15
 *
 */
class ChatViewModel : BaseViewModel() {

    private val repo = MessageRepository()

    private val blockStatusUIModel = BaseUIModel<BlockStatusResult>()
    val blockStatusUiState = blockStatusUIModel.uiState

    private val addOrRemoveFromBlacklistUIModel = BaseUIModel<BlockResult>()
    val addOrRemoveFromBlacklistUiState = addOrRemoveFromBlacklistUIModel.uiState

    private var blockStatus: Long? = null

    //查看该user是否在黑名单中
    fun loadBlockStatus(userId: Long?) {
        if (userId == null) {
            return
        }
        viewModelScope.launch(main) {
            blockStatusUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                repo.loadBlockStatus(userId)
            }
            checkResult(result,
                success = {
                    blockStatus = it.bizData?.isBlock
                    blockStatusUIModel.emitUIState(success = it)
                },
                error = { blockStatusUIModel.emitUIState(error = it) },
                netError = { blockStatusUIModel.emitUIState(netError = it) })
        }
    }

    // 拉黑/取消拉黑
    fun addOrRemoveBlacklist(userId: Long, action: Int) {
        viewModelScope.launch(main) {
            addOrRemoveFromBlacklistUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                repo.actionBlock(userId, action)
            }
            checkResult(result,
                success = {
                    changeBlockStatus()
                    addOrRemoveFromBlacklistUIModel.emitUIState(success = it)
                },
                error = { addOrRemoveFromBlacklistUIModel.emitUIState(error = it) },
                netError = { addOrRemoveFromBlacklistUIModel.emitUIState(netError = it) })
        }
    }

    private fun changeBlockStatus() {
        if (blockStatus == 1L) {
            blockStatus = 2L
        } else if (blockStatus == 2L) {
            blockStatus = 1L
        }
    }

    fun getBlockStatus(): Long? {
        return blockStatus
    }
}