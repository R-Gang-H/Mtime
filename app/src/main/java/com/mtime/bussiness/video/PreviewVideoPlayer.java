package com.mtime.bussiness.video;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.fragment.app.FragmentActivity;
import android.text.TextUtils;

import com.kk.taurus.playerbase.assist.AssistPlay;
import com.kk.taurus.playerbase.assist.OnAssistPlayEventHandler;
import com.kk.taurus.playerbase.entity.DataSource;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.GroupValue;
import com.kk.taurus.playerbase.receiver.IReceiver;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.mtime.base.statistic.StatisticEnum;
import com.mtime.bussiness.video.bean.CategoryVideosBean;
import com.mtime.frame.App;
import com.mtime.player.DataInter;
import com.mtime.player.MTimeDataProvider;
import com.mtime.player.MemoryPlayRecorder;
import com.mtime.player.PlayerHelper;
import com.mtime.player.PlayerLibraryConfig;
import com.mtime.player.ReceiverGroupManager;
import com.mtime.player.bean.MTimeVideoData;
import com.mtime.player.bean.StatisticsInfo;
import com.mtime.player.bean.VideoInfo;
import com.mtime.player.eventproducer.AirPlaneChangeEventProducer;
import com.mtime.player.listener.OnBackRequestListener;
import com.mtime.player.listener.OnRequestInputDialogListener;
import com.mtime.player.receivers.PlayerGestureCover;
import com.mtime.player.receivers.RecommendListCover;
import com.mtime.player.receivers.UserGuideCover;
import com.mtime.player.receivers.VideoDefinitionCover;
import com.mtime.player.splayer.BSPlayer;

/**
 * Created by JiaJunHui on 2018/6/19.
 */
public final class PreviewVideoPlayer extends BSPlayer {

    private static PreviewVideoPlayer i;

    private DataSource mDataSource;

    private FragmentActivity mHostActivity;

    private PreviewVideoPlayer() {
        super(App.get().getApplicationContext());
    }

    @Override
    protected void onInit(Context context) {
        super.onInit(context);
        mRelationAssist.setDataProvider(new MTimeDataProvider());
        mRelationAssist.getSuperContainer().setBackgroundColor(Color.BLACK);
        //add air plane setting change event producer
        mRelationAssist.getSuperContainer().addEventProducer(new AirPlaneChangeEventProducer(mAppContext));
        //set event handler
        mRelationAssist.setEventAssistHandler(new OnAssistPlayEventHandler() {
            @Override
            public void onAssistHandle(AssistPlay assistPlay, int eventCode, Bundle bundle) {
                super.onAssistHandle(assistPlay, eventCode, bundle);

            }

            @Override
            public void requestRetry(AssistPlay assistPlay, Bundle bundle) {
                if (PlayerHelper.isTopActivity(mHostActivity)) {
                    super.requestRetry(assistPlay, bundle);
                }
            }
        });
    }

    public static PreviewVideoPlayer get() {
        if (null == i) {
            synchronized (PreviewVideoPlayer.class) {
                if (null == i) {
                    i = new PreviewVideoPlayer();
                }
            }
        }
        return i;
    }

    private StatisticsInfo mStatisticsInfo;

    private OnNextPlayListener onNextPlayListener;
    private OnRequestInputDialogListener onRequestInputDialogListener;
    private OnBackRequestListener onBackRequestListener;

    private boolean needRecommendList;

    private String mPageRefer;

    public int getCurrentVid() {
        GroupValue groupValue = getGroupValue();
        if (groupValue != null) {
            String vid = groupValue.getString(DataInter.Key.KEY_CURRENT_VIDEO_ID);
            return parseInt(vid);
        }
        return -1;
    }

    public void setStatisticsInfo(StatisticsInfo statisticsInfo) {
        this.mStatisticsInfo = statisticsInfo;
        updateStatisticsInfo();
    }

    public void updateAutoPlayFlag(boolean auto) {
        String flag = auto ? StatisticEnum.EnumVAuto.AUTO.getValue() : StatisticEnum.EnumVAuto.MANUAL.getValue();
        updateGroupValue(DataInter.Key.KEY_AUTO_PLAY_FLAG, flag);
    }

