package com.mtime.player.receivers;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.kk.taurus.playerbase.receiver.BaseCover;
import com.mtime.R;
import com.mtime.player.DataInter;
import com.mtime.player.ReceiverGroupManager;

/**
 * Created by mtime on 2017/10/30.
 */

public class UserGuideCover extends BaseCover {

    public static final String KEY = "GuideCover";

    private final Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

        }
    };

    public UserGuideCover(Context context) {
        super(context);
    }

    @Override
    public void onReceiverBind() {
        super.onReceiverBind();
        View mConfirmView = findViewById(R.id.lla_i_know);
        mConfirmView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setGuideState(false);
            }
        });
    }

    private void setGuideState(boolean state){
        setCoverVisibility(state?View.VISIBLE:View.GONE);
        if(state){
            mHandler.removeCallbacks(mDelayHiddenGuideCover);
            mHandler.postDelayed(mDelayHiddenGuideCover, 5000);
        }else{
            ReceiverGroupManager.updateUserGuideIKnow(getContext());
            mHandler.removeCallbacks(mDelayHiddenGuideCover);
        }
        getGroupValue().putBoolean(DataInter.Key.KEY_USER_GUIDE_STATE, state);
    }

    @Override
    protected void onCoverAttachedToWindow() {
        super.onCoverAttachedToWindow();
        setGuideState(true);
    }

    @Override
    protected void onCoverDetachedToWindow() {
        super.onCoverDetachedToWindow();
        mHandler.removeCallbacks(mDelayHiddenGuideCover);
        getGroupValue().putBoolean(DataInter.Key.KEY_USER_GUIDE_STATE, false);
    }

    @Override
    public View onCreateCoverView(Context context) {
        return View.inflate(context, R.layout.layout_player_user_guide_cover,null);
    }

    @Override
    public void onPlayerEvent(int eventCode, Bundle bundle) {

    }

    @Override
    public void onErrorEvent(int i, Bundle bundle) {

    }

    @Override
    public void onReceiverEvent(int i, Bundle bundle) {

    }

    private final Runnable mDelayHiddenGuideCover = new Runnable() {
        @Override
        public void run() {
            setGuideState(false);
        }
    };

    @Override
    public int getCoverLevel() {
        return levelHigh(DataInter.CoverLevel.COVER_LEVEL_USER_GUIDE);
    }
}
