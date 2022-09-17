package com.mtime.player.receivers;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnErrorEventListener;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseReceiver;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.mtime.base.statistic.StatisticConstant;
import com.mtime.base.statistic.StatisticEnum;
import com.mtime.base.statistic.bean.StatisticPageBean;
import com.mtime.bussiness.video.SourceTypeTrans;
import com.mtime.common.utils.Utils;
import com.mtime.constant.FrameConstant;
import com.mtime.player.DataInter;
import com.mtime.player.bean.StatisticsInfo;
import com.mtime.statistic.large.StatisticManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by mtime on 2017/10/18.
 * 播放器 埋点4.0数据上报
 */

public class LogReceiver extends BaseReceiver {

    /** 上次路径来源 */
    private String mRefer;
    /** 播放器所在的页面名称 */
    private String mPageName;
    /** 当前播放的视频ID */
    private String mVID;
    /** 当前播放的视频类型 */
    private String mVType;
    /** 视频画质:high, standard, super；参考枚举值定义 */
    private String mVQuality;
    /** 自动轮播标识: 0:手动播放，1:自动播放 (默认0) ; 参考枚举值定义*/
    private String mVAuto = StatisticEnum.EnumVAuto.MANUAL.getValue();
    /** 视频播放session唯一标识一次视频播放,md5(毫秒时间戳+UUID)，32位大写MD5值 */
    private String mVSID;
    /** 全屏or半屏 0: 半屏，1: 全屏（默认0）; 参考枚举值定义*/
    private String mVScreen = StatisticEnum.EnumVScreen.HALF.getValue();
    /** 拖动开始时的视频时间 */
    private String mSeekStartCurPos;
    /** 是否改变当前的sessionID */
    private boolean mIsChangeSID = true;

    private boolean mIsPauseHeartbeat;
    private Timer mHeartbeatTimer;
    private TimerTask mHeartbeatTimerTask;
    private final Map<String, String> mTempParam = new HashMap<>();
    private boolean onNextClick;

    public LogReceiver(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        getGroupValue().registerOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    private final IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
        @Override
        public String[] filterKeys() {
            return new String[]{
                    DataInter.Key.KEY_STATISTICS_INFO,
                    DataInter.Key.KEY_IS_FULL_SCREEN,
                    DataInter.Key.KEY_AUTO_PLAY_FLAG
            };
        }

        @Override
        public void onValueUpdate(String s, Object o) {
            //获取数据上报的基础数据
            if(DataInter.Key.KEY_STATISTICS_INFO.equals(s)){
                StatisticsInfo info = (StatisticsInfo) o;
                mVID = info.getVid();
                mVType = SourceTypeTrans.getStatisticsVType(info.getSourceType());
                mRefer = info.getPageRefer();
                mPageName = info.getPageLabel();

                if(onNextClick){
                    //下一个视频(用户手动点击下一个的时候上报，或者自动播放下一个时上报)
                    submitNextVideo(mVID, mVType);
                    onNextClick = false;
                }else{
                    //开始初始化播放器时上报
                    mTempParam.clear();
                    mTempParam.put(StatisticConstant.V_ID, mVID);
                    mTempParam.put(StatisticConstant.V_TYPE, mVType);
                    StatisticManager.getInstance().submit(
                            assemble("vodPlayer", null, "init", null, null, null, mTempParam));
                }

            }else if(DataInter.Key.KEY_IS_FULL_SCREEN.equals(s)){
                boolean isFullScreen = (boolean) o;
                mVScreen = isFullScreen?StatisticEnum.EnumVScreen.FULL.getValue():StatisticEnum.EnumVScreen.HALF.getValue();
                //切换窗口
                submitBaseAllParam("changeWindow");
            }else if(DataInter.Key.KEY_DANMU_OPEN_STATE.equals(s)){
                boolean state = (boolean) o;
                //弹幕开或关
                submitBaseAllParam("danmakuSwitch", state?"open":"close");
            }else if(DataInter.Key.KEY_AUTO_PLAY_FLAG.equals(s)){
                //获取自动、手动播放标识
                mVAuto = (String) o;
            }
        }
    };

