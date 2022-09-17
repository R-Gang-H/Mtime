package com.mtime.player.receivers;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.kk.taurus.playerbase.event.OnPlayerEventListener;
import com.kk.taurus.playerbase.receiver.BaseCover;
import com.kk.taurus.playerbase.receiver.IReceiverGroup;
import com.mtime.R;
import com.mtime.player.DataInter;

/**
 * Created by mtime on 2017/10/11.
 */

public class LoadingCover extends BaseCover {

    private final String TAG = "LoadingCover";

    private final int LOADING_TEXT_TYPE_ON_LOADING = 0;
    private final int LOADING_TEXT_TYPE_RIGHT_NOW_PLAY = 1;

    private ImageView mLoadingIcon;
    private TextView mLoadingText;
    private RotateAnimation mLoadingAnimation;

    public LoadingCover(Context context) {
        super(context);
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_loading_cover,null);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        mLoadingIcon = findViewById(R.id.video_layout_player_loading_icon_iv);
        mLoadingText = findViewById(R.id.video_layout_player_loading_hint_tv);

        mLoadingAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getContext(), R.anim.video_anim_loading);
        mLoadingAnimation.setInterpolator(new LinearInterpolator());

        getGroupValue().registerOnGroupValueUpdateListener(onGroupValueUpdateListener);
    }

    private final IReceiverGroup.OnGroupValueUpdateListener onGroupValueUpdateListener =
            new IReceiverGroup.OnGroupValueUpdateListener() {
        @Override
        public String[] filterKeys() {
            return new String[]{DataInter.Key.KEY_ERROR_SHOW_STATE};
        }

        @Override
        public void onValueUpdate(String s, Object o) {
            if(DataInter.Key.KEY_ERROR_SHOW_STATE.equals(s)){
                if((boolean)o){
                    setLoadingState(false);
                }
            }
        }
    };

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {
        switch (eventCode){
            case OnPlayerEventListener.PLAYER_EVENT_ON_PROVIDER_DATA_START:
                Log.d(TAG,"on url loading......");
                switchLoadingText(LOADING_TEXT_TYPE_RIGHT_NOW_PLAY);
                setLoadingState(true);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_DATA_SOURCE_SET:
                Log.d(TAG,"on set data source......");
                switchLoadingText(LOADING_TEXT_TYPE_RIGHT_NOW_PLAY);
                setLoadingState(true);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_VIDEO_RENDER_START:
                Log.d(TAG,"on render start......");
                setLoadingState(false);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_SEEK_TO:
                switchLoadingText(LOADING_TEXT_TYPE_ON_LOADING);
                break;
            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_START:
                Log.d(TAG,"buffering start......");
                switchLoadingText(LOADING_TEXT_TYPE_ON_LOADING);
                setLoadingState(true);
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_BUFFERING_END:
                Log.d(TAG,"buffering end......");
                setLoadingState(false);
                break;

            case OnPlayerEventListener.PLAYER_EVENT_ON_STOP:
                Log.d(TAG,"on stopped......");
                setLoadingState(false);
                break;
        }
    }

    @Override
    public void onErrorEvent(int i, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int i, Bundle bundle) {

    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        mLoadingIcon.clearAnimation();
        mLoadingIcon.post(new Runnable() {
            @Override
            public void run() {
                mLoadingIcon.startAnimation(mLoadingAnimation);
            }
        });
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        mLoadingIcon.clearAnimation();
        setLoadingState(false);
    }

    private boolean isErrorState(){
        return getGroupValue()!=null && getGroupValue().getBoolean(DataInter.Key.KEY_ERROR_SHOW_STATE);
    }

    public void setLoadingState(boolean state) {
        if(isErrorState()){
            setCoverVisibility(View.GONE);
            return;
        }
        setCoverVisibility(state?View.VISIBLE:View.GONE);
    }

    private void switchLoadingText(int type){
        switch (type){
            case LOADING_TEXT_TYPE_RIGHT_NOW_PLAY:
                setLoadingText(getString(R.string.video_loading_hint));
                break;
            default:
            case LOADING_TEXT_TYPE_ON_LOADING:
                setLoadingText(getString(R.string.video_loading_hint_new));
                break;
        }
    }

    private String getString(int id){
        return getContext().getString(id);
    }

    public void setLoadingText(String text) {
        mLoadingText.setText(text);
    }

    @Override
    public int getCoverLevel() {
        return levelLow(DataInter.CoverLevel.COVER_LEVEL_LOADING);
    }
}
