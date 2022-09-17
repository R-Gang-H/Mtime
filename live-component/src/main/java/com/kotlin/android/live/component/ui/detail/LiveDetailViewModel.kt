package com.kotlin.android.live.component.ui.detail

import android.text.TextUtils
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.hyphenate.chat.EMCmdMessageBody
import com.hyphenate.chat.EMMessage
import com.hyphenate.chat.EMTextMessageBody
import com.kotlin.android.api.base.BaseUIModel
import com.kotlin.android.app.data.entity.common.ShareResultExtend
import com.kotlin.android.app.data.entity.live.*
import com.kotlin.android.core.BaseViewModel
import com.kotlin.android.ktx.ext.log.e
import com.kotlin.android.live.component.constant.*
import com.kotlin.android.live.component.repository.LiveDetailRepository
import com.kotlin.android.live.component.viewbean.LiveDetailExtraBean
import com.kotlin.android.live.component.viewbean.LiveVideoExtraBean
import com.kotlin.android.mtime.ktx.ext.showToast
import com.kotlin.android.user.UserManager
import kotlinx.android.synthetic.main.activity_live_detail.*
import kotlinx.coroutines.launch
import org.json.JSONObject

/**
 * create by lushan on 2021/3/2
 * description:直播viewModel
 */
class LiveDetailViewModel : BaseViewModel() {
    private val mRepo by lazy { LiveDetailRepository() }

    //扩展分享
    private val shareExtendUIModel by lazy { BaseUIModel<ShareResultExtend<Any>>() }
    val shareExtendUIState by lazy { shareExtendUIModel.uiState }

    //直播详情
    private val detailUIModel by lazy { BaseUIModel<LiveDetailExtraBean>() }
    val detailUIState by lazy { detailUIModel.uiState }

    //直播预约
    private val liveAppointUIModel by lazy { BaseUIModel<LiveAppointResult>() }
    val liveAppointUIState by lazy { liveAppointUIModel.uiState }

    //直播机位列表
    private val cameraStandListUIModel by lazy { BaseUIModel<LiveVideoExtraBean>() }
    val cameraStandListState by lazy { cameraStandListUIModel.uiState }

    //直播机位播放地址
    private val cameraPlayUrlUIModel by lazy { BaseUIModel<CameraPlayUrl>() }
    val cameraPlayUrlState by lazy { cameraPlayUrlUIModel.uiState }


    //直播流地址变动切换
    private val _cameraIdOfStreamChangeState = MutableLiveData<Long>()
    val cameraIdOfStreamChangeState: LiveData<Long>
        get() = _cameraIdOfStreamChangeState

    //直播状态变动
    private val _liveStatusState = MutableLiveData<Long>()
    val liveStatusSocketState: LiveData<Long>
        get() = _liveStatusState

    //直播状态下在线观看人数
    private val _liveOnLinePersonNumState = MutableLiveData<Long>()
    val liveOnLinePersonNumState: LiveData<Long>
        get() = _liveOnLinePersonNumState

    //轮询信令
    private val signalPollingUIModel by lazy { BaseUIModel<MutableList<SignalPolling>>() }
    val signalPollingState by lazy { signalPollingUIModel.uiState }

    //导播台
    private val directorUnitsUIModel by lazy { BaseUIModel<DirectorUnits>() }
    val directorUnitsState by lazy { directorUnitsUIModel.uiState }

    private var cmdMap: HashMap<Long, LiveCommand> = hashMapOf()