    /**
     * 组装统计类
     *
     * @param firstRegion
     * @param firstRegionMark
     * @param secRegion
     * @param secRegionMark
     * @param thrRegion
     * @param thrRegionMark
     * @param businessParam
     * @return
     */
    private StatisticPageBean assemble(String firstRegion, String firstRegionMark, String secRegion,
                                       String secRegionMark, String thrRegion, String thrRegionMark, Map<String, String> businessParam) {
        StatisticPageBean bean = new StatisticPageBean();
        bean.refer = mRefer;
        bean.pageName = mPageName;
        bean.path = new HashMap<>();
        if (!TextUtils.isEmpty(firstRegion)) {
            bean.path.put(StatisticConstant.AREA1, firstRegion);
        }
        if (!TextUtils.isEmpty(firstRegionMark)) {
            bean.path.put(StatisticConstant.NUM1, firstRegionMark);
        }
        if (!TextUtils.isEmpty(secRegion)) {
            bean.path.put(StatisticConstant.AREA2, secRegion);
        }
        if (!TextUtils.isEmpty(secRegionMark)) {
            bean.path.put(StatisticConstant.NUM2, secRegionMark);
        }
        if (!TextUtils.isEmpty(thrRegion)) {
            bean.path.put(StatisticConstant.AREA3, thrRegion);
        }
        if (!TextUtils.isEmpty(thrRegionMark)) {
            bean.path.put(StatisticConstant.NUM3, thrRegionMark);
        }
        bean.businessParam = businessParam;
        return bean;
    }

    private void submitBaseAllParam(String secRegion) {
        submitBaseAllParam(secRegion, null);
    }

    //TODO
    private int getDuration(){
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        return playerStateGetter!=null?playerStateGetter.getDuration():0;
    }

    //TODO
    private int getCurrentPosition(){
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        return playerStateGetter!=null?playerStateGetter.getCurrentPosition():0;
    }

    private String getRateValue(){
        return mVQuality!=null?mVQuality:"";
    }

    private boolean isPlayingIlLegal(){
        return getDuration() <= 0 || getCurrentPosition() > getDuration();
    }