    private void updateStatisticsInfo() {
        if (mStatisticsInfo != null) {
            updateGroupValue(DataInter.Key.KEY_STATISTICS_INFO, mStatisticsInfo);
        }
    }

    public void attachActivity(FragmentActivity activity, String pageRefer) {
        mHostActivity = activity;
        mPageRefer = pageRefer;
    }

    public void setOnRequestInputDialogListener(OnRequestInputDialogListener onRequestInputDialogListener) {
        this.onRequestInputDialogListener = onRequestInputDialogListener;
    }

    public void setOnBackRequestListener(OnBackRequestListener onBackRequestListener) {
        this.onBackRequestListener = onBackRequestListener;
    }

    @Override
    protected void onCallBackPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                if (CategoryDataManager.get().isListComplete()) {
                    updateGroupValue(DataInter.Key.KEY_LIST_COMPLETE, true);
                } else {
                    updateAutoPlayFlag(true);
                    postNext();
                }
                break;
        }
    }

    private void postNext() {
        new Handler(Looper.getMainLooper()).post(() -> playNext());
    }

    public void setOnNextPlayListener(OnNextPlayListener onNextPlayListener) {
        this.onNextPlayListener = onNextPlayListener;
    }

    private void playNext() {
        CategoryDataManager.get().next(new CategoryDataManager.OnNextVideoListener() {
            @Override
            public void onNextReady(CategoryVideosBean.RecommendVideoItem item, int type, int index) {
                if (item != null) {
                    if (onNextPlayListener != null) {
                        onNextPlayListener.onNextPlay(item, type, index);
                    }
                    MTimeVideoData data = new MTimeVideoData(String.valueOf(item.getvId()), item.getVideoSource());
                    play(data, true);
                }
            }

            @Override
            public void onFailure() {
                //TODO load next video error, maybe do something.
            }
        });
    }

    public void sendReceiverEvent(int eventCode, Bundle bundle, IReceiverGroup.OnReceiverFilter onReceiverFilter) {
        IReceiverGroup receiverGroup = getReceiverGroup();
        if (receiverGroup != null) {
            receiverGroup.forEach(onReceiverFilter, new IReceiverGroup.OnLoopListener() {
                @Override
                public void onEach(IReceiver iReceiver) {
                    iReceiver.onReceiverEvent(eventCode, bundle);
                }
            });
        }
    }

    public void sendReceiverPrivateEvent(String receiverKey, int eventCode, Bundle bundle) {
        IReceiverGroup receiverGroup = getReceiverGroup();
        if (receiverGroup != null) {
            IReceiver receiver = receiverGroup.getReceiver(receiverKey);
            if (receiver != null)
                receiver.onPrivateEvent(eventCode, bundle);
        }
    }

    @Override
    protected void onCallBackErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    protected void onCallBackReceiverEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case DataInter.ReceiverEvent.EVENT_REQUEST_BACK:
                if (onBackRequestListener != null) {
                    onBackRequestListener.onBack();
                }
                break;
            case DataInter.ReceiverEvent.EVENT_REQUEST_TOGGLE_SCREEN_STATE:
                mHostActivity.setRequestedOrientation(isLandScape() ?
                        ActivityInfo.SCREEN_ORIENTATION_PORTRAIT :
                        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case DataInter.ReceiverEvent.EVENT_REQUEST_NEXT_VIDEO:
                updateAutoPlayFlag(false);
                postNext();
                break;
            case DataInter.ReceiverEvent.EVENT_REQUEST_EDIT_DANMU:
                if (onRequestInputDialogListener != null && isLandScape()) {
                    onRequestInputDialogListener.onRequest();
                }
                break;
        }
    }

    public void setNeedRecommendList(boolean needRecommendList) {
        this.needRecommendList = needRecommendList;
    }

    public boolean isLandScape() {
        if (mHostActivity != null) {
            return mHostActivity.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        }
        return false;
    }

    public void setReceiverGroupFullScreenState(Context context, RecommendListCover recommendListCover) {
        ConfigParams params = new ConfigParams();
        params.recommendListCover = recommendListCover;
        setReceiverGroupConfigState(context, RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE, params);
    }

    public void setReceiverGroupConfigState(Context context, int configState) {
        setReceiverGroupConfigState(context, configState, null);
    }

    public void setReceiverGroupConfigState(Context context, int configState, ConfigParams configParams) {
        IReceiverGroup receiverGroup = getReceiverGroup();
        if (receiverGroup == null) {
            receiverGroup = ReceiverGroupManager.getStandardReceiverGroup(context);
            setReceiverGroup(receiverGroup);
            updateStatisticsInfo();
        }
        updateGroupValue(DataInter.Key.KEY_STATISTICS_PAGE_REFER, mPageRefer == null ? "" : mPageRefer);
        switch (configState) {
            case RECEIVER_GROUP_CONFIG_LIST_STATE:
                //remove some receivers
                receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER);
                receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
                receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_RECOMMEND_LIST_COVER);
                receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER);
                //config params
                updateGroupValue(DataInter.Key.KEY_DANMU_SHOW_STATE, false);
                updateGroupValue(DataInter.Key.KEY_NEED_VIDEO_DEFINITION, false);
                updateGroupValue(DataInter.Key.KEY_NEED_RECOMMEND_LIST, false);
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, false);
                updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, false);
                updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, false);
                updateGroupValue(DataInter.Key.KEY_DANMU_EDIT_ENABLE, false);
                updateGroupValue(DataInter.Key.KEY_NEED_ERROR_TITLE, false);
                break;
            case RECEIVER_GROUP_CONFIG_DETAIL_PORTRAIT_STATE:
                //remove some receivers
                receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
                receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER);
                receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER);
                receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_RECOMMEND_LIST_COVER);
                //need gesture cover
                receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, new PlayerGestureCover(context));
                //config params
                updateGroupValue(DataInter.Key.KEY_DANMU_SHOW_STATE, true);
                updateGroupValue(DataInter.Key.KEY_DANMU_EDIT_ENABLE, false);
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_DANMU_SWITCH_ENABLE, true);
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE, false);
                updateGroupValue(DataInter.Key.KEY_NEED_VIDEO_DEFINITION, false);
                updateGroupValue(DataInter.Key.KEY_NEED_RECOMMEND_LIST, false);
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
                updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, false);
                updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, false);
                updateGroupValue(DataInter.Key.KEY_NEED_ERROR_TITLE, true);
                break;
            case RECEIVER_GROUP_CONFIG_FULL_SCREEN_STATE:
                //remove some receivers
                receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER);
                //need gesture cover
                receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_GESTURE_COVER, new PlayerGestureCover(context));
                //need definition cover
                IReceiver fDefinitionReceiver = receiverGroup.getReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER);
                if (fDefinitionReceiver == null) {
                    receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_DEFINITION_COVER, new VideoDefinitionCover(context));
                }
                //need recommend cover
                if (needRecommendList && configParams != null && configParams.recommendListCover != null) {
                    receiverGroup.removeReceiver(DataInter.ReceiverKey.KEY_RECOMMEND_LIST_COVER);
                    receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_RECOMMEND_LIST_COVER, configParams.recommendListCover);
                }
                //need user guide cover
                if (ReceiverGroupManager.isNeedUserGuideCover(context)) {
                    IReceiver fUserGuideReceiver = receiverGroup.getReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER);
                    if (fUserGuideReceiver == null) {
                        receiverGroup.addReceiver(DataInter.ReceiverKey.KEY_USER_GUIDE_COVER, new UserGuideCover(context));
                    }
                }
                //config params
                updateGroupValue(DataInter.Key.KEY_DANMU_SHOW_STATE, true);
                updateGroupValue(DataInter.Key.KEY_DANMU_EDIT_ENABLE, true);
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_DANMU_SWITCH_ENABLE, true);
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_SHARE_ENABLE, true);
                updateGroupValue(DataInter.Key.KEY_NEED_VIDEO_DEFINITION, true);
                updateGroupValue(DataInter.Key.KEY_NEED_RECOMMEND_LIST, needRecommendList);
                updateGroupValue(DataInter.Key.KEY_CONTROLLER_TOP_ENABLE, true);
                updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, needRecommendList);
                updateGroupValue(DataInter.Key.KEY_IS_FULL_SCREEN, true);
                updateGroupValue(DataInter.Key.KEY_NEED_ERROR_TITLE, true);
                break;
        }
    }

    public DataSource getDataSource() {
        return mDataSource;
    }

    public boolean isEqualDataSource(String vid, String videoSource) {
        if (TextUtils.isEmpty(vid) || TextUtils.isEmpty(videoSource))
            return false;
        if (mDataSource != null && mDataSource instanceof MTimeVideoData) {
            MTimeVideoData data = ((MTimeVideoData) mDataSource);
            return vid.equals(data.getVideoId()) && videoSource.equals(String.valueOf(data.getSource()));
        }
        return false;
    }

    private void updateVideoInfo(DataSource dataSource) {
        GroupValue groupValue = getGroupValue();
        if (groupValue != null && dataSource != null && dataSource instanceof MTimeVideoData) {
            MTimeVideoData data = (MTimeVideoData) dataSource;
            VideoInfo info = new VideoInfo(data.getVideoId(), String.valueOf(data.getSource()));
            groupValue.putObject(DataInter.Key.KEY_VIDEO_INFO, info);
        }
    }

    public void updateLandScapeDanmuEditState(boolean edit) {
        updateGroupValue(DataInter.Key.KEY_DANMU_COVER_IN_LANDSCAPE_EDIT_STATE, edit);
    }

    private int parseInt(String id) {
        try {
            if (!TextUtils.isEmpty(id))
                return Integer.parseInt(id);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean isEqualData(String vid) {
        int currentVid = getCurrentVid();
        return String.valueOf(currentVid).equals(vid);
    }

    protected void onSetDataSource(DataSource dataSource) {
        if (dataSource != null) {
            this.mDataSource = dataSource;
        }
        if (dataSource != null) {
            updateVideoInfo(dataSource);
            if (dataSource instanceof MTimeVideoData) {
                dataSource.setStartPos(MemoryPlayRecorder.getRecordPlayTime(Integer.parseInt(((MTimeVideoData) dataSource).getVideoId())));
            }
        }
        //check list play soon complete
        if (PlayerLibraryConfig.isListMode) {
            boolean soonComplete = CategoryDataManager.get().isListComplete();
            updateGroupValue(DataInter.Key.KEY_NEED_PLAY_NEXT, !soonComplete);
        }
    }

    public void logicResourceResume(DataSource dataSource) {
        if (isInPlaybackState()) {
            logicResume();
        } else if (dataSource != null && dataSource instanceof MTimeVideoData) {
            MTimeVideoData data = (MTimeVideoData) dataSource;
            int vid = parseInt(data.getVideoId());
            dataSource.setStartPos(MemoryPlayRecorder.getRecordPlayTime(vid));
            play(dataSource, true);
        }
    }

    public void logicPause() {
        GroupValue groupValue = getGroupValue();
        boolean needHandlePause = false;
        if (groupValue != null) {
            boolean isErrorState = groupValue.getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE);
            boolean isAllComplete = groupValue.getBoolean(DataInter.Key.KEY_LIST_COMPLETE);
            if (!isErrorState && !isAllComplete) {
                needHandlePause = true;
            }
        } else {
            needHandlePause = true;
        }
        if (needHandlePause) {
            if (isInPlaybackState()) {
                pause();
            } else {
                stop();
                reset();
            }
        }
    }

    public void logicResume() {
        GroupValue groupValue = getGroupValue();
        if (groupValue != null) {
            boolean isErrorState = groupValue.getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE);
            boolean isAllComplete = groupValue.getBoolean(DataInter.Key.KEY_LIST_COMPLETE);
            boolean isUserPause = groupValue.getBoolean(DataInter.Key.KEY_IS_USER_PAUSE);
            if (!isErrorState && !isAllComplete && !isUserPause) {
                if (isInPlaybackState()) {
                    resume();
                } else {
                    rePlay(MemoryPlayRecorder.getRecordPlayTime(getCurrentVid()));
                }
            }
        }
    }

    @Override
    public void stop() {
        //for pause event to record position
        pause();
        super.stop();
    }

    @Override
    public void destroy() {
        super.destroy();
        PlayerLibraryConfig.isListMode = false;
        mHostActivity = null;
        i = null;
    }

    public static class ConfigParams {
        public RecommendListCover recommendListCover;
    }

    public interface OnNextPlayListener {
        void onNextPlay(CategoryVideosBean.RecommendVideoItem item, int type, int index);
    }

}
