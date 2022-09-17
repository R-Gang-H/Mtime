package com.mtime.player.receivers;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;

import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseReceiver;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.kotlin.android.app.data.entity.video.VideoPlayList;
import com.mtime.player.DataInter;
import com.mtime.player.MemoryPlayRecorder;
import com.mtime.player.PlayerLibraryConfig;
import com.mtime.player.bean.MTimeVideoData;
import com.mtime.player.bean.VideoInfo;

/**
 * Created by JiaJunHui on 2018/6/28.
 */
public class DataReceiver extends BaseReceiver {

    private int mCurrVid;

    public DataReceiver(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();

    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode) {
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START:
                getGroupValue().putBoolean(DataInter.Key.KEY_LIST_COMPLETE, false);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PAUSE:
                recordPlayPosition();
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_PLAY_COMPLETE:
                recordPlayPosition(0);
                if (!PlayerLibraryConfig.isListMode) {
                    getGroupValue().putBoolean(DataInter.Key.KEY_LIST_COMPLETE, true);
                }
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                if (bundle != null) {
                    MTimeVideoData data = (MTimeVideoData) bundle.getSerializable(EventKey.SERIALIZABLE_DATA);
                    if (data != null) {
                        mCurrVid = parseInt(data.getVideoId());
                        VideoInfo info = new VideoInfo(data.getVideoId(), String.valueOf(data.getSource()));
                        // 广告（没有迁移接口，先注释）
//                        AdHelper.updateAdList(data.getVideoId(), data.getSource());
                        getGroupValue().putString(DataInter.Key.KEY_CURRENT_VIDEO_ID, String.valueOf(mCurrVid));
                        getGroupValue().putObject(DataInter.Key.KEY_VIDEO_INFO, info);
                        getGroupValue().putString(DataInter.Key.KEY_CURRENT_URL, data.getData());
                        getGroupValue().putString(DataInter.Key.KEY_CURRENT_DEFINITION, data.getTag());
                    }
                }
                break;
            case DataInter.ProviderEvent.EVENT_VIDEO_RATE_DATA:
                if (bundle != null) {
                    VideoPlayList bean = (VideoPlayList) bundle.getSerializable(DataInter.Key.KEY_PROVIDER_PLAY_URL_INFO);
                    if (bean != null) {
                        getGroupValue().putObject(DataInter.Key.KEY_VIDEO_RATE_DATA, bean.getPlayUrlList());
                    }
                }
                break;
        }
    }

    private void recordPlayPosition(int msc) {
        if (mCurrVid > 0)
            MemoryPlayRecorder.recordPlayTime(mCurrVid, msc);
    }

    private void recordPlayPosition() {
        if (getDuration() > 0)
            recordPlayPosition(getCurrentPosition());
    }

    private int getCurrentPosition() {
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        if (playerStateGetter != null)
            return playerStateGetter.getCurrentPosition();
        return 0;
    }

    private int getDuration() {
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        if (playerStateGetter != null)
            return playerStateGetter.getDuration();
        return 0;
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

    @Override
    public void onErrorEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }
}