    /**
     * 获取分享数据
     */
    fun getShareInfo(liveId: Long, extend: Any) {
        viewModelScope.launch(main) {
            shareExtendUIModel.emitUIState(showLoading = true)

            val result = withOnIO {
                mRepo.getShareInfo(liveId, extend)
            }

            shareExtendUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 获取直播详情数据
     */
    fun getLiveDetail(liveId: Long, isFromLogin: Boolean) {
        viewModelScope.launch(main) {
            detailUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                mRepo.getLiveDetail(liveId, isFromLogin)
            }
            detailUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 预约
     */
    fun liveAppoint(liveId: Long) {
        viewModelScope.launch(main) {
            liveAppointUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                mRepo.liveAppoint(liveId)
            }
            liveAppointUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 获取机位直播列表
     * @param addVideoId 刚上架或下架机位id
     * @param isPolling 轮询中获取直播机位列表
     */
    fun getCameraStandList(liveId: Long, isPolling: Boolean = false) {
        viewModelScope.launch(main) {
            cameraStandListUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                mRepo.getCameraStandList(liveId, isPolling)
            }
            cameraStandListUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 获取直播机位播放地址
     */
    fun getCameraPlayUrl(videoId: Long) {
        viewModelScope.launch(main) {
//            cameraPlayUrlUIModel.emitUIState(showLoading = true)
            val result = withOnIO {
                mRepo.getCameraPlayUrl(videoId)
            }
            cameraPlayUrlUIModel.checkResultAndEmitUIState(result = result)
        }
    }

    /**
     * 轮询信令
     */
    fun getSignalPolling(roomNum: String) {
        viewModelScope.launch(main) {
            if (TextUtils.isEmpty(roomNum)) {
                return@launch
            }
            val result = withOnIO {
                mRepo.signalPolling(roomNum)
            }
            signalPollingUIModel.checkResultAndEmitUIState(result)
        }
    }

    fun handleSinglePolling(
        detailBean: LiveDetail?,
        pollingList: MutableList<SignalPolling>,
        liveStatus: Long
    ) {
        val newCmdMap: HashMap<Long, LiveCommand> = hashMapOf()
        pollingList.forEach {
            newCmdMap[it.cmdCode] = LiveCommand(
                it.cmdCode.toInt(),
                it.roomNum.orEmpty(),
                JSONObject(Gson().toJson(it.cmdParams))
            )
        }

        val cmd104 = newCmdMap[SOCKET_CMD_CODE_104]
        if (cmd104 != null && ifHasValidCmdToDo(cmd104)) {
            handleCommand(cmd104, detailBean, liveStatus)
            this.cmdMap.clear()
            this.cmdMap.putAll(newCmdMap)
            return
        }
        val cmd101 = newCmdMap[SOCKET_CMD_CODE_101]
        if (ifHasValidCmdToDo(cmd101)) {
            cmd101?.let { handleCommand(it, detailBean, liveStatus) }
        }
        val cmd102 = newCmdMap[SOCKET_CMD_CODE_102]
        if (ifHasValidCmdToDo(cmd102)) {
            cmd102?.let { handleCommand(it, detailBean, liveStatus) }
        }
        val cmd103 = newCmdMap[SOCKET_CMD_CODE_103]
        if (ifHasValidCmdToDo(cmd103)) {
            cmd103?.let { handleCommand(it, detailBean, liveStatus) }
        }
        val cmd105 = newCmdMap[SOCKET_CMD_CODE_105]
        if (ifHasValidCmdToDo(cmd105)) {
            cmd105?.let { handleCommand(it, detailBean, liveStatus) }
        }
        val cmd106 = newCmdMap[SOCKET_CMD_CODE_106]
        if (ifHasValidCmdToDo(cmd106)) {
            cmd106?.let { handleCommand(it, detailBean, liveStatus) }
        }
        this.cmdMap.clear()
        this.cmdMap.putAll(newCmdMap)
    }

    private fun ifHasValidCmdToDo(cmd: LiveCommand?): Boolean {
        cmd ?: return false
        val cmdParams = cmd.cmdParams
        var newttl: Long = 0L
        if (hasParamsInJson(cmdParams, "ttl")) {
            newttl = cmdParams?.optLong("ttl", 0L) ?: 0L
        }
        //如果是第一次不执行
        val oldCmd = cmdMap[cmd.cmdCode.toLong()] ?: return true

        val oldCmdParams = oldCmd.cmdParams
        var oldttl: Long = 0L
        if (hasParamsInJson(oldCmdParams, "ttl")) {
            oldttl = oldCmdParams?.optLong("ttl", 0L) ?: 0L
        }

        return newttl > oldttl
    }

    private fun getShowMsg(cmdParams: JSONObject?): String {
        var showMsg: String = ""
        if (hasParamsInJson(cmdParams, "showMsg")) {
            showMsg = cmdParams?.optString("showMsg").orEmpty()
        }
        return showMsg
    }

    fun handleCommand(
        cmd: LiveCommand,
        detailBean: LiveDetail?,
        liveStatus: Long,
        isPolling: Boolean = true
    ) {
        synchronized(this::class.java) {
            with(cmd) {
                cmdMap[cmdCode.toLong()] = cmd
                when (cmdCode.toLong()) {
                    SOCKET_CMD_CODE_101 -> {//机位上下架通知
                        if (isNotLiving(liveStatus)) return
                        var videoId = 0L
                        if (hasParamsInJson(cmdParams, "videoId")) {
                            videoId = cmdParams?.optLong("videoId") ?: 0L//上架或下架的机位id
                        }
                        // 请求机位列表，判断当前机位id，如果是改id，需要播放第一个，如果不是只刷新机位列表
                        detailBean ?: return
                        if (videoId == 0L) return
                        viewModelScope.launch(main) {
                            getShowMsg(cmdParams).showToast()
                        }
                        getCameraStandList(detailBean.liveId)
                    }
                    SOCKET_CMD_CODE_102 -> {//流地址切换通知
                        if (isNotLiving(liveStatus)) return
                        var videoId = 0L
                        if (hasParamsInJson(cmdParams, "videoId")) {
                            videoId = cmdParams?.optLong("videoId") ?: 0L//上架或下架的机位id
                        }
                        if (videoId == 0L) return
                        var showMsg = getShowMsg(cmdParams)
                        showMsg.showToast()
                        //如果是当前播放的id，需要重新播放，如果不是当前视频，不做处理
                        _cameraIdOfStreamChangeState.postValue(videoId)
                    }

                    SOCKET_CMD_CODE_103 -> {//流状态变更

                    }
                    SOCKET_CMD_CODE_104 -> {//直播状态变更：1.直播前 2.直播中 3.直播结束 4.直播后
                        var liveStatus = 0L
                        if (hasParamsInJson(cmdParams, "liveStatus")) {
                            liveStatus = cmdParams?.optLong("liveStatus") ?: 0L
                        }
                        if (liveStatus == 0L) return
                        var showMsg = getShowMsg(cmdParams)
                        showMsg.showToast()
                        _liveStatusState.postValue(liveStatus)
                    }
                    SOCKET_CMD_CODE_105 -> {//观看直播人数统计通知
                        if (isNotLiving(liveStatus)) return
                        var personNum: String = ""
                        if (hasParamsInJson(cmdParams, "personNum")) {
                            personNum = cmdParams?.optString("personNum").orEmpty()
                        }
                        if (TextUtils.isEmpty(personNum) || personNum.isDigitsOnly().not()) {
                            return
                        }
                        var personNumLong = personNum.toLong()
                        if (personNumLong < 0L) return
                        _liveOnLinePersonNumState.postValue(personNumLong)
                    }
                    SOCKET_CMD_CODE_106 -> {//机位变更
                        if (isNotLiving(liveStatus)) return
                        var showMsg = getShowMsg(cmdParams)
                        showMsg.showToast()
                        getCameraStandList(detailBean?.liveId ?: 0L)
                    }
                }
            }
        }
    }

    private fun isNotLiving(liveStatus: Long) = liveStatus != LIVE_STATUS_LIVING

    private fun hasParamsInJson(jsonObject: JSONObject?, key: String) = jsonObject?.has(key) == true

    /**
     * 获取导播台信息
     *
     */
    fun getDirectorUnits(liveId: Long) {
        viewModelScope.launch(main) {
            val result = withOnIO {
                mRepo.getDirectorUnits(liveId)
            }

            directorUnitsUIModel.checkResultAndEmitUIState(result)
        }
    }

    /**
     * 更新弹幕
     */
    fun updateDanmu(
        messages: List<EMMessage>?,
        isMine: Boolean,
        updateDanmuAction: (DanmuBean) -> Unit
    ) {
        messages?.let {
            for (message in it) {
                if (message.type == EMMessage.Type.TXT) {
                    updateDanmuAction.invoke(
                        DanmuBean(
                            content = (message.body as EMTextMessageBody).message,
                            time = System.currentTimeMillis(),
                            nickName = message.userName,
                            userId = UserManager.instance.userId,
                            isMine = isMine
                        )
                    )
                }
            }
        }
    }

    fun executeCmdMessage(messages: List<EMMessage>?, detailBean: LiveDetail?, liveStatus: Long) {
        messages?.let {
            for (message in it) {
                try {
                    val jsonString = (message.body as EMCmdMessageBody).action()
                    val liveCmdHuanxin = Gson().fromJson(jsonString, LiveCmdHuanxin::class.java)
                    val liveCommand = liveCmdHuanxin.toLiveCommand()

                    if (ifHasValidCmdToDo(liveCommand)) {
                        handleCommand(liveCommand, detailBean, liveStatus)
                    }
                } catch (e: Exception) {
                    //JsonSyntaxException 解析出非该场景需要的信息 忽略
                    e.e()
                }
            }
        }
    }
}