    //上报
    private void submitBaseAllParam(String secRegion, String thrRegion) {
        if(isPlayingIlLegal())
            return;
        mTempParam.clear();
        mTempParam.put(StatisticConstant.V_ID, mVID);
        mTempParam.put(StatisticConstant.V_TYPE, mVType);
        mTempParam.put(StatisticConstant.V_AUTO, mVAuto);
        mTempParam.put(StatisticConstant.V_SID, mVSID);
        mTempParam.put(StatisticConstant.V_SCREEN, mVScreen);
        mTempParam.put(StatisticConstant.V_CUR_POS, String.valueOf(getCurrentPosition() / 1000));
        mTempParam.put(StatisticConstant.V_QUALITY, mVQuality); //视频画质：high, standard, super；参考枚举值定义
        StatisticManager.getInstance().submit(
                assemble("vodPlayer", null, secRegion, null, thrRegion, null, mTempParam));
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                if(bundle!=null){
                    DataSource dataSource = (DataSource) bundle.getSerializable(EventKey.SERIALIZABLE_DATA);
                    if(dataSource!=null){
                        mVQuality = dataSource.getTag();
                    }
                }
                break;
            case DataInter.ProviderEvent.EVENT_VIDEO_RATE_DATA:
                //播控调度成功上报
                mVQuality = getRateValue();
                mTempParam.clear();
                mTempParam.put(StatisticConstant.V_ID, mVID);
                mTempParam.put(StatisticConstant.V_TYPE, mVType);
                mTempParam.put(StatisticConstant.V_AUTO, mVAuto);
                mTempParam.put(StatisticConstant.V_QUALITY, mVQuality); //视频画质：high, standard, super；参考枚举值定义
                StatisticManager.getInstance().submit(
                        assemble("vodPlayer", null, "control", null, null, null, mTempParam));
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_PREPARED:
                //生成sessionID
                if(TextUtils.isEmpty(mVSID) || mIsChangeSID) {
                    mVSID = Utils.getMd5(System.currentTimeMillis() + FrameConstant.deviceToken).toUpperCase();
                }
                mIsChangeSID = true;
                //加载成功时上报
                mTempParam.clear();
                mTempParam.put(StatisticConstant.V_ID, mVID);
                mTempParam.put(StatisticConstant.V_TYPE, mVType);
                mTempParam.put(StatisticConstant.V_AUTO, mVAuto);
                mTempParam.put(StatisticConstant.V_SID, mVSID);
                mTempParam.put(StatisticConstant.V_SCREEN, mVScreen);
                mTempParam.put(StatisticConstant.V_QUALITY, mVQuality); //视频画质：high, standard, super；参考枚举值定义
                StatisticManager.getInstance().submit(
                        assemble("vodPlayer", null, "load", null, null, null, mTempParam));
                resumeHeartbeat();
                //启动心跳上报
                startHeartbeat();
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                //起播成功时上报
                submitBaseAllParam("start");
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START:
                //开始卡顿时上报
                submitBaseAllParam("bufferStart");
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END:
                //卡顿结束
                submitBaseAllParam("bufferEnd");
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE:
                pauseHeartbeat();
                //用户手动暂停&自动暂停
                submitBaseAllParam("pause");
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_RESUME:
                resumeHeartbeat();
                //用户手动继续（即使缓存不足以继续播放）&自动继续
                submitBaseAllParam("continue");
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                //视频正常播放到最后结束
                submitBaseAllParam("end");
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_DESTROY:
                //停止心跳上报
                stopHeartbeat();
                if(getDuration() != getCurrentPosition()) {
                    //用户离开页面等导致播放器停止播放了
                    submitBaseAllParam("end");
                }
                break;
        }
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        //停止心跳上报
        stopHeartbeat();
        if(getDuration() != getCurrentPosition()) {
            //用户离开页面等导致播放器停止播放了
            submitBaseAllParam("end");
        }
        getGroupValue().unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    @Override
    public Bundle onPrivateEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case DataInter.ReceiverEvent.EVENT_CODE_SEEK_START_TRACING_TOUCH:
                mSeekStartCurPos = String.valueOf(getCurrentPosition() / 1000);
                break;
            case DataInter.ReceiverEvent.EVENT_CODE_SEEK_END_TRACING_TOUCH:
                submitSeek();
                break;
        }
        return super.onPrivateEvent(eventCode, bundle);
    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case DataInter.ReceiverEvent.EVENT_CODE_DEFINITION_CHANGE:
                //用户手动切换画质，vQuality上报为切换至的新画质参数
                if(null != bundle) {
                    mVQuality = bundle.getString(EventKey.STRING_DATA);
                    //切换后vSID不变，但需要重新上报control、load、start等（如存在）
                    mIsChangeSID = false;
                    submitBaseAllParam("changeQuality");
                }
                break;

            case DataInter.ReceiverEvent.EVENT_DANMU_EDIT_TEXT_CHANGE:
                //TODO 弹幕输入
                submitBaseAllParam("danmakuFocus");
                break;

            case DataInter.ReceiverEvent.EVENT_DANMU_COVER_SEND_DANMU:
                //弹幕发送
                submitBaseAllParam("danmakuSend");
                break;

            case DataInter.ReceiverEvent.EVENT_CODE_ON_PAUSE_AD_SHOW:
                String adId = null;
                int position = -1; //标号是当前视频加载的暂停广告序列中标号(从1开始)
                if(null != bundle) {
                    adId = String.valueOf(bundle.getInt(EventKey.INT_ARG1));
                    position = bundle.getInt(EventKey.INT_ARG2, -1);
                }
                submitAd("show", adId, position);
                break;

            case DataInter.ReceiverEvent.EVENT_CODE_ON_PAUSE_AD_CLICK:
                String adId1 = null;
                int position1 = -1; //标号是当前视频加载的暂停广告序列中标号(从1开始)
                if(null != bundle) {
                    adId1 = String.valueOf(bundle.getInt(EventKey.INT_ARG1));
                    position1 = bundle.getInt(EventKey.INT_ARG2, -1);
                }
                submitAd("click", adId1, position1);
                break;

            case DataInter.ReceiverEvent.EVENT_REQUEST_NEXT_VIDEO:
                onNextClick = true;
                break;
        }
    }

    //上报下一个视频
    private void submitNextVideo(String targetVideoID, String targetVideoType) {
        if(TextUtils.isEmpty(targetVideoID) || TextUtils.isEmpty(targetVideoType))
            return;
        if(isPlayingIlLegal())
            return;
        mTempParam.clear();
        mTempParam.put(StatisticConstant.V_ID, mVID);
        mTempParam.put(StatisticConstant.V_TYPE, mVType);
        mTempParam.put(StatisticConstant.V_AUTO, mVAuto);
        mTempParam.put(StatisticConstant.V_SID, mVSID);
        mTempParam.put(StatisticConstant.V_SCREEN, mVScreen);
        mTempParam.put(StatisticConstant.V_CUR_POS, String.valueOf(getCurrentPosition() / 1000));
        mTempParam.put(StatisticConstant.V_QUALITY, mVQuality); //视频画质：high, standard, super；参考枚举值定义
        mTempParam.put(StatisticConstant.TARGET_VIDEO_ID, targetVideoID);
        mTempParam.put(StatisticConstant.TARGET_VIDEO_TYPE, targetVideoType);
        StatisticManager.getInstance().submit(
                assemble("vodPlayer", null, "nextVideo", null, null, null, mTempParam));
    }

    //上报广告
    private void submitAd(String thrRegion, String aID, int position) {
        if(TextUtils.isEmpty(aID) || position < 1)
            return;
        if(isPlayingIlLegal())
            return;
        mTempParam.clear();
        mTempParam.put(StatisticConstant.V_ID, mVID);
        mTempParam.put(StatisticConstant.V_TYPE, mVType);
        mTempParam.put(StatisticConstant.V_AUTO, mVAuto);
        mTempParam.put(StatisticConstant.V_SID, mVSID);
        mTempParam.put(StatisticConstant.V_SCREEN, mVScreen);
        mTempParam.put(StatisticConstant.V_CUR_POS, String.valueOf(getCurrentPosition() / 1000));
        mTempParam.put(StatisticConstant.V_QUALITY, mVQuality); //视频画质：high, standard, super；参考枚举值定义
        mTempParam.put(StatisticConstant.A_ID, aID);
        StatisticManager.getInstance().submit(
                assemble("vodPlayer", null, "pauseAd", null, thrRegion, String.valueOf(position), mTempParam));
    }

    //播放器发生错误时上报，用errorMsg带上错误信息
    private void submitError(String errorMsg) {
        if(isPlayingIlLegal())
            return;
        mTempParam.clear();
        mTempParam.put(StatisticConstant.V_ID, mVID);
        mTempParam.put(StatisticConstant.V_TYPE, mVType);
        mTempParam.put(StatisticConstant.V_AUTO, mVAuto);
        mTempParam.put(StatisticConstant.V_SID, mVSID);
        mTempParam.put(StatisticConstant.V_SCREEN, mVScreen);
        mTempParam.put(StatisticConstant.V_CUR_POS, String.valueOf(getCurrentPosition() / 1000));
        mTempParam.put(StatisticConstant.V_ERROR_MSG, errorMsg);
        mTempParam.put(StatisticConstant.V_QUALITY, mVQuality); //视频画质：high, standard, super；参考枚举值定义
        StatisticManager.getInstance().submit(
                assemble("vodPlayer", null, "error", null, null, null, mTempParam));
    }

    //拖动完成（松手）后上报
    //vCurPos上报拖动开始时的视频时间，vSeekPos上报拖动完成（松手）时的视频时间
    private void submitSeek() {
        if(isPlayingIlLegal())
            return;
        mTempParam.clear();
        mTempParam.put(StatisticConstant.V_ID, mVID);
        mTempParam.put(StatisticConstant.V_TYPE, mVType);
        mTempParam.put(StatisticConstant.V_AUTO, mVAuto);
        mTempParam.put(StatisticConstant.V_SID, mVSID);
        mTempParam.put(StatisticConstant.V_SCREEN, mVScreen);
        mTempParam.put(StatisticConstant.V_CUR_POS, mSeekStartCurPos);
        mTempParam.put(StatisticConstant.V_SEEK_POS, String.valueOf(getCurrentPosition() / 1000));
        mTempParam.put(StatisticConstant.V_QUALITY, mVQuality); //视频画质：high, standard, super；参考枚举值定义
        StatisticManager.getInstance().submit(
                assemble("vodPlayer", null, "seek", null, null, null, mTempParam));
    }
    
    
    // pause 心跳
    private synchronized void pauseHeartbeat(){
        mIsPauseHeartbeat = true;
    }
    
    // resume 心跳
    private synchronized void resumeHeartbeat() {
        mIsPauseHeartbeat = false;
    }
    
    /**
     * start 心跳
     * 在播放器处于前台激活状态时（包括起播加载过程、用户主动按暂停、视频卡顿等情况）
     * 每5秒报一次。第一次是从load成功开始。
     */
    private synchronized void startHeartbeat() {
        if (mHeartbeatTimerTask == null && mHeartbeatTimer == null) {
            mHeartbeatTimerTask = new TimerTask() {
                @Override
                public void run() {
                    //心跳上报
                    if(!mIsPauseHeartbeat)
                        submitBaseAllParam("heartbeat");
                }
            };
            mHeartbeatTimer = new Timer();
            mHeartbeatTimer.schedule(mHeartbeatTimerTask, 5000, 5000);
        }
    }
    
    /**
     * stop 心跳
     */
    private synchronized void stopHeartbeat() {
        try {
            if(null != mHeartbeatTimer) {
                mHeartbeatTimer.cancel();
                mHeartbeatTimer = null;
            }
            if (mHeartbeatTimerTask != null) {
                mHeartbeatTimerTask.cancel();
                mHeartbeatTimerTask = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnErrorEventListener.ERROR_EVENT_COMMON:
                String errorMsg = "未知错误";
                if(null != bundle) {
                    int errorCode = bundle.getInt(EventKey.INT_DATA);
                    errorMsg = "errorCode:" + errorCode;
                }
                submitError(errorMsg);
                break;

            case OnErrorEventListener.ERROR_EVENT_IO:
                submitError("网络连接异常");
                break;
        }
        pauseHeartbeat();
    }

}
