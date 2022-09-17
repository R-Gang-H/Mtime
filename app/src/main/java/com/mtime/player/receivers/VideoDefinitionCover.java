package com.mtime.player.receivers;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kk.taurus.playerbase.event.BundlePool;
import com.kk.taurus.playerbase.event.EventKey;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.kk.taurus.playerbase.receiver.PlayerStateGetter;
import com.kotlin.android.app.data.entity.video.VideoPlayUrl;
import com.mtime.R;
import com.mtime.base.utils.MScreenUtils;
import com.mtime.bussiness.video.view.DefinitionItemView;
import com.mtime.player.DataInter;
import com.mtime.player.PlayerHelper;
import com.mtime.player.bean.MTimeVideoData;
import com.mtime.player.bean.VideoInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mtime on 2017/10/18.
 */

public class VideoDefinitionCover extends BaseCover {

    private LinearLayout mDefinitionView;

    private List<VideoPlayUrl> mUrlItems = new ArrayList<>();

    private final int MSG_CODE_DELAY_HIDDEN_RATE_LIST = -123;

    private final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case MSG_CODE_DELAY_HIDDEN_RATE_LIST:
                    setDefinitionState(false);
                    break;
            }
        }
    };

    public VideoDefinitionCover(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        mDefinitionView = findViewById(R.id.definition_view);
        getView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDefinitionState(false);
            }
        });

        getGroupValue().registerOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    @Override
    public void onReceiverUnBind() {
        super.onReceiverUnBind();
        getGroupValue().unregisterOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    private final IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
        @Override
        public String[] filterKeys() {
            return new String[]{DataInter.Key.KEY_VIDEO_RATE_DATA};
        }

        @Override
        public void onValueUpdate(String s, Object o) {
            if(DataInter.Key.KEY_VIDEO_RATE_DATA.equals(s)){
                ArrayList<VideoPlayUrl> urlItems = (ArrayList<VideoPlayUrl>) o;
                refreshView(urlItems);
            }
        }
    };

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onErrorEvent(int i, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public Bundle onPrivateEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case DataInter.ReceiverEvent.EVENT_REQUEST_SHOW_DEFINITION_LIST:
                setDefinitionState(true);
                break;
        }
        return super.onPrivateEvent(eventCode, bundle);
    }

    private void sendDelayHiddenListMsg(){
        removeDelayHiddenListMsg();
        mHandler.sendEmptyMessageDelayed(MSG_CODE_DELAY_HIDDEN_RATE_LIST,5000);
    }

    private void removeDelayHiddenListMsg(){
        mHandler.removeMessages(MSG_CODE_DELAY_HIDDEN_RATE_LIST);
    }

    private void refreshView(ArrayList<VideoPlayUrl> urlItems) {
        this.mUrlItems = urlItems;
    }

    private int getCurrentPosition(){
        PlayerStateGetter playerStateGetter = getPlayerStateGetter();
        return playerStateGetter==null?0:playerStateGetter.getCurrentPosition();
    }

    private void onDefinitionShow(){
        if(mUrlItems==null){
            mUrlItems = getGroupValue().get(DataInter.Key.KEY_VIDEO_RATE_DATA);
        }
        if(mUrlItems==null)
            return;
        mDefinitionView.removeAllViews();
        DefinitionItemView itemView;
        String currentUrl = getGroupValue().getString(DataInter.Key.KEY_CURRENT_URL);
        for(final VideoPlayUrl item : mUrlItems){
            itemView = new DefinitionItemView(getContext());
            itemView.setKey(item.getUrl());
            itemView.setCurrentItemKey(currentUrl);
            itemView.setText(item.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!currentUrl.equals(item.getUrl())){
                        Bundle bundle = BundlePool.obtain();
                        bundle.putSerializable(EventKey.SERIALIZABLE_DATA, buildDataSource(item));
                        requestPlayDataSource(bundle);
                        setDefinitionState(false);
                        PlayerHelper.recordDefinition(String.valueOf(item.getResolutionType()));

                        Bundle obtain = BundlePool.obtain();
                        obtain.putString(EventKey.STRING_DATA, item.getName());
                        obtain.putLong(EventKey.LONG_DATA, item.getFileSize());
                        notifyReceiverEvent(DataInter.ReceiverEvent.EVENT_CODE_DEFINITION_CHANGE, obtain);
                    }
                }
            });
            mDefinitionView.addView(itemView,getParams());
        }
    }

    private MTimeVideoData buildDataSource(VideoPlayUrl item){
        MTimeVideoData data = new MTimeVideoData(item.getUrl());
        data.setStartPos(getCurrentPosition());
        data.setTag(item.getName());
        VideoInfo info = getGroupValue().get(DataInter.Key.KEY_VIDEO_INFO);
        if(info!=null){
            data.setVideoId(info.getVid());
            data.setSource(Integer.parseInt(info.getSourceType()));
        }
        return data;
    }

    private LinearLayout.LayoutParams getParams(){
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int marginValue = MScreenUtils.dp2px(10f);
        params.setMargins(marginValue, marginValue, marginValue, marginValue);
        return params;
    }

    public void setDefinitionState(boolean state){
        setCoverVisibility(state?View.VISIBLE:View.GONE);
        if(state){
            onDefinitionShow();
            sendDelayHiddenListMsg();
        }
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_definition_cover,null);
    }

    @Override
    public int getCoverLevel() {
        return levelMedium(DataInter.CoverLevel.COVER_LEVEL_DEFINITION);
    }
}